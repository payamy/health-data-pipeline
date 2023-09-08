package com.payamy.etl.job;

import com.payamy.etl.annotation.After;
import com.payamy.etl.annotation.Before;
import com.payamy.etl.annotation.ETL;
import com.payamy.etl.annotation.Component;
import com.payamy.etl.config.ConfigManager;
import com.payamy.etl.connector.broker.KafkaConnector;
import com.payamy.etl.connector.db.CassandraConnector;
import com.payamy.etl.job.cache.UserCache;
import com.payamy.etl.session.LogLevel;
import com.payamy.etl.session.SessionManager;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

@Component
public class StreamJob {

    private KafkaConnector kafkaConnector;

    private String keyspace;

    private ConfigManager configManager;

    private UserCache userCache;

    @Before
    public void setUpJob() throws ConfigurationException, ClassNotFoundException, TimeoutException {

        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.setLogLevel(LogLevel.WARN);
        sessionManager.getOrCreateSession();

        configManager = ConfigManager.getInstance();
        String[] bootstrapServers = configManager.getList("kafka.bootstrapServers");

        kafkaConnector = new KafkaConnector(
                bootstrapServers,
                "aggregated");
        CassandraConnector cassandraConnector = new CassandraConnector();
        cassandraConnector.createConnection();

        keyspace = configManager.getString("cassandra.keyspace");

        userCache = new UserCache(
                bootstrapServers,
                "postgres.public.users",
                "userCache");
        userCache.streamCache();
    }

    @ETL
    public void myJob() throws TimeoutException, InterruptedException {
        Dataset<Row> df = kafkaConnector.read();

        df = df.selectExpr("CAST(value AS STRING)")
                .select(
                        get_json_object(col("value"), "$.eventName").alias("eventName"),
                        get_json_object(col("value"), "$.userId").alias("userId"),
                        get_json_object(col("value"), "$.payload").alias("payload"),
                        get_json_object(col("value"), "$.sentAt").alias("sentAt")
                )
                .select(
                        col("eventName"),
                        col("userId"),
                        get_json_object(col("payload"), "$.step").alias("step").cast("integer"),
                        get_json_object(col("payload"), "$.distance").alias("distance").cast("integer"),
                        get_json_object(col("payload"), "$.heartbeat").alias("heartbeat").cast("float"),
                        get_json_object(col("payload"), "$.temperature").alias("temperature").cast("float"),
                        get_json_object(col("payload"), "$.oxygen").alias("oxygen").cast("float"),
                        to_timestamp(col("sentAt").cast("double").divide(lit(1000))).alias("sentAt")
                );

        boolean isCachesAvailable = false;
        Dataset<Row> userTable = null;
        while (!isCachesAvailable) {
            try {
                userTable = userCache.getStreamCache();
                isCachesAvailable = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());

                TimeUnit.SECONDS.sleep(2);
            }
        }
        userTable = userTable.withColumnRenamed("eyeColor", "eye_color")
                .withColumnRenamed("bloodType", "blood_type");

        Dataset<Row> dfAgg = df.withWatermark("sentAt", "10 minutes")
                        .dropDuplicates()
                        .groupBy(
                                window(col("sentAt"), "5 minutes", "5 minutes"),
                                col("userId")
                        ).agg(
                                sum(col("step")).alias("step"),
                                sum(col("distance")).alias("distance"),
                                avg(col("heartbeat")).alias("heartbeat"),
                                avg(col("temperature")).alias("temperature"),
                                avg(col("oxygen")).alias("oxygen")
                        ).withColumn("datetime", col("window.start")
                        ).drop(col("window")
                );

        String checkpointRoot = configManager.getString("etl.checkpoint.root");

        userTable.writeStream()
                .outputMode("append")
                .queryName("user")
                .option("checkpointLocation", checkpointRoot + "user")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", keyspace)
                .option("table", "user")
                .queryName("userTable")
                .start();

        dfAgg.writeStream()
                .outputMode("update")
                .queryName("healthByUser")
                .option("checkpointLocation", checkpointRoot + "health_by_user")
                .foreachBatch(
                        (VoidFunction2<Dataset<Row>, Long>) ( miniBatch, batchId ) -> {
                            Dataset<Row> staticUserTable = userCache.getCache();
                            miniBatch.alias("df").join(
                                    staticUserTable.alias("user"),
                                    miniBatch.col("userId")
                                            .equalTo(staticUserTable.col("id")),
                                    "inner"
                            ).select(
                                    col("df.userId").alias("userid"),
                                    col("user.blood_type").alias("blood_type"),
                                    col("user.eye_color").alias("eye_color"),
                                    col("df.step").alias("step"),
                                    col("df.distance").alias("distance"),
                                    col("df.heartbeat").alias("heartbeat"),
                                    col("df.temperature").alias("temperature"),
                                    col("df.oxygen").alias("oxygen"),
                                    col("df.datetime").alias("datetime")
                            ).write()
                                    .format("org.apache.spark.sql.cassandra")
                                    .mode("append")
                                    .option("keyspace", "stream_etl")
                                    .option("table", "health_by_user")
                                    .save();
                        }
                )
                .trigger(Trigger.ProcessingTime(60000))
                .start();
    }

    @After
    public void closeSession() throws StreamingQueryException {
        SparkSession spark = SessionManager.getInstance().getOrCreateSession();
        spark.streams().awaitAnyTermination();
    }
}
