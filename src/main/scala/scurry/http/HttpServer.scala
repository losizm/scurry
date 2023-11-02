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

import java.util.concurrent.atomic.AtomicReference

import settings.*

/** Defines HTTP server. */
class HttpServer private[scurry] (app: RealServerApplication, inactiveSettings: InactiveServerSettings) extends Router(app):
  /**
   * Creates HTTP server using supplied settings.
   *
   * ### Server Settings
   *
   * The following provides example settings:
   *
   * {{{
   *  [
   *    host: "localhost",
   *    port: 8443,
   *    ssl: [
   *      key: "/private/ssl/server.key",
   *      certificate: "/private/ssl/server.crt"
   *    ],
   *    logger: "HttpServer", // i.e., logger name
   *    backlogSize: 50,
   *    queueSize: 32,
   *    poolSize: 8,
   *    bufferSize: 8192,
   *    readTimeout: 1000,
   *    headerLimit: 100,
   *    keepAlive: [
   *     timeout: 30,
   *     max: 5
   *    ]
   *  ]
   * }}}
   *
   * @param settings server settings
   */
  def this(settings: JMap[String, AnyRef]) =
    this(toRealServerApplication(settings), InactiveServerSettings(settings))

  private val httpServer = AtomicReference[RealHttpServer]()
  private val currentSettings = AtomicReference[ServerSettings](inactiveSettings)

  /**
   * Gets server settings.
   *
   * @note If server is not running, the returned settings do not necessarily
   * represent the settings when server is started. That is, values that are not
   * explicitly provided are eventually given default values.
   */
  def getSettings(): ServerSettings =
    currentSettings.get

  /** Tests for running. */
  def isRunning(): Boolean = synchronized {
    httpServer.get match
      case null   => false
      case server => ! server.isClosed
  }

  /** Starts server. */
  def start(): Unit = synchronized {
    if httpServer.get == null || httpServer.get.isClosed then
      val server = app.toHttpServer(inactiveSettings.getHost(), inactiveSettings.getPort())
      httpServer.set(server)
      currentSettings.set(ActiveServerSettings(server))
  }

  /** Stops server. */
  def stop(): Unit = synchronized {
    val server = httpServer.get
    if server != null then
      server.close()
      httpServer.set(null)
      currentSettings.set(inactiveSettings)
  }
