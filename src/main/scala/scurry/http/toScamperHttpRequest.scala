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

import java.io.{ File, InputStream, OutputStream, Reader }
import java.net.{ URI, URL }
import java.nio.file.Path

import scamper.http.{ Entity, HttpRequest, QueryString as ScamperQueryString, RequestMethod, Uri }

private object toScamperHttpRequest extends Converter:
  def apply(map: JMap[String, AnyRef]): HttpRequest =
    val method = Option(map.get("method")).map {
      case value: String => convert("method", value, RequestMethod(_))
      case _             => bad("method")
    }.getOrElse(RequestMethod("GET"))

    val url = Option(map.get("url")).map {
      case value: String => convert("url", value, Uri(_))
      case value: URI    => convert("url", value.toString, Uri(_))
      case value: URL    => convert("url", value.toString, Uri(_))
      case _             => bad("url")
    }.getOrElse(throw IllegalArgumentException("Missing url"))

    val query = url.query ++ Option(map.get("query")).map {
      case value: String     => convert("query", value, ScamperQueryString(_))
      case value: JMap[?, ?] => convert("query", convert("query", value, _.asInstanceOf[JMap[String, AnyRef]]), toScamperQueryString(_))
      case _                 => bad("query")
    }.getOrElse(ScamperQueryString.empty)

    val headers = Option(map.get("headers")).map {
      case value: JMap[?, ?] => convert("headers", value, toScamperHeaders(_))
      case _                 => bad("headers")
    }.getOrElse(Nil)

    val body = Option(map.get("body")).map {
      case value: String      => Entity(value)
      case value: File        => Entity(value)
      case value: Path        => Entity(value.toFile)
      case value: Array[Byte] => Entity(value)
      case value: InputStream => Entity(value)
      case value: Reader      => Entity(out => ReaderBodyWriter(value).write(out))
      case value: BodyWriter  => Entity(value.write(_))
      case null               => Entity.empty
      case _                  => bad("body")
    }.getOrElse(Entity.empty)

    method(url).setQuery(query).setHeaders(headers).setBody(body)
