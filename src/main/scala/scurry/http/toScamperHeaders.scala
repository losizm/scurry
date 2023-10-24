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

import java.lang.{ Integer as JInteger, Long as JLong, Short as JShort }
import java.time.Instant
import java.util.Date

import scala.collection.mutable.ListBuffer

import scamper.http.Header

private object toScamperHeaders:
  def apply(map: JMap[?, ?]): Seq[Header] =
    val headers = ListBuffer[Header]() 

    try
      map.forEach {
        case (name: String, value: String)    => headers += Header(name, value)
        case (name: String, value: JShort)    => headers += Header(name, value.intValue)
        case (name: String, value: JInteger)  => headers += Header(name, value)
        case (name: String, value: JLong)     => headers += Header(name, value)
        case (name: String, value: Instant)   => headers += Header(name, value)
        case (name: String, value: Date)      => headers += Header(name, value.toInstant)
        case (name: String, values: JList[?]) => values.forEach {
          case value: String   => headers += Header(name, value)
          case value: JShort   => headers += Header(name, value.intValue)
          case value: JInteger => headers += Header(name, value)
          case value: JLong    => headers += Header(name, value)
          case value: Instant  => headers += Header(name, value)
          case value: Date     => headers += Header(name, value.toInstant)
          case _               => throw IllegalArgumentException(s"Invalid header: $name")
        }
        case (_, _) => throw IllegalArgumentException("Invalid headers")
      }

      headers.toSeq
    catch case _: Exception => throw IllegalArgumentException("Invalid headers")
