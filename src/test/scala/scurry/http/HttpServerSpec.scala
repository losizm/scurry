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

import groovy.lang.GroovyShell

import java.io.File

class HttpServerSpec extends org.scalatest.flatspec.AnyFlatSpec:
  self =>

  private val sh = GroovyShell()
  sh.setVariable("KB", 1024)
  sh.setVariable("MB", 1024 * 1024)
  sh.setVariable("resources", File("./src/test/resources"))
  sh.setVariable("log", new AnyRef { def info(message: String): Unit = self.info(message) })

  it should "send and receive messages from greet server" in withGroovyServer("greet-server", "localhost", 0) { server =>
    val settings = server.getSettings()
    val host = settings.getHost()
    val port = settings.getPort()
    val sslEnabled = settings.isSslEnabled()

    info(s"server is running at $host:$port (sslEnabled=$sslEnabled)")
    val script = ScriptFile("greet-client")
    sh.run(script, Array(host, port.toString, sslEnabled.toString))
  }

  it should "send and receive messages from messenger websocket server" in withGroovyServer("messenger-server", "localhost", 0) { server =>
    val settings = server.getSettings()
    val host = settings.getHost()
    val port = settings.getPort()
    val sslEnabled = settings.isSslEnabled()

    info(s"server is running at $host:$port (sslEnabled=$sslEnabled)")
    val script = ScriptFile("messenger-client")
    sh.run(script, Array(host, port.toString, sslEnabled.toString))
  }

  private def withGroovyServer[T](name: String, host: String, port: Int)(f: HttpServer => T): T =
    val script = ScriptFile(name)
    val server = sh.run(script, Array(host, port.toString)).asInstanceOf[HttpServer]
    try
      server.start()
      f(server)
    finally
      server.stop()
