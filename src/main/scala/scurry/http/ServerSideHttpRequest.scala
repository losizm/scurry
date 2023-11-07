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

import scamper.http.server.{ PathParameters, toServerHttpRequest }
import scamper.http.types.MediaType

private class ServerSideHttpRequest(req: RealHttpRequest) extends HttpRequest(req) with ServerSideHttpMessage with Authorization:
  def this(req: JMap[String, AnyRef]) =
    this(toRealHttpRequest(req))

  def getPathParameters(): PathParameters =
    realHttpMessage.pathParams

  def continue(): Boolean =
    realHttpMessage.continue()

  def findAccepted(types: JList[String]): String =
    realHttpMessage.findAccepted(toSeq(types).map(MediaType(_)))
      .map(_.toString)
      .getOrElse(null)
