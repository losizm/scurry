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

import scamper.http.server.HttpServer as ScamperHttpServer

private[scurry] class ActiveServerSettings(server: ScamperHttpServer) extends ServerSettings:
  def isSslEnabled(): Boolean = server.isSecure
  def getHost(): String = server.host.getHostAddress
  def getPort(): Int = server.port
  def getBacklogSize(): Int = server.backlogSize
  def getQueueSize(): Int = server.queueSize
  def getPoolSize(): Int = server.poolSize
  def getBufferSize(): Int = server.bufferSize
  def getReadTimeout(): Int = server.readTimeout
  def getHeaderLimit(): Int = server.headerLimit
  def isKeepAliveEnabled(): Boolean = server.keepAlive.nonEmpty
