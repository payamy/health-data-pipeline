package com.payamy.etl.cache;

import com.payamy.etl.config.ConfigManager;
import com.payamy.etl.connector.broker.KafkaConnector;
import com.payamy.etl.session.SessionManager;
import io.delta.tables.DeltaTable;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public abstract class Cache<T extends Serializable> {

    protected Dataset<T> ds;
    public final Encoder<T> encoder;

    private final StructType payloadSchema;

    private final String sourceTopic;
    private final String cacheName;
    private final String[] bootstrapServers;

    private final String cacheRoot;
    private final String cacheCheckpoint;

    private String identityColumn;

    public Cache(String[] bootstrapServers, String sourceTopic, String cacheName)
            throws ConfigurationException, ClassNotFoundException {
        this.bootstrapServers = bootstrapServers;
        this.sourceTopic = sourceTopic;
        this.cacheName = cacheName;
        this.payloadSchema = DataTypes.createStructType(new StructField[] {
                DataTypes.createStructField("before", DataTypes.StringType, false),
                DataTypes.createStructField("after", DataTypes.StringType, false),
                DataTypes.createStructField("source", DataTypes.StringType, false),
                DataTypes.createStructField("ts_ms", DataTypes.StringType, false),
                DataTypes.createStructField("transaction", DataTypes.StringType, false),
        });

        ConfigManager cm = ConfigManager.getInstance();
        this.cacheRoot = cm.getString("cache.root");
        this.cacheCheckpoint = cm.getString("cache.checkpoint");
        this.identityColumn = "id";
        this.encoder = Encoders.bean(getGenericClass());
    }

    public final void setIdentityColumn(String value) {
        this.identityColumn = value;
    }

    private KafkaConnector getConnector() {
        return new KafkaConnector(this.bootstrapServers, this.sourceTopic);
    }

    private void createDataFrame() throws ClassNotFoundException {

        Dataset<Row> df = getConnector().read();

        df = df.selectExpr("CAST(value AS STRING)")
                .select(get_json_object(col("value"), "$.payload").alias("payload"))
                .select(from_json(col("payload"), payloadSchema).alias("payload"));

        Field[] fields = getGenericClass().getDeclaredFields();

        for (Field field: fields) {
            String[] fieldSplit = field.toString().split(" ");

            String[] fieldTypeSplit = fieldSplit[1].split("\\.");
            String fieldType = fieldTypeSplit[fieldTypeSplit.length - 1].toLowerCase();
            String[] fieldNameSplit = fieldSplit[2].split("\\.");
            String fieldName = fieldNameSplit[fieldNameSplit.length - 1];

            df = df.withColumn(fieldName,
                    get_json_object(col("payload.after"), "$." + fieldName)
                            .cast(fieldType));
        }

        df = df.drop(col("payload"));

        ds = df.as(encoder);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getGenericClass() throws ClassNotFoundException {
        String className = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
        Class<?> clazz = Class.forName(className);
        return (Class<T>) clazz;
    }

    public final void streamCache() throws ClassNotFoundException, TimeoutException {
        createDataFrame();
        manipulateDataFrame();
        writeCache();
    }

    private void writeCache() throws TimeoutException {
        ds.writeStream()
                .format("delta")
                .foreachBatch((VoidFunction2<Dataset<T>, Long>) ( miniBatch, batchId ) -> {
                    try {
                        DeltaTable deltaTable = DeltaTable.forPath(cacheRoot + cacheName);
                        deltaTable.alias("t").merge(
                                        miniBatch.toDF().alias("s"),
                                        String.format("s.%s = t.%s", identityColumn, identityColumn))
                                .whenMatched().updateAll()
                                .whenNotMatched().insertAll()
                                .execute();
                    }
                    catch (Exception exception) {
                        if (exception.getMessage().contains("is not a Delta table")) {
                            miniBatch.write().format("delta").save(cacheRoot + cacheName);
                        }
                    }
                })
                .option("checkpointLocation", cacheCheckpoint + cacheName)
                .outputMode("update")
                .queryName(cacheName)
                .start();
    }

    public final Dataset<Row> getStreamCache() {
        SparkSession spark = SessionManager.getInstance().getOrCreateSession();
        return spark.readStream().format("delta")
                .load(cacheRoot + cacheName);
    }

    public final Dataset<Row> getCache() {
        SparkSession spark = SessionManager.getInstance().getOrCreateSession();
        return spark.read().format("delta")
                .load(cacheRoot + cacheName);
    }

    protected void manipulateDataFrame() {
    }
}

