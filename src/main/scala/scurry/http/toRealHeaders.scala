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

import scala.collection.mutable.ListBuffer

import scamper.http.Header

private object toRealHeaders extends Converter:
  def apply(map: JMap[String, AnyRef]): Seq[Header] =
    try
      val headers = ListBuffer[Header]()
      map.forEach {
        case (name, value: JList[?]) => value.forEach { value => headers += toHeader(name, value) }
        case (name, value: AnyRef)   => headers += toHeader(name, value)
      }

      headers.toSeq
    catch case cause: Exception =>
      bad("Invalid headers", cause)

  private def toHeader[T](name: String, value: T): Header =
    value match
      case value: CharSequence => Header(name, value.toString)
      case value: JShort       => Header(name, value.intValue)
      case value: JInteger     => Header(name, value)
      case value: JLong        => Header(name, value)
      case value: Instant      => Header(name, value)
      case value: Date         => Header(name, value.toInstant)
      case _                   => bad(s"Invalid header: $name")
