package com.payamy.etl.session;

import org.apache.spark.sql.SparkSession;

public class SessionManager {

    private static SessionManager sessionManager;
    private SparkSession spark;

    private String jobId;
    private String sparkMaster;
    private LogLevel logLevel;

    public void setJobId( String jobId ) {
        this.jobId = jobId;
    }

    public void setSparkMaster( String sparkMaster ) {
        this.sparkMaster = sparkMaster;
    }

    public void setLogLevel( LogLevel logLevel ) {
        this.logLevel = logLevel;
    }

    private SessionManager() {

    }

    public SparkSession getOrCreateSession() {
        if (spark != null) {
            return spark;
        }
        spark = SparkSession
                .builder()
                .appName(this.jobId == null ? "Spark Application" : this.jobId)
                .master(this.sparkMaster == null ? "local[*]" : this.sparkMaster)
                .getOrCreate();

        spark.sparkContext().setLogLevel(this.logLevel == null ? "INFO" : this.logLevel.toString());

        return spark;
    }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }

        return sessionManager;
    }
}

