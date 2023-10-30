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

import scamper.http.cookies.{ Cookie, PlainCookie, SetCookie }

private object toScamperCookies extends Converter:
  def apply(list: JList[AnyRef]): Seq[Cookie] =
    apply(list.toArray.toSeq)

  def apply(list: Seq[AnyRef]): Seq[Cookie] =
    try
      list.map {
        case null              => throw NullPointerException("cookie")
        case value: Cookie     => value
        case value: JMap[?, ?] => toCookie(asMap[String, AnyRef](value))
        case _                 => bad("unknown cookie type")
      }
    catch case cause: Exception =>
      bad("Invalid cookies", cause)

  private def toCookie(map: JMap[String, AnyRef]): Cookie =
    var isPlain = true

    val name = map.get("name") match
      case null                => bad("Missing cookie name")
      case value: CharSequence => value.toString
      case _                   => bad("cookie name")

    val value = map.get("value") match
      case null                => bad("Missing cookie value")
      case value: CharSequence => value.toString
      case _                   => bad("cookie value")

    val domain = map.get("domain") match
      case null                => None
      case value: CharSequence => isPlain = false; Some(value.toString)
      case _                   => bad("cookie domain")

    val path = map.get("path") match
      case null                => None
      case value: CharSequence => isPlain = false; Some(value.toString)
      case _                   => bad("cookie path")

    val expires = map.get("expires") match
      case null                 => None
      case value: Instant       => isPlain = false; Some(value)
      case value: Date          => isPlain = false; Some(value.toInstant)
      case value: CharSequence  => isPlain = false; Some(convert("cookie expires", value.toString, Instant.parse(_)))
      case _                    => bad("cookie expires")

    val maxAge = map.get("maxAge") match
      case null            => None
      case value: JShort   => isPlain = false; Some(value.longValue)
      case value: JInteger => isPlain = false; Some(value.longValue)
      case value: JLong    => isPlain = false; Some(value.longValue)
      case _               => bad("cookie maxAge")

    val secure = map.get("secure") match
      case null            => None
      case value: JBoolean => isPlain = false; Some(value.booleanValue)
      case _               => bad("cookie secure")

    val httpOnly = map.get("httpOnly") match
      case null            => None
      case value: JBoolean => isPlain = false; Some(value.booleanValue)
      case _               => bad("cookie httpOnly")

    isPlain match
      case true  => PlainCookie(name, value)
      case false => SetCookie(
        name     = name,
        value    = value,
        domain   = domain,
        path     = path,
        expires  = expires,
        maxAge   = maxAge,
        secure   = secure.contains(true),
        httpOnly = httpOnly.contains(true)
      )
