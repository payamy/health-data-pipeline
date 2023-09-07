# the first stage of our build will use a maven 3.6.1 parent image
FROM maven:3.6.1-jdk-8-alpine AS MAVEN_BUILD
# copy the pom and src code to the container
COPY ./src ./src
COPY ./pom.xml ./pom.xml
# package our application code
RUN mvn clean package


# the second stage of our build will use open jdk 8 on alpine 3.9
FROM openjdk:8-jre-alpine3.9
# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /target/etl-0.0.1-jar-with-dependencies.jar /etl-0.0.1.jar

ENV SPARK_VERSION=3.3.3 \
HADOOP_VERSION=3

# DOWNLOAD SPARK AND INSTALL
RUN DOWNLOAD_URL_SPARK="https://dlcdn.apache.org/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz" \
    && wget -O apache-spark.tgz  "${DOWNLOAD_URL_SPARK}"\
    && mkdir -p /home/spark \
    && tar -xf apache-spark.tgz -C /home/spark --strip-components=1 \
    && rm apache-spark.tgz

RUN rm /home/spark/jars/guava-14.0.1.jar
RUN apk add --no-cache bash


# SET SPARK ENV VARIABLES
# ENV SPARK_HOME="/home/spark"
# ENV PATH="${SPARK_HOME}/bin/:${PATH}"

CMD ["/home/spark/bin/spark-submit", \
     "--master", "spark://spark-master:7077", \
     "--total-executor-cores", \
     "4", \
     "--driver-memory", \
     "1G", \
     "--executor-memory", \
     "2G", \
     "--packages", \
     "org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.2,com.datastax.cassandra:cassandra-driver-core:3.11.3,commons-configuration:commons-configuration:1.6,io.delta:delta-core_2.12:2.3.0,com.datastax.spark:spark-cassandra-connector_2.12:3.3.0,com.codahale.metrics:metrics-core:3.0.2", \
     "--conf", \
     "\"spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension\"", \
     "--conf", \
     "\"spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog\"", \
     "--class", \
     "com.payamy.etl.Main", \
     "/etl-0.0.1.jar"]
