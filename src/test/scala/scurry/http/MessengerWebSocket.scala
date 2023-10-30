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

import com.typesafe.config.Config

import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.LoggerFactory.getLogger as Logger

import scamper.http.server.{ *, given }

class MessengerWebSocket(config: Config) extends RouterApplication:
  private val messages = Seq("Hello, world!", "It's time to scurry", "Goodbye, cruel world!")

  def apply(router: Router): Unit =
    val log = Logger("MessengerWebSocket")

    router.websocket("/") { websocket =>
      val messageIndex = AtomicInteger(0)

      websocket.idleTimeout(1000 * config.getInt("test.server.messenger.idleTimeout"))
      websocket.messageCapacity(config.getInt("test.server.messenger.messageCapacity"))
      websocket.payloadLimit(config.getInt("test.server.messenger.payloadLimit"))

      websocket.onPing { data =>
        log.debug(s"WebSocket ${websocket.id}: onPing => ${data.size} byte(s)")
        websocket.pong(data)
      }

      websocket.onPong { data =>
        log.debug(s"WebSocket ${websocket.id}: onPong => ${data.size} byte(s)")
      }

      websocket.onText { text =>
        log.debug(s"WebSocket ${websocket.id}: onText => $text")
        text match
          case "get"   =>
            val index = messageIndex.getAndIncrement()
            index < messages.size match
              case true  => websocket.send(messages(index))
              case false => websocket.send("Error: No more messages")

          case "reset" =>
            messageIndex.set(0)

          case other   =>
            websocket.send("Error: Invalid command")
      }

      websocket.onBinary { binary =>
        log.debug(s"WebSocket ${websocket.id}: onBinary => ${binary.size} byte(s)")
      }

      websocket.onError { error =>
        log.debug(s"WebSocket ${websocket.id}: onError => $error")
      }

      websocket.onClose { code =>
        log.debug(s"WebSocket ${websocket.id}: onClose => $code")
      }

      websocket.ping("test".getBytes("UTF-8"))
      websocket.open()
    }
