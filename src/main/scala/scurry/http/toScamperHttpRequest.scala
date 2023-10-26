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

import scamper.http.{ Entity, Header, HttpRequest, QueryString as ScamperQueryString, RequestMethod, Uri }
import scamper.http.{ getHeaderValue, setFileBody, setFormBody, setOctetBody, setPlainBody }
import scamper.http.cookies.{ Cookie, PlainCookie, RequestCookies, SetCookie }
import scamper.http.multipart.Multipart.boundary as multipartBoundary
import scamper.http.multipart.setMultipartBody as setScamperMultipartBody

private object toScamperHttpRequest extends Converter:
  private val plainHeader = Header("Content-Type", "text/plain")
  private val octetHeader = Header("Content-Type", "application/octet-stream")

  def apply(map: JMap[String, AnyRef]): HttpRequest =
    val method = map.get("method") match
      case null          => RequestMethod("GET")
      case value: String => convert("method", value, RequestMethod(_))
      case _             => bad("method")

    val url = map.get("url") match
      case null          => bad("Missing url")
      case value: String => convert("url", value, Uri(_))
      case value: URI    => convert("url", value.toString, Uri(_))
      case value: URL    => convert("url", value.toString, Uri(_))
      case _             => bad("url")

    val query = url.query ++ { map.get("query") match
      case null              => ScamperQueryString.empty
      case value: String     => convert("query", value, ScamperQueryString(_))
      case value: JMap[?, ?] => convert("query", toMap[String, AnyRef](value), toScamperQueryString(_))
      case _                 => bad("query")
    }

    val headers = map.get("headers") match
      case null              => Nil
      case value: Headers    => value.toScamperHeaders
      case value: JMap[?, ?] => convert("headers", toMap[String, AnyRef](value), toScamperHeaders(_))
      case _                 => bad("headers")

    val cookies = map.get("cookies") match
      case null                 => Nil
      case value: Cookies       => value.toScamperCookies
      case value: Array[AnyRef] => convert("cookies", value.toSeq, toScamperCookies(_))
      case value: JList[?]      => convert("cookies", toList[AnyRef](value), toScamperCookies(_))
      case _                    => bad("cookies")

    method(url)
      .setQuery(query)
      .setHeaders(headers)
      .putCookies(cookies.collect { case cookie: PlainCookie => cookie })
      .setAnyRefBody(map.get("body"), headers.getHeaderValue("Content-Type").isEmpty)

  extension (req: HttpRequest)
    private def setAnyRefBody(body: AnyRef, autoContentType: Boolean): HttpRequest =
      body match
        case null               => req
        case value: Array[Byte] => if autoContentType then req.setOctetBody(value) else req.setBody(Entity(value))
        case value: String      => if autoContentType then req.setPlainBody(value) else req.setBody(Entity(value))
        case value: File        => if autoContentType then req.setFileBody(value)  else req.setBody(Entity(value))
        case value: Path        => req.setPathBody(value, autoContentType)
        case value: InputStream => req.setInputStreamBody(value, autoContentType)
        case value: Reader      => req.setReaderBody(value, autoContentType)
        case value: QueryString => req.setQueryStringBody(value, autoContentType)
        case value: Multipart   => req.setMultipartBody(value, autoContentType)
        case value: BodyWriter  => req.setBodyWriterBody(value, autoContentType)
        case value: JMap[?, ?]  => req.setParamsBody(value, autoContentType)
        case _                  => bad("body")

    private def setPathBody(value: Path, autoContentType: Boolean): HttpRequest =
      autoContentType match
        case true  => req.setFileBody(value.toFile)
        case false => req.setBody(Entity(value.toFile))

    private def setInputStreamBody(value: InputStream, autoContentType: Boolean): HttpRequest =
      val entity = Entity(value)
      autoContentType match
        case true  => req.setBody(entity).addHeaders(octetHeader)
        case false => req.setBody(entity)

    private def setReaderBody(value: Reader, autoContentType: Boolean): HttpRequest =
      val entity = Entity(out => ReaderBodyWriter(value).write(out))
      autoContentType match
        case true  => req.setBody(entity).addHeaders(plainHeader)
        case false => req.setBody(entity)

    private def setQueryStringBody(value: QueryString, autoContentType: Boolean): HttpRequest =
      val queryString = value.toScamperQueryString
      autoContentType match
        case true  => req.setFormBody(queryString)
        case false => req.setBody(Entity(queryString))

    private def setMultipartBody(value: Multipart, autoContentType: Boolean): HttpRequest =
      val multipart = value.toScamperMultipart
      autoContentType match
        case true  => req.setScamperMultipartBody(multipart)
        case false => req.setBody(multipart.toEntity(multipartBoundary()))

    private def setBodyWriterBody(value: BodyWriter, autoContentType: Boolean): HttpRequest =
      val entity = Entity(out => value.write(out))
      autoContentType match
        case true  => req.setBody(entity).addHeaders(octetHeader)
        case false => req.setBody(entity)

    private def setParamsBody(value: JMap[?, ?], autoContentType: Boolean): HttpRequest =
      val queryString = convert("body", toMap[String, AnyRef](value), QueryString(_))
      setQueryStringBody(queryString, autoContentType)
