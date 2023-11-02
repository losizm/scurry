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
import scamper.http.auth.{ WwwAuthenticate, Challenge }
import scamper.http.cookies.{ Cookie, ResponseCookies, SetCookie }

private object toRealHttpResponse extends RealHttpMessageExtensions:
  def apply(map: JMap[String, AnyRef], statusCode: Int): RealHttpResponse =
    map.put("statusCode", JInteger.valueOf(statusCode))
    map.remove("reasonPhrase")
    apply(map)

  def apply(map: JMap[String, AnyRef]): RealHttpResponse =
    val statusCode = map.requireInt("statusCode")

    val status = map.optionString("reasonPhrase")
      .map(ResponseStatus(statusCode, _))
      .getOrElse(ResponseStatus(statusCode))

    val headers = map.get("headers") match
      case null              => Nil
      case value: Headers    => value.realHeaders
      case value: JMap[?, ?] => convert("headers", asJMap[String, AnyRef](value), toRealHeaders(_))
      case value             => bad(s"headers (${value.getClass})")

    val cookies = map.get("cookies") match
      case null                 => Nil
      case value: Cookies       => value.realCookies
      case value: Array[AnyRef] => convert("cookies", value.toSeq, toRealCookies(_))
      case value: JList[?]      => convert("cookies", asJList[AnyRef](value), toRealCookies(_))
      case value                => bad(s"cookies (${value.getClass})")

    val wwwAuthenticate = Option(map.get("wwwAuthenticate")).map {
      case value: JMap[?, ?] => convert("wwwAuthenticate", asJMap[String, AnyRef](value), toRealChallenge(_))
      case value             => bad(s"wwwAuthenticate (${value.getClass})")
    }

    status()
      .setHeaders(headers)
      .putCookies(cookies.map(toSetCookie))
      .setChallenge(wwwAuthenticate)
      .setAnyRefBody(map.get("body"), headers.getHeaderValue("Content-Type").isEmpty)

  private def toSetCookie(cookie: Cookie): SetCookie =
    cookie match
      case cookie: SetCookie => cookie
      case cookie            => SetCookie(cookie.name, cookie.value)

  extension (res: RealHttpResponse)
    private def setChallenge(challenge: Option[Challenge]): RealHttpResponse =
      challenge.map(res.setWwwAuthenticate(_)).getOrElse(res)
