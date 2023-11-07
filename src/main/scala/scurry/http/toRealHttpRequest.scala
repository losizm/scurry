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

import java.net.{ URI, URL }

import scala.language.implicitConversions

import scamper.http.{ RequestMethod, Uri, getHeaderValue }
import scamper.http.auth.{ Credentials, toAuthorization }
import scamper.http.cookies.{ PlainCookie, toRequestCookies }

private object toRealHttpRequest extends RealHttpMessageExtensions:
  def apply(map: JMap[String, AnyRef], method: String): RealHttpRequest =
    map.put("method", method)
    apply(map)

  def apply(map: JMap[String, AnyRef]): RealHttpRequest =
    val method = map.optionString("method")
      .map(value => convert("method", value, RequestMethod(_)))
      .getOrElse(RequestMethod("GET"))

    val target = map.get("target") match
      case null                => bad("Missing target")
      case value: CharSequence => convert("target", value.toString, Uri(_))
      case value: URI          => convert("target", value.toString, Uri(_))
      case value: URL          => convert("target", value.toString, Uri(_))
      case value               => bad(s"target (${value.getClass})")

    val query = target.query ++ { map.get("query") match
      case null                    => RealQueryString.empty
      case value: CharSequence     => convert("query", value.toString, RealQueryString(_))
      case value: JMap[?, ?]       => convert("query", asJMap[String, AnyRef](value), toRealQueryString(_))
      case value                   => bad(s"query (${value.getClass})")
    }

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

    val authorization = Option(map.get("authorization")).map {
      case value: JMap[?, ?] => convert("authorization", asJMap[String, AnyRef](value), toRealCredentials(_))
      case value             => bad(s"authorization (${value.getClass})")
    }

    method(target)
      .setQuery(query)
      .setHeaders(headers)
      .putCookies(cookies.collect { case cookie: PlainCookie => cookie })
      .setCredentials(authorization)
      .setAnyRefBody(map.get("body"), headers.getHeaderValue("Content-Type").isEmpty)

  extension (req: RealHttpRequest)
    def setCredentials(creds: Option[Credentials]): RealHttpRequest =
      creds.map(req.setAuthorization).getOrElse(req)
