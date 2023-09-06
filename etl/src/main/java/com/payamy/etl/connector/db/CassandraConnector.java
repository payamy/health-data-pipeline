package com.payamy.etl.connector.db;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.payamy.etl.config.ConfigManager;
import org.apache.commons.configuration.ConfigurationException;

public class CassandraConnector {

    private Session session;
    private Cluster cluster;

    private boolean isEstablished;

    private String node;

    private Integer port;

    private Integer replicationFactor;

    private void setConfig() throws ConfigurationException {
        ConfigManager configManager = ConfigManager.getInstance();
        node = configManager.getString("cassandra.node");
        port = configManager.getInt("cassandra.port");
        replicationFactor = configManager.getInt("cassandra.replicationFactor");
    }

    private void connect() {
        Cluster.Builder builder = Cluster.builder().addContactPoint(node);
        if (port != null) {
            builder.withPort(port);
        }
        cluster = builder.build();

        session = cluster.connect();
    }

    public Session getSession() {
        return this.session;
    }

    public void close() {
        session.close();
        cluster.close();
    }

    private void createKeyspace() {

        String query = "CREATE KEYSPACE IF NOT EXISTS stream_etl" +
                " WITH replication = {'class':'SimpleStrategy', 'replication_factor':" +
                replicationFactor +
                "};";
        session.execute(query);
    }

    private void createTableUser() {
        String query = "CREATE TABLE IF NOT EXISTS " +
                "stream_etl.user(" +
                "id bigint," +
                "username text," +
                "name text," +
                "blood_type text," +
                "eye_color text," +
                "PRIMARY KEY (id)" +
                ");";
        session.execute(query);
    }

    private void createTableHealthByUser() {
        String query = "CREATE TABLE IF NOT EXISTS " +
                "stream_etl.health_by_user(" +
                "userid bigint," +
                "blood_type text," +
                "eye_color text," +
                "datetime timestamp," +
                "step int," +
                "distance int," +
                "heartbeat float," +
                "temperature float," +
                "oxygen float," +
                "PRIMARY KEY ((userid), datetime)" +
                ");";
        String bloodTypeIndex = "CREATE INDEX IF NOT EXISTS ON " +
                "stream_etl.health_by_user(blood_type)";
        String eyeColorIndex = "CREATE INDEX IF NOT EXISTS ON " +
                "stream_etl.health_by_user(eye_color)";
        session.execute(query);
        session.execute(bloodTypeIndex);
        session.execute(eyeColorIndex);
    }

    public void createConnection() throws ConfigurationException {
        if (!isEstablished) {
            isEstablished = true;
            setConfig();
            connect();
            createKeyspace();
            createTableUser();
            createTableHealthByUser();
        }
    }
}
