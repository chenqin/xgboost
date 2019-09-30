/*
 Copyright (c) 2014 by Contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package ml.dmlc.xgboost4j.scala.spark.params

import org.apache.spark.ml.param._

private[spark] trait RabitParams extends Params {
  /**
   * Rabit worker configurations. These parameters were passed to Rabit.Init and decide
   * rabit_reduce_ring_mincount - threshold of enable ring based allreduce/broadcast operations.
   * rabit_reduce_buffer - buffer size to recv and run reduction
   * rabit_bootstrap_cache - enable save allreduce cache before loadcheckpoint
   * rabit_debug - enable more verbose rabit logging to stdout
   * DMLC_WORKER_CONNECT_RETRY - number of retrys to tracker
   */
  final val ringReduceMin = new IntParam(this, "rabit_reduce_ring_mincount",
    "minimal counts of enable allreduce/broadcast with ring based topology",
    ParamValidators.gtEq(1))

  final def getRingReduceMin: Int = $(ringReduceMin)

  final def reduceBuffer: Param[String] = new Param[String](this, "rabit_reduce_buffer",
    "buffer size (MB/GB) allocated to each xgb trainner recv and run reduction",
    (buf: String) => buf.contains("MB") || buf.contains("GB"))

  final def getReduceBuffer: String = ${reduceBuffer}

  final def bootstrapCache: IntParam = new IntParam(this, "rabit_bootstrap_cache",
    "enable save allreduce cache before loadcheckpoint, used to allow failed task retry",
    (cache: Int) => cache == 0 || cache == 1)

  final def getBootstrapCache: Int = ${bootstrapCache}

  final def rabitDebug: IntParam = new IntParam(this, "rabit_debug",
    "enable more verbose rabit logging to stdout", (debug: Int) => debug == 0 || debug == 1)

  final def getRabitDebug: Int = ${rabitDebug}

  final def connectRetry: IntParam = new IntParam(this, "DMLC_WORKER_CONNECT_RETRY",
    "number of retry worker do before fail", ParamValidators.gtEq(1))

  final def getConnectRetry: Int = ${connectRetry}

  setDefault(ringReduceMin -> (32 << 10), reduceBuffer -> "256MB", bootstrapCache -> 0,
    rabitDebug -> 0, connectRetry -> 5)

  def XGBoostToRabitParams(xgboostParams: Map[String, Any]): Unit = {
    for ((paramName, paramValue) <- xgboostParams) {
      val name = paramName
      params.find(_.name == name).foreach {
        case _: IntParam =>
          set(name, paramValue.toString.toInt)
        case _: Param[String] =>
          set(name, paramValue.toString)
      }
    }
  }

  def RabitParamsToXGBoost: Map[String, String] = Map(
      "rabit_reduce_ring_mincount" -> getRingReduceMin.toString,
      "rabit_reduce_buffer" -> getReduceBuffer.toString,
      "rabit_bootstrap_cache" -> getBootstrapCache.toString,
      "rabit_debug" -> getRabitDebug.toString,
      "DMLC_WORKER_CONNECT_RETRY" -> getConnectRetry.toString
    )
}
