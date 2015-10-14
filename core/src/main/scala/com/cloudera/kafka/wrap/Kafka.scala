/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.kafka.wrap

import java.util.Properties

import kafka.utils.Logging
import org.apache.kafka.common.config.SslConfigs

import scala.sys.process.Process

object Kafka extends Logging {

  val SslPasswordParams: Array[String] = Array(
    SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
    SslConfigs.SSL_KEY_PASSWORD_CONFIG,
    SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)

  def exec(command: String): String = {
    Process(command)!!
  }

  def generateSslPasswordsOverrides(serverProps: Properties): Array[String] = {
    SslPasswordParams.flatMap(key => {
      val value = serverProps.getProperty(key + ".generator")
      if (value != null) {
        try {
          val overrideString = Some(s"--override $key=${exec(value)}")
          debug(s"Generated password for $key")
          overrideString
        } catch {
          case e: Exception =>
            error(s"Failed to generate password for $key.\n$e")
            None
        }
      }
      else
        None
    })
  }

  def main(args: Array[String]): Unit = {
    try {
      val serverProps = kafka.Kafka.getPropsFromArgs(args)
      val sslPasswordsOverrides = generateSslPasswordsOverrides(serverProps)
      val argsWithOverrides: Array[String] = args ++ sslPasswordsOverrides.flatMap(_.split(" "))

      kafka.Kafka.main(argsWithOverrides)
    }
    catch {
      case e: Throwable =>
        fatal(e)
        System.exit(1)
    }
    System.exit(0)
  }
}
