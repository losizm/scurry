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

import java.lang.Integer as JInteger

import scamper.http.{ ResponseStatus, getHeaderValue }
import scamper.http.cookies.{ ResponseCookies, SetCookie }

private object toScamperHttpResponse extends ScamperHttpMessageExtensions:
  def apply(map: JMap[String, AnyRef], statusCode: Int): ScamperHttpResponse =
    map.put("statusCode", JInteger.valueOf(statusCode))
    map.remove("reasonPhrase")
    apply(map)

  def apply(map: JMap[String, AnyRef]): ScamperHttpResponse =
    val statusCode = map.get("statusCode") match
      case null            => bad("Missing statusCode")
      case value: JShort   => value.intValue
      case value: JInteger => value.intValue
      case _               => bad("statusCode")

    val status = map.get("reasonPhrase") match
      case null                => ResponseStatus(statusCode)
      case value: CharSequence => ResponseStatus(statusCode, value.toString)
      case _                   => bad("reasonPhrase")

    val headers = map.get("headers") match
      case null              => Nil
      case value: Headers    => value.toScamperHeaders
      case value: JMap[?, ?] => convert("headers", asMap[String, AnyRef](value), toScamperHeaders(_))
      case _                 => bad("headers")

    val cookies = map.get("cookies") match
      case null                 => Nil
      case value: Cookies       => value.toScamperCookies
      case value: Array[AnyRef] => convert("cookies", value.toSeq, toScamperCookies(_))
      case value: JList[?]      => convert("cookies", asList[AnyRef](value), toScamperCookies(_))
      case _                    => bad("cookies")

    status()
      .setHeaders(headers)
      .putCookies(cookies.collect { case cookie: SetCookie => cookie })
      .setAnyRefBody(map.get("body"), headers.getHeaderValue("Content-Type").isEmpty)
