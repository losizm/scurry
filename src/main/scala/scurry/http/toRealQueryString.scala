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

private object toRealQueryString extends Converter:
  def apply(map: JMap[String, AnyRef]): RealQueryString =
    try
      val params = ListBuffer[(String, String)]()
      map.forEach {
        case (name, value: JList[?]) => value.forEach { value => params += toParam(name, value) }
        case (name, value: AnyRef)   => params += toParam(name, value)
      }
      RealQueryString(params.toSeq)
    catch case cause: Exception =>
      bad("Invalid query string", cause)

  private def toParam[T](name: String, value: T): (String, String) =
    value match
      case value: CharSequence => name -> value.toString
      case value: JShort       => name -> value.toString
      case value: JInteger     => name -> value.toString
      case value: JLong        => name -> value.toString
      case value               => bad(s"Invalid query parameter: $name (${value.getClass})")
