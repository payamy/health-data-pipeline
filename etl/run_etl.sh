#!/bin/bash

/home/spark/bin/spark-submit \
--master spark://spark-master:7077 \
--total-executor-cores 4 \
--driver-memory 1G \
--executor-memory 2G \
--packages \
org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.2,com.datastax.cassandra:cassandra-driver-core:3.11.3,commons-configuration:commons-configuration:1.6,\
io.delta:delta-core_2.12:2.3.0,com.datastax.spark:spark-cassandra-connector_2.12:3.3.0,com.codahale.metrics:metrics-core:3.0.2 \
--conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
--conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
--class com.payamy.etl.Main \
etl-0.0.1.jar
