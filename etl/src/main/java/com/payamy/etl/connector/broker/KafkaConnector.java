package com.payamy.etl.connector.broker;

import com.payamy.etl.session.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class KafkaConnector {

    private final SparkSession spark;
    private final String kafkaTopic;
    private final String bootstrapServer;

    public KafkaConnector(String bootstrapServer, String kafkaTopic) {
        this.spark = SessionManager.getInstance().getOrCreateSession();
        this.kafkaTopic = kafkaTopic;
        this.bootstrapServer = bootstrapServer;
    }

    public KafkaConnector(String[] bootstrapServer, String kafkaTopic) {
        this.spark = SessionManager.getInstance().getOrCreateSession();
        this.kafkaTopic = kafkaTopic;
        this.bootstrapServer = StringUtils.join(bootstrapServer, ",");
    }

    public Dataset<Row> read() {
        return spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", bootstrapServer)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "earliest")
                .load();
    }
}
