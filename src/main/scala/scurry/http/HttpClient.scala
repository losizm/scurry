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

import scamper.http.RequestMethod
import scamper.http.client.ClientSettings as ScamperClientSettings
import scamper.http.cookies.RequestCookies
import RequestMethod.Registry.*

import settings.*

/**
 * Defines HTTP client.
 *
 * @define asterisk *
 */
class HttpClient private[scurry] (settings: ScamperClientSettings):
  /**
   * Creates HTTP client using supplied settings.
   *
   * ### Client Settings
   *
   * The following provides example settings:
   *
   * ```
   * [
   *   accept: '$asterisk/$asterisk',
   *   acceptEncodings: ['applications/json', '$asterisk/$asterisk; q=0.1'],
   *   bufferSize: 8192,
   *   readTimeout: 15000,
   *   continueTimeout: 1000,
   *   keepAliveEnabled: false,
   *   cookieStoreEnabled: true,
   *   // Used to resolve non-absolute URLs
   *   resolveTo: [
   *     host: 'api.example.com',
   *     port: 443,
   *     secure: true
   *   ],
   *   // Used to verify SSL/TLS certificates
   *   trust: [
   *     truststore: '/path/to/example.jks',
   *     type: 'jks',
   *     password: 'letmein'
   *   ]
   * ]
   * ```
   *
   * @param settings client settings
   */
  def this(settings: JMap[String, AnyRef]) = this(toScamperClientSettings(settings))

  /** Creates HTTP client using default settings. */
  def this() = this(ScamperClientSettings())

  private lazy val httpClient = settings.toHttpClient()
  private lazy val currentSettings = ActiveClientSettings(httpClient)

  /** Gets client settings. */
  def getSettings(): ClientSettings =
    currentSettings

  /**
   * Sends request.
   *
   * ### HTTP Request
   *
   * The following provides example HTTP request:
   *
   * ```
   * [
   *   method: 'POST',
   *   url: 'https://api.example.com/messages',
   *   headers: [
   *     'Content-Type': 'application/json',
   *     'Authorization': 'Bearer 94c2f320-7120-4338-8e40-42bc2581dd05'
   *   ],
   *   // Supply body as Array[Byte], String, File, Path, InputStream, Reader,
   *   // QueryString, Multipart, or BodyWriter
   *   body: '''{ "to": ["Peter", "Mary"], "text": "Hello, friends!"] }'''
   * ]
   * ```
   *
   * ### HTTP Response
   *
   * The following provides example HTTP response:
   *
   * ```
   * [
   *   httpVersion: 'HTTP/1.1',
   *   statusCode: 200,
   *   reasonPhrase: 'OK',
   *   headers: [
   *     'Content-Type': 'application/json',
   *     'Content-Length': 45,
   *     'Date': 'Mon, 23 Oct 2023 16:11:12 GMT',
   *     'Connection': 'close'
   *   ],
   *   body: ... // See scurry.http.Body
   * ]
   * ```
   *
   * @param req outgoing request
   * @param handler response handler
   *
   * @see [[Body]], [[BodyWriter]], [[Multipart]], [[QueryString]]
   */
  def send[T](req: AnyRef, handler: HttpResponse => T): T =
    sendRequest(req, None, handler)

  /**
   * Sends GET request.
   *
   * @param req outgoing request
   * @param handler response handler
   *
   * @see [[send]]
   */
  def get[T](req: AnyRef, handler: HttpResponse => T): T =
    sendRequest(req, Some(Get), handler)

  /**
   * Sends POST request.
   *
   * @param req outgoing request
   * @param handler response handler
   *
   * @see [[send]]
   */
  def post[T](req: AnyRef, handler: HttpResponse => T): T =
    sendRequest(req, Some(Post), handler)

  /**
   * Sends PUT request.
   *
   * @param req outgoing request
   * @param handler response handler
   *
   * @see [[send]]
   */
  def put[T](req: AnyRef, handler: HttpResponse => T): T =
    sendRequest(req, Some(Put), handler)

  /**
   * Sends DELETE request.
   *
   * @param req outgoing request
   * @param handler response handler
   *
   * @see [[send]]
   */
  def delete[T](req: AnyRef, handler: HttpResponse => T): T =
    sendRequest(req, Some(Delete), handler)

  /**
   * Sends WebSocket request.
   *
   * @param req outgoing request
   * @param handler websocket session handler
   */
  def websocket[T](req: AnyRef, handler: WebSocket => T): T =
    val real = toRealRequest(req, Some(Get))
    httpClient.websocket(real.target, real.headers, real.cookies) { session =>
      handler(WebSocket(session))
    }

  private def sendRequest[T](req: AnyRef, method: Option[RequestMethod], handler: HttpResponse => T): T =
    val real = toRealRequest(req, method)
    httpClient.send(real) { res => handler(HttpResponse(res)) }

  private def toRealRequest(req: AnyRef, method: Option[RequestMethod]): ScamperHttpRequest =
    val real = req match
      case req: ScamperHttpRequest => req
      case req: HttpRequest        => req.scamperHttpMessage
      case req: JMap[?, ?]         => toScamperHttpRequest(asMap[String, AnyRef](req))
      case _                       => throw IllegalArgumentException("Invalid request")

    method.map(real.setMethod).getOrElse(real)
