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

import scamper.http.QueryString as ScamperQueryString

private object toScamperQueryString:
  def apply(map: JMap[String, AnyRef]): ScamperQueryString =
    val params = ListBuffer[(String, String)]() 

    try
      map.forEach {
        case (name: String, value: String)    => params += name -> value
        case (name: String, value: JShort)    => params += name -> value.toString
        case (name: String, value: JInteger)  => params += name -> value.toString
        case (name: String, value: JLong)     => params += name -> value.toString
        case (name: String, values: JList[?]) => values.forEach {
          case value: String   => params += name -> value
          case value: JShort   => params += name -> value.toString
          case value: JInteger => params += name -> value.toString
          case value: JLong    => params += name -> value.toString
          case _               => throw IllegalArgumentException(s"Invalid parameter: $name")
        }
        case (_, _) => throw IllegalArgumentException("Invalid query string")
      }

      ScamperQueryString(params.toSeq)
    catch case _: Exception => throw IllegalArgumentException("query string")
