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

import scamper.http.{ RequestMethod, Uri, getHeaderValue }
import scamper.http.cookies.{ PlainCookie, RequestCookies }

private object toScamperHttpRequest extends ScamperHttpMessageExtensions:
  def apply(map: JMap[String, AnyRef], method: String): ScamperHttpRequest =
    map.put("method", method)
    apply(map)

  def apply(map: JMap[String, AnyRef]): ScamperHttpRequest =
    val method = map.get("method") match
      case null                => RequestMethod("GET")
      case value: CharSequence => convert("method", value.toString, RequestMethod(_))
      case _                   => bad("method")

    val url = map.get("url") match
      case null                => bad("Missing url")
      case value: CharSequence => convert("url", value.toString, Uri(_))
      case value: URI          => convert("url", value.toString, Uri(_))
      case value: URL          => convert("url", value.toString, Uri(_))
      case _                   => bad("url")

    val query = url.query ++ { map.get("query") match
      case null                    => ScamperQueryString.empty
      case value: CharSequence     => convert("query", value.toString, ScamperQueryString(_))
      case value: JMap[?, ?]       => convert("query", asMap[String, AnyRef](value), toScamperQueryString(_))
      case _                       => bad("query")
    }

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

    method(url)
      .setQuery(query)
      .setHeaders(headers)
      .putCookies(cookies.collect { case cookie: PlainCookie => cookie })
      .setAnyRefBody(map.get("body"), headers.getHeaderValue("Content-Type").isEmpty)
