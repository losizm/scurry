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
package settings

import scamper.http.client.HttpClient as ScamperHttpClient
import scamper.http.cookies.CookieStore

private[scurry] class ActiveClientSettings(client: ScamperHttpClient) extends ClientSettings:
  def getAccept(): JList[String] = toList(client.accept, _.toString)
  def getAcceptEncoding(): JList[String] = toList(client.acceptEncoding, _.toString)
  def getBufferSize(): Int = client.bufferSize
  def getReadTimeout(): Int = client.readTimeout
  def getContinueTimeout(): Int = client.continueTimeout
  def isKeepAliveEnabled(): Boolean = client.keepAlive
  def isCookieStoreEnabled(): Boolean = client.cookies == CookieStore.Null
