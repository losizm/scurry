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

import java.io.File
import java.nio.file.Path

import scala.language.implicitConversions

import scamper.http.server.toServerHttpResponse

import settings.{ ActiveServerSettings, ServerSettings }

private[scurry] class ServerSideHttpResponse(res: RealHttpResponse) extends HttpResponse(res) with ServerSideHttpMessage with WwwAuthenticate:
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res))

  def getRequest(): HttpRequest =
    realHttpMessage.request
      .map(HttpRequest(_))
      .getOrElse(null)

  def setAttachment(file: File | Path): this.type =
    file match
      case f: File => setRealHttpMessage(realHttpMessage.setAttachment(f))
      case f: Path => setRealHttpMessage(realHttpMessage.setAttachment(f.toFile))
    this

  def setInline(file: File | Path): this.type =
    file match
      case f: File => setRealHttpMessage(realHttpMessage.setInline(f))
      case f: Path => setRealHttpMessage(realHttpMessage.setInline(f.toFile))
    this

  def setDeflateContentEncoding(bufferSize: Int): this.type =
    setRealHttpMessage(realHttpMessage.setDeflateContentEncoding(bufferSize))
    this

  def setGzipContentEncoding(bufferSize: Int): this.type =
    setRealHttpMessage(realHttpMessage.setGzipContentEncoding(bufferSize))
    this
