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

import java.io.File
import java.nio.file.Path

private trait Converter:
  def convert[I, O](name: String, value: I, converter: I => O): O =
    try
      converter(value)
    catch case cause: Exception =>
      bad(name, cause)

  def bad(message: String, cause: Throwable = null): Nothing =
    throw IllegalArgumentException(message, cause)

  def checkInt(value: Long, message: String): Int =
    val intValue = value.toInt
    if value != intValue then
      bad(message)
    intValue

  extension (map: JMap[String, AnyRef])
    def optionBoolean(name: String): Option[Boolean] =
      map.get(name) match
        case null            => None
        case value: JBoolean => Some(value)
        case value           => bad(s"Boolean required for $name (${value.getClass})")

    def requireBoolean(name: String): Boolean =
      optionBoolean(name).getOrElse(bad(s"$name is required"))

    def requireInt(name: String): Int =
      optionInt(name).getOrElse(bad(s"$name is required"))

    def optionInt(name: String): Option[Int] =
      map.get(name) match
        case null            => None
        case value: JShort   => Some(value.intValue)
        case value: JInteger => Some(value.intValue)
        case value: JLong    => Some(checkInt(value.longValue, s"Integer value required for $name ($value)"))
        case value           => bad(s"Integer required for $name (${value.getClass})")

    def requireString(name: String): String =
      optionString(name).getOrElse(bad(s"$name is required"))

    def optionString(name: String): Option[String] =
      map.get(name) match
        case null                => None
        case value: CharSequence => Some(value.toString)
        case value: Array[Char]  => Some(String(value))
        case value               => bad(s"String required for $name (${value.getClass})")

    def requireFile(name: String): File =
      optionFile(name).getOrElse(bad(s"$name is required"))

    def optionFile(name: String): Option[File] =
      map.get(name) match
        case null                => None
        case value: File         => Some(value)
        case value: Path         => Some(value.toFile)
        case value: CharSequence => Some(File(value.toString))
        case value               => bad(s"File required for $name (${value.getClass})")
