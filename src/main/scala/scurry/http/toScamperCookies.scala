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

import java.lang.{ Boolean as JBoolean, Integer as JInteger, Long as JLong, Short as JShort }
import java.time.Instant
import java.util.Date

import scala.collection.mutable.ListBuffer

import scamper.http.cookies.{ Cookie, PlainCookie, SetCookie }

private object toScamperCookies extends Converter:
  def apply(list: JList[AnyRef]): Seq[Cookie] =
    apply(list.toArray.toSeq)

  def apply(list: Seq[AnyRef]): Seq[Cookie] =
    list.map { value =>
      try
        toCookie(value.asInstanceOf[JMap[String, AnyRef]])
      catch case cause: Exception =>
        bad(s"Invalid cookie", cause)
    }

  private def toCookie(map: JMap[String, AnyRef]): Cookie =
    var isPlain = true

    val name = map.get("name") match
      case null          => bad("Missing cookie name")
      case value: String => value
      case _             => bad("cookie name")

    val value = map.get("value") match
      case null          => bad("Missing cookie value")
      case value: String => value
      case _             => bad("cookie value")

    val domain = map.get("domain") match
      case null          => None
      case value: String => isPlain = false; Some(value)
      case _             => bad("cookie domain")

    val path = map.get("path") match
      case null          => None
      case value: String => isPlain = false; Some(value)
      case _             => bad("cookie path")

    val expires = map.get("expires") match
      case null           => None
      case value: Instant => isPlain = false; Some(value)
      case value: Date    => isPlain = false; Some(value.toInstant)
      case value: String  => isPlain = false; Some(convert("cookie expires", value, Instant.parse(_)))
      case _              => bad("cookie expires")

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
