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
COPY ./run_etl.sh ./run_etl.sh
RUN chmod +x ./run_etl.sh

CMD ["./run_etl.sh"]
