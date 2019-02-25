package org.apache.spark.source

import org.apache.spark.metrics.source.Source
import com.codahale.metrics.{Gauge, MetricRegistry}
import org.apache.spark.SparkContext
import org.apache.spark.util.{AccumulatorV2, CollectionAccumulator, LongAccumulator}

class CustomBIMetricsSource extends Source{

  private val registry = new MetricRegistry
  protected def register[T](accumulators: Map[String, AccumulatorV2[_, T]]): Unit = {
    accumulators.foreach {
      case (name, accumulator) =>
        val gauge = new Gauge[T] {
          override def getValue: T = accumulator.value
        }
        registry.register(MetricRegistry.name(name), gauge)
    }
  }

  override def sourceName: String = "CustomBISource"
  override def metricRegistry: MetricRegistry = registry
}

class DistinctAccumulatorSource extends CustomBIMetricsSource

object DistinctAccumulatorSource extends CustomBIMetricsSource {
  def register(sc: SparkContext, accumulators: Map[String, CollectionAccumulator[String]]): Unit = {
    val source = new DistinctAccumulatorSource
    source.register(accumulators)
    sc.env.metricsSystem.registerSource(source)
  }
}

class PublicLongAccumulatorSource extends CustomBIMetricsSource

object PublicLongAccumulatorSource {
  def register(sc: SparkContext, accumulators: Map[String, LongAccumulator]): Unit = {
    val source = new PublicLongAccumulatorSource
    source.register(accumulators)
    sc.env.metricsSystem.registerSource(source)
  }
}