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

import scala.collection.mutable.{ HashMap, ListBuffer }

import scamper.http.auth.*

private object toRealChallenge extends Converter:
  def apply(map: JMap[String, AnyRef]): Challenge =
    map.requireString("scheme").toLowerCase match
      case "basic"  => toBasicChallenge(map)
      case "bearer" => toBearerChallenge(map)
      case value    => bad(s"Unrecognized authenticate scheme: $value")

  private def toBasicChallenge(map: JMap[String, AnyRef]): Challenge =
    val realm  = map.requireString("realm")
    val params = new HashMap[String, String]

    map.get("params") match
      case null              => Map.empty[String, String]
      case value: JMap[?, ?] =>
        val in = asJMap[String, AnyRef](value)
        in.keySet.forEach { name =>
          params += name -> in.requireString(name)
        }

    BasicChallenge(realm, params.toMap)

  private def toBearerChallenge(map: JMap[String, AnyRef]): Challenge =
    val realm = map.optionString("realm")
    val error = map.optionString("error")
    val error_description = map.optionString("error_description")
    val error_uri = map.optionString("error_uri")

    val scope = Option(map.get("scope")).map {
      case value: CharSequence => value.toString
      case value: Array[Char]  => String(value)
      case value: JList[?]     => toScope(asJList(value))
      case value               => bad(s"Invalid bearer scope (${value.getClass})")
    }

    val params = new HashMap[String, String]
    realm.foreach(value => params += "realm" -> value)
    error.foreach(value => params += "error" -> value)
    error_description.foreach(value => params += "error_description" -> value)
    error_uri.foreach(value => params += "error_uri" -> value)
    scope.foreach(value => params += "scope" -> value)

    BearerChallenge(params.toMap)

  private def toScope(list: JList[AnyRef]): String =
    try
      val buffer = new ListBuffer[String]
      list.forEach(value => buffer += requireString(value))
      buffer.mkString(" ")
    catch case cause: Exception =>
      bad("Invalid bearer scope", cause)

  private def requireString(value: AnyRef): String =
    value match
      case null                => throw NullPointerException()
      case value: CharSequence => value.toString
      case value: Array[Char]  => String(value)
      case value               => bad(s"Invalid value (${value.getClass})")
