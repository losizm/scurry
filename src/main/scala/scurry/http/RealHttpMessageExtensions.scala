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
import java.nio.file.Path

import scamper.http.{ Entity, Header, MessageBuilder }
import scamper.http.{ getHeaderValue, setFileBody, setFormBody, setOctetBody, setPlainBody }
import scamper.http.multipart.setMultipartBody as setRealMultipartBody
import RealMultipart.boundary as multipartBoundary

private trait RealHttpMessageExtensions extends Converter:
  private val plainHeader = Header("Content-Type", "text/plain")
  private val octetHeader = Header("Content-Type", "application/octet-stream")

  extension [T <: RealHttpMessage & MessageBuilder[T]] (msg: T)
    def setAnyRefBody(body: AnyRef, autoContentType: Boolean): T =
      body match
        case null                => msg
        case value: Array[Byte]  => if autoContentType then msg.setOctetBody(value) else msg.setBody(Entity(value))
        case value: File         => if autoContentType then msg.setFileBody(value)  else msg.setBody(Entity(value))
        case value: Path         => msg.setPathBody(value, autoContentType)
        case value: CharSequence => msg.setCharSequenceBody(value, autoContentType)
        case value: InputStream  => msg.setInputStreamBody(value, autoContentType)
        case value: Reader       => msg.setReaderBody(value, autoContentType)
        case value: QueryString  => msg.setQueryStringBody(value, autoContentType)
        case value: Multipart    => msg.setMultipartBody(value, autoContentType)
        case value: BodyWriter   => msg.setBodyWriterBody(value, autoContentType)
        case value: JMap[?, ?]   => msg.setParamsBody(value, autoContentType)
        case value               => bad(s"body (${value.getClass})")

    def setCharSequenceBody(value: CharSequence, autoContentType: Boolean): T =
      autoContentType match
        case true  => msg.setPlainBody(value.toString)
        case false => msg.setBody(Entity(value.toString))

    def setPathBody(value: Path, autoContentType: Boolean): T =
      autoContentType match
        case true  => msg.setFileBody(value.toFile)
        case false => msg.setBody(Entity(value.toFile))

    def setInputStreamBody(value: InputStream, autoContentType: Boolean): T =
      val entity = Entity(value)
      autoContentType match
        case true  => msg.setBody(entity).addHeaders(octetHeader)
        case false => msg.setBody(entity)

    def setReaderBody(value: Reader, autoContentType: Boolean): T =
      val entity = Entity(value)
      autoContentType match
        case true  => msg.setBody(entity).addHeaders(plainHeader)
        case false => msg.setBody(entity)

    def setQueryStringBody(value: QueryString, autoContentType: Boolean): T =
      val queryString = value.realQueryString
      autoContentType match
        case true  => msg.setFormBody(queryString)
        case false => msg.setBody(Entity(queryString))

    def setMultipartBody(value: Multipart, autoContentType: Boolean): T =
      val multipart = value.realMultipart
      autoContentType match
        case true  => msg.setRealMultipartBody(multipart)
        case false => msg.setBody(multipart.toEntity(multipartBoundary()))

    def setBodyWriterBody(value: BodyWriter, autoContentType: Boolean): T =
      val entity = Entity(out => value.write(out))
      autoContentType match
        case true  => msg.setBody(entity).addHeaders(octetHeader)
        case false => msg.setBody(entity)

    def setParamsBody(value: JMap[?, ?], autoContentType: Boolean): T =
      val queryString = convert("body", asJMap[String, AnyRef](value), QueryString(_))
      setQueryStringBody(queryString, autoContentType)
