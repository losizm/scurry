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
package settings

import java.io.File
import java.net.InetAddress
import java.nio.file.Path

private[scurry] trait ServerSettingsConverter extends Converter:
  def toHost(settings: JMap[String, AnyRef]): InetAddress =
    settings.get("host") match
      case null                => InetAddress.getByName("0.0.0.0")
      case value: CharSequence => InetAddress.getByName(value.toString)
      case value: InetAddress  => value
      case _                   => throw IllegalArgumentException("Invalid host")

  def toPort(settings: JMap[String, AnyRef]): Int =
    settings.get("port") match
      case null            => 0
      case value: JShort   => if value < 0 then value + 65536 else value.intValue
      case value: JInteger => value.intValue
      case _               => throw IllegalArgumentException("Invalid port")

  def toInt(map: JMap[String, AnyRef], name: String): Int =
    toOptionInt(map, name).getOrElse(bad(s"Missing $name"))

  def toFile(map: JMap[String, AnyRef], name: String): File =
    map.get(name) match
      case null                => bad(s"Missing $name")
      case value: File         => value
      case value: Path         => value.toFile
      case value: CharSequence => File(value.toString)
      case _                   => bad(name)

  def toOptionString(map: JMap[String, AnyRef], name: String): Option[String] =
    Option(map.get(name)).map {
      case value: CharSequence => value.toString
      case _                   => bad(name)
    }

  def toOptionInt(map: JMap[String, AnyRef], name: String): Option[Int] =
    Option(map.get(name)).map {
      case value: JShort   => value.intValue
      case value: JInteger => value
      case _               => bad(name)
    }

  def toOptionKeepAlive(map: JMap[String, AnyRef]): Option[(Int, Int)] =
    Option(map.get("keepAlive")).map {
      case value: JMap[?, ?] =>
        try
          (toInt(asMap(value), "timeout"), toInt(asMap(value), "max"))
        catch case cause: Exception => bad("keepAlive", cause)
      case _ => bad("keepAlive")
    }

  def toOptionSsl(map: JMap[String, AnyRef]): Option[(File, File)] =
    Option(map.get("ssl")).map {
      case value: JMap[?, ?]   =>
        try
          (toFile(asMap(value), "key"), toFile(asMap(value), "certificate"))
        catch case cause: Exception => bad("ssl", cause)
      case _ => bad("ssl")
    }
