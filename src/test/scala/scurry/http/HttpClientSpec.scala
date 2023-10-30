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

class HttpClientSpec extends org.scalatest.flatspec.AnyFlatSpec:
  self =>

  private val sh = GroovyShell()
  sh.setVariable("KB", 1024)
  sh.setVariable("MB", 1024 * 1024)
  sh.setVariable("resources", File("./src/test/resources"))
  sh.setVariable("log", new AnyRef { def info(message: String): Unit = self.info(message) })

  it should "send request and read response from bible server" in {
    val script = ScriptFile("bible-client")
    sh.run(script, Array("localhost", "10080"))
  }

  it should "send request and read response from barbershop server" in {
    val script = ScriptFile("barbershop-client")
    sh.run(script, Array("localhost", "20080"))
  }

  it should "send and receive messages from messenger websocket server" in {
    val server = TestServerFactory.createServer()

    try
      val script = ScriptFile("messenger-client")
      sh.run(script, Array(server.host.getHostAddress, server.port.toString, server.isSecure.toString))
    finally
      server.close()
  }
