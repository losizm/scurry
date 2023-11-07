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

import scamper.http.RequestMethod
import scamper.http.client.{ ClientSettings as RealClientSettings, HttpClient as RealHttpClient, * }
import scamper.http.cookies.toRequestCookies
import RequestMethod.Registry.*

import settings.*

/**
 * Defines HTTP client.
 *
 * @define asterisk *
 */
class HttpClient private[scurry] (settings: RealClientSettings):
  /**
   * Creates HTTP client using supplied settings.
   *
   * ### Client Settings
   *
   * The following provides example settings:
   *
   * {{{
   *  [
   *    accept: "$asterisk/$asterisk",
   *    acceptEncodings: ["applications/json", "$asterisk/$asterisk; q=0.1"],
   *    bufferSize: 8192,
   *    readTimeout: 15000,
   *    continueTimeout: 1000,
   *    keepAliveEnabled: false,
   *    cookieStoreEnabled: true,
   *    // Used to resolve non-absolute request targets
   *    resolveTo: [
   *      host: "api.example.com",
   *      port: 443,
   *      secure: true
   *    ],
   *    // Used to verify SSL/TLS certificates
   *    trust: [
   *      truststore: "/path/to/example.jks",
   *      type: "jks",
   *      password: "letmein"
   *    ]
   *  ]
   * }}}
   *
   * @param settings client settings
   */
  def this(settings: JMap[String, AnyRef]) = this(toRealClientSettings(settings))

  /** Creates HTTP client using default settings. */
  def this() = this(RealClientSettings())

  private var httpClient = settings.toHttpClient()
  private var currentSettings = ActiveClientSettings(httpClient)
  private val cookies = PersistentCookies(httpClient.cookies)


  /** Gets client settings. */
  def getSettings(): ClientSettings =
    currentSettings

  /** Gets persistent cookies. */
  def getCookies(): PersistentCookies =
    cookies

  /**
   * Adds request filter.
   *
   * @param filter request filter
   *
   * @return this
   */
  def outgoing(filter: HttpRequest => AnyRef): this.type = synchronized {
    settings.outgoing(toRequestFilter(filter))
    httpClient = settings.cookies(cookies.realCookieStore).toHttpClient()
    currentSettings = ActiveClientSettings(httpClient)
    this
  }

  /**
   * Adds response filter.
   *
   * @param filter response filter
   *
   * @return this
   */
  def incoming(filter: HttpResponse => AnyRef): this.type = synchronized {
    settings.incoming(toResponseFilter(filter))
    httpClient = settings.cookies(cookies.realCookieStore).toHttpClient()
    currentSettings = ActiveClientSettings(httpClient)
    this
  }

  /**
   * Sends request.
   *
   * @see [[HttpRequest]]
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

  private def toRealRequest(req: AnyRef, method: Option[RequestMethod]): RealHttpRequest =
    val real = req match
      case req: RealHttpRequest => req
      case req: HttpRequest     => req.realHttpMessage
      case req: JMap[?, ?]      => toRealHttpRequest(asJMap(req))
      case value                => throw IllegalArgumentException(s"Invalid request (${value.getClass})")
    method.map(real.setMethod).getOrElse(real)

  private def toRequestFilter(filter: HttpRequest => AnyRef): RequestFilter =
    req => filter(ClientSideHttpRequest(req)) match
      case null                 => throw NullPointerException("Request from request filter")
      case msg: RealHttpRequest => msg
      case msg: HttpRequest     => msg.realHttpMessage
      case msg: JMap[?, ?]      => toRealHttpRequest(asJMap(msg))
      case msg                  => throw IllegalArgumentException(s"Request from request filter (${msg.getClass})")

  private def toResponseFilter(filter: HttpResponse => AnyRef): ResponseFilter =
    res => filter(ClientSideHttpResponse(res)) match
      case null                  => throw NullPointerException("Response from response filter")
      case msg: RealHttpResponse => msg
      case msg: HttpResponse     => msg.realHttpMessage
      case msg: JMap[?, ?]       => toRealHttpResponse(asJMap(msg))
      case msg                   => throw IllegalArgumentException(s"Response from response filter (${msg.getClass})")
