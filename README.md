
# Health Data Pipeline

In this project I have implemented a data pipeline. 
From architectural point of view, this project is developed using Kappa architecture 
As you may know, Kappa architecture consists of two main layers:

- Speed Layer
- Serving Layer

How the pipeline works?

- The client (written in Python) sends two types of random data to the server:
    - Heartbeat data which is consisted of heartbeats, body temperature and body oxygen.
    - Jogging data which is users steps and the distance user navigated.
- The server is consisted of two main parts:
    - Authentication: which is implemented using REST APIs. Users data will be stored on PostgreSQL database and after that, the data will be pushed to a Kafka topic using Debezium.
    - Producer: which recieves data from clients and then push it to the Kafka topics. this part is implemented using WebSockets.
- The ETL which is written in Java Spark. ETL program consumes data from Kafka topics. the processed data will be loaded into Cassandra tables. Also, Delta Lake is used to cache CDC data. the ETL is using HDFS for storing real-time states.

Here is a list of technologies used in this project
- Java (as the programming language)
- Python (for the client code)
- Spring Boot
- PostgreSQL (Server database)
- Redis (Server cache)
- Spark
- Kafka
- Debezium
- HDFS
- Cassandra
