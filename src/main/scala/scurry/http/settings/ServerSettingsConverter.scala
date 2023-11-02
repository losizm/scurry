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
  extension (map: JMap[String, AnyRef])
    def requireHost(): InetAddress =
      map.get("host") match
        case null                => InetAddress.getByName("0.0.0.0")
        case value: CharSequence => InetAddress.getByName(value.toString)
        case value: InetAddress  => value
        case value               => bad(s"Invalid host (${value.getClass})")

    def requirePort(): Int =
      map.get("port") match
        case null            => 0
        case value: JShort   => value.intValue
        case value: JInteger => value.intValue
        case value: JLong    => checkInt(value.longValue, s"Port number too large ($value)")
        case value           => bad(s"Invalid port (${value.getClass})")

    def optionKeepAlive(): Option[(Int, Int)] =
      Option(map.get("keepAlive")).map {
        case value: JMap[?, ?] =>
          try
            val keepAlive = asJMap[String, AnyRef](value)
            (keepAlive.requireInt("timeout"), keepAlive.requireInt("max"))
          catch case cause: Exception => bad("keepAlive", cause)
        case _ => bad("keepAlive")
      }

    def optionSsl(): Option[(File, File)] =
      Option(map.get("ssl")).map {
        case value: JMap[?, ?] =>
          try
            val ssl = asJMap[String, AnyRef](value)
            (ssl.requireFile("key"), ssl.requireFile("certificate"))
          catch case cause: Exception => bad("ssl", cause)
        case _ => bad("ssl")
      }
