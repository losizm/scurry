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

import com.typesafe.config.{ Config, ConfigFactory }

import java.io.File

import scamper.http.server.{ HttpServer, ServerApplication }

object TestServerFactory:
  private val config = ConfigFactory.load()

  def createServer(): HttpServer =
    createServer(config)

  def createServer(config: Config): HttpServer =
    ServerApplication()
      .logger(config.getString("test.server.logger"))
      .backlogSize(config.getInt("test.server.backlogSize"))
      .queueSize(config.getInt("test.server.queueSize"))
      .poolSize(config.getInt("test.server.poolSize"))
      .bufferSize(config.getInt("test.server.bufferSize"))
      .readTimeout(config.getInt("test.server.readTimeout"))
      .headerLimit(config.getInt("test.server.headerLimit"))
      .enableKeepAlive(config)
      .enableSecure(config)
      .addMessenger(config)
      .toHttpServer(config.getString("test.server.host"), config.getInt("test.server.port"))

  extension (app: ServerApplication)
    private def enableSecure(config: Config): ServerApplication =
      if config.getBoolean("test.server.secure.enabled") then
        app.secure(
          File(config.getString("test.server.secure.key")),
          File(config.getString("test.server.secure.cert"))
        )
      app

    private def enableKeepAlive(config: Config): ServerApplication =
      if config.getBoolean("test.server.keepAlive.enabled") then
        app.keepAlive(
          config.getInt("test.server.keepAplive.timeout"),
          config.getInt("test.server.keepAplive.max")
        )
      app

    private def addMessenger(config: Config): ServerApplication =
      app.route(config.getString("test.server.messenger.mountPath")) { MessengerWebSocket(config) }
