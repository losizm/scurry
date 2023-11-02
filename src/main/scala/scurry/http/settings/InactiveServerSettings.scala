/*
 * Copyright 2023 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scurry.http
package settings

private[scurry] class InactiveServerSettings(settings: JMap[String, AnyRef]) extends ServerSettings with ServerSettingsConverter:
  private lazy val host = settings.requireHost()
  private lazy val port = settings.requirePort()
  private lazy val backlogSize = settings.optionInt("backlogSize").getOrElse(0)
  private lazy val queueSize = settings.optionInt("queueSize").getOrElse(0)
  private lazy val poolSize = settings.optionInt("poolSize").getOrElse(0)
  private lazy val bufferSize = settings.optionInt("bufferSize").getOrElse(0)
  private lazy val readTimeout = settings.optionInt("readTimeout").getOrElse(0)
  private lazy val headerLimit = settings.optionInt("headerLimit").getOrElse(0)
  private lazy val keepAlive = settings.optionKeepAlive()
  private lazy val ssl = settings.optionSsl()

  def isSslEnabled(): Boolean =
    ssl.nonEmpty

  def getHost(): String =
    host.getHostAddress

  def getPort(): Int =
    port

  def getBacklogSize(): Int =
    backlogSize

  def getQueueSize(): Int =
    queueSize

  def getPoolSize(): Int =
    poolSize

  def getBufferSize(): Int =
    bufferSize

  def getReadTimeout(): Int =
    readTimeout

  def getHeaderLimit(): Int =
    headerLimit

  def isKeepAliveEnabled(): Boolean =
    keepAlive.nonEmpty
