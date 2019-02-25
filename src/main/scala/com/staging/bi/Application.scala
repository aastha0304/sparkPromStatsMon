package com.staging.bi

import org.apache.spark.scheduler.{SparkListener, SparkListenerStageCompleted}
import org.apache.log4j.LogManager
import org.apache.spark.source.DistinctAccumulatorSource
import org.apache.spark.sql.SparkSession
import org.apache.spark.source.PublicLongAccumulatorSource


object Application {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .config("spark.metrics.conf.*.sink.statsd.class",
        "org.apache.spark.metrics.sink.StatsdSink")
      .config("spark.metrics.conf.*.sink.statsd.port", 9125)
      .config("spark.metrics.conf.*.sink.statsd.prefix", "spark")
      .appName("MetricsDemo")
      .getOrCreate()

    val sc = spark.sparkContext

    val acc = sc.longAccumulator("long-metric")
    PublicLongAccumulatorSource.register(sc, List("spark_distinct_add_count" -> acc).toMap)

    //sc.collectionAccumulator[String]("distinct-metrics")
    //DistinctAccumulatorSource.register(sc, List("distinct-metric" -> acc).toMap)

    val df = spark.read.format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")
      .option("header", "true")
      .load("listings.csv")

    df.show

    import spark.implicits._

    val distinctListings = df.select($"listing_url").map(_.toString).distinct

    distinctListings.foreach( _ => acc.add(1) )

    val myListener = new CustomListener
    sc.addSparkListener(myListener)

    val date = spark.read.parquet("abcd").agg(max($"report_date")).show()

  }
}


class CustomListener extends SparkListener  {
  val logger = LogManager.getLogger("CustomListener")

  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    logger.warn(s"Stage completed, runTime: ${stageCompleted.stageInfo.taskMetrics.executorRunTime}, " +
      s"cpuTime: ${stageCompleted.stageInfo.taskMetrics.executorCpuTime}")

  }
}