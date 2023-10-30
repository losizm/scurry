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

import java.net.Socket
import java.time.Instant
import java.util.Date

import scamper.http.Header
import scamper.http.server.ServerHttpMessage

import settings.{ ActiveServerSettings, ServerSettings }

private trait ServerSideHttpMessage:
  self: HttpMessage =>

  def setHeader(name: String, value: AnyRef): Unit =
    value match
      case value: CharSequence => setScamperHttpMessage(scamperMessageBuilder.putHeaders(Header(name, value.toString)))
      case value: JShort       => setScamperHttpMessage(scamperMessageBuilder.putHeaders(Header(name, value.intValue)))
      case value: JInteger     => setScamperHttpMessage(scamperMessageBuilder.putHeaders(Header(name, value.intValue)))
      case value: JLong        => setScamperHttpMessage(scamperMessageBuilder.putHeaders(Header(name, value.longValue)))
      case value: Instant      => setScamperHttpMessage(scamperMessageBuilder.putHeaders(Header(name, value)))
      case value: Date         => setScamperHttpMessage(scamperMessageBuilder.putHeaders(Header(name, value.toInstant)))
      case _                   => throw IllegalArgumentException("Invalid header value")

  def addHeader(name: String, value: AnyRef): Unit =
    value match
      case value: CharSequence => setScamperHttpMessage(scamperMessageBuilder.addHeaders(Header(name, value.toString)))
      case value: JShort       => setScamperHttpMessage(scamperMessageBuilder.addHeaders(Header(name, value.intValue)))
      case value: JInteger     => setScamperHttpMessage(scamperMessageBuilder.addHeaders(Header(name, value.intValue)))
      case value: JLong        => setScamperHttpMessage(scamperMessageBuilder.addHeaders(Header(name, value.longValue)))
      case value: Instant      => setScamperHttpMessage(scamperMessageBuilder.addHeaders(Header(name, value)))
      case value: Date         => setScamperHttpMessage(scamperMessageBuilder.addHeaders(Header(name, value.toInstant)))
      case _                   => throw IllegalArgumentException("Invalid header value")

  def removeHeader(name: String): Unit =
    setScamperHttpMessage(scamperMessageBuilder.removeHeaders(name))

  def getAttribute(name: String): AnyRef =
    scamperHttpMessage.getAttributeOrElse(name, null)

  def setAttribute(name: String, value: AnyRef): Unit =
    setScamperHttpMessage(scamperMessageBuilder.putAttributes(name -> value))

  def removeAttribute(name: String): Unit =
    setScamperHttpMessage(scamperMessageBuilder.removeAttributes(name))

  def getServerSettings(): ServerSettings =
    ActiveServerSettings(scamperHttpMessage.server)

  def getCorrelate(): String =
    scamperHttpMessage.correlate

  def getRequestCount(): Int =
    scamperHttpMessage.requestCount

  def getSocket(): Socket =
    scamperHttpMessage.socket
