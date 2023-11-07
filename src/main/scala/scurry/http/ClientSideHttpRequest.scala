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

import scala.language.implicitConversions

import scamper.http.client.toClientHttpRequest

private class ClientSideHttpRequest(req: RealHttpRequest) extends HttpRequest(req) with ClientSideHttpMessage with Authorization:
  def this(req: JMap[String, AnyRef]) =
    this(toRealHttpRequest(req))

  def setDeflateContentEncoding(bufferSize: Int): this.type =
    setRealHttpMessage(realHttpMessage.setDeflateContentEncoding(bufferSize))
    this

  def setGzipContentEncoding(bufferSize: Int): this.type =
    setRealHttpMessage(realHttpMessage.setGzipContentEncoding(bufferSize))
    this
