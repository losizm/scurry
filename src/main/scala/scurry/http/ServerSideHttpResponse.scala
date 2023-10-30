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
import java.time.Instant
import java.util.Date

import scamper.http.Header
import scamper.http.server.ServerHttpResponse
import scamper.http.types.MediaType

import settings.{ ActiveServerSettings, ServerSettings }

private[scurry] class ServerSideHttpResponse(res: ScamperHttpResponse) extends HttpResponse(res) with ServerSideHttpMessage:
  def this(res: JMap[String, AnyRef]) =
    this(toScamperHttpResponse(res))

  def getRequest(): HttpRequest =
    scamperHttpMessage.request
      .map(HttpRequest(_))
      .getOrElse(null)

  def setAttachment(file: File | Path): Unit =
    file match
      case f: File => setScamperHttpMessage(scamperHttpMessage.setAttachment(f))
      case f: Path => setScamperHttpMessage(scamperHttpMessage.setAttachment(f.toFile))

  def setInline(file: File | Path): Unit =
    file match
      case f: File => setScamperHttpMessage(scamperHttpMessage.setInline(f))
      case f: Path => setScamperHttpMessage(scamperHttpMessage.setInline(f.toFile))

  def setDeflateContentEncoding(bufferSize: Int): Unit =
    setScamperHttpMessage(scamperHttpMessage.setDeflateContentEncoding(bufferSize))

  def setGzipContentEncoding(bufferSize: Int): Unit =
    setScamperHttpMessage(scamperHttpMessage.setGzipContentEncoding(bufferSize))
