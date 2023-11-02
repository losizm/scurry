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

import java.time.Instant
import java.util.Date

import scamper.http.Header

private trait MutableHttpMessage:
  msg: HttpMessage =>

  def setHeader(name: String, value: AnyRef): this.type =
    value match
      case value: CharSequence => setRealHttpMessage(realMessageBuilder.putHeaders(Header(name, value.toString)))
      case value: JShort       => setRealHttpMessage(realMessageBuilder.putHeaders(Header(name, value.intValue)))
      case value: JInteger     => setRealHttpMessage(realMessageBuilder.putHeaders(Header(name, value.intValue)))
      case value: JLong        => setRealHttpMessage(realMessageBuilder.putHeaders(Header(name, value.longValue)))
      case value: Instant      => setRealHttpMessage(realMessageBuilder.putHeaders(Header(name, value)))
      case value: Date         => setRealHttpMessage(realMessageBuilder.putHeaders(Header(name, value.toInstant)))
      case value               => throw IllegalArgumentException(s"Invalid header value (${value.getClass})")
    this

  def addHeader(name: String, value: AnyRef): this.type =
    value match
      case value: CharSequence => setRealHttpMessage(realMessageBuilder.addHeaders(Header(name, value.toString)))
      case value: JShort       => setRealHttpMessage(realMessageBuilder.addHeaders(Header(name, value.intValue)))
      case value: JInteger     => setRealHttpMessage(realMessageBuilder.addHeaders(Header(name, value.intValue)))
      case value: JLong        => setRealHttpMessage(realMessageBuilder.addHeaders(Header(name, value.longValue)))
      case value: Instant      => setRealHttpMessage(realMessageBuilder.addHeaders(Header(name, value)))
      case value: Date         => setRealHttpMessage(realMessageBuilder.addHeaders(Header(name, value.toInstant)))
      case value               => throw IllegalArgumentException(s"Invalid header value (${value.getClass})")
    this

  def removeHeader(name: String): this.type =
    setRealHttpMessage(realMessageBuilder.removeHeaders(name))
    this

  def getAttribute(name: String): AnyRef =
    realHttpMessage.getAttributeOrElse(name, null)

  def setAttribute(name: String, value: AnyRef): this.type =
    setRealHttpMessage(realMessageBuilder.putAttributes(name -> value))
    this

  def removeAttribute(name: String): this.type =
    setRealHttpMessage(realMessageBuilder.removeAttributes(name))
    this
