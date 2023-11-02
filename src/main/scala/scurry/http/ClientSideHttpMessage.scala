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

import java.net.Socket

import scamper.http.client.ClientHttpMessage

import settings.{ ActiveClientSettings, ClientSettings }

private trait ClientSideHttpMessage extends MutableHttpMessage:
  msg: HttpMessage =>

  def getClientSettings(): ClientSettings =
    ActiveClientSettings(realHttpMessage.client)

  def getSocket(): Socket =
    realHttpMessage.socket

  def getCorrelate(): String =
    realHttpMessage.correlate

  def getAbsoluteTarget(): String =
    realHttpMessage.absoluteTarget.toString
