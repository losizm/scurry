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

import scala.collection.mutable.ListBuffer

private object toScamperQueryString extends Converter:
  def apply(map: JMap[String, AnyRef]): ScamperQueryString =
    val params = ListBuffer[(String, String)]() 

    try
      map.forEach {
        case (name, value: CharSequence) => params += name -> value.toString
        case (name, value: JShort)       => params += name -> value.toString
        case (name, value: JInteger)     => params += name -> value.toString
        case (name, value: JLong)        => params += name -> value.toString
        case (name, values: JList[?])    => values.forEach {
          case value: CharSequence => params += name -> value.toString
          case value: JShort       => params += name -> value.toString
          case value: JInteger     => params += name -> value.toString
          case value: JLong        => params += name -> value.toString
          case _                   => bad(s"Invalid query string parameter: $name")
        }
        case (name, _) => bad(s"Invalid query string parameter: $name")
      }
      ScamperQueryString(params.toSeq)
    catch case cause: Exception => throw bad("Invalid query string", cause)
