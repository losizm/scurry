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

import scamper.http.MessageBuilder
import scamper.http.cookies.{ RequestCookies, ResponseCookies }

/** Encapsulates HTTP message. */
sealed abstract class HttpMessage private[scurry] ():
  private[scurry] type ScamperHttpMessageType <: ScamperHttpMessage

  /** Gets message start line. */
  def getStartLine(): String =
    scamperHttpMessage.startLine.toString

  /** Gets HTTP version. */
  def getHttpVersion(): String =
    scamperHttpMessage.version.toString

  /** Gets message headers. */
  def getHeaders(): Headers =
    Headers(scamperHttpMessage.headers)

  /** Gets cookies. */
  def getCookies(): Cookies

  /** Gets message body. */
  def getBody(): Body =
    Body(scamperHttpMessage)

  private[scurry] def scamperHttpMessage: ScamperHttpMessageType

  private[scurry] def setScamperHttpMessage(msg: ScamperHttpMessageType): Unit

  private[scurry] def scamperMessageBuilder: MessageBuilder[ScamperHttpMessageType] =
    scamperHttpMessage.asInstanceOf[MessageBuilder[ScamperHttpMessageType]]

/** Encapsulates HTTP request. */
class HttpRequest private[scurry] (private var req: ScamperHttpRequest) extends HttpMessage:
  private[scurry] type ScamperHttpMessageType = ScamperHttpRequest

  /** Creates HTTP request from supplied request. */
  def this(req: JMap[String, AnyRef]) =
    this(toScamperHttpRequest(req))

  private lazy val query = QueryString(req.query)
  private lazy val cookies = Cookies(req.cookies)

  /** Gets request method. */
  def getMethod(): String =
    req.method.toString

  /** Gets request target. */
  def getTarget(): String =
    req.target.toString

  /** Gets request path. */
  def getPath(): String =
    req.path

  /** Gets query string. */
  def getQuery(): QueryString =
    query

  /** Gets cookies. */
  def getCookies(): Cookies =
    cookies

  private[scurry] def scamperHttpMessage: ScamperHttpRequest = req

  private[scurry] def setScamperHttpMessage(req: ScamperHttpRequest): Unit =
    this.req = req

/** Encapsulates HTTP response. */
class HttpResponse  private[scurry] (private var res: ScamperHttpResponse) extends HttpMessage:
  private[scurry] type ScamperHttpMessageType = ScamperHttpResponse

  /** Creates HTTP response from supplied response. */
  def this(res: JMap[String, AnyRef]) =
    this(toScamperHttpResponse(res))

  private lazy val cookies = Cookies(res.cookies)

  /** Gets status code. */
  def getStatusCode(): Int =
    res.statusCode

  /** Gets reason phrase. */
  def getReasonPhrase(): String =
    res.reasonPhrase

  /** Tests for informational status code. */
  def isInformational(): Boolean =
    res.isInformational

  /** Tests for successful status code. */
  def isSuccessful(): Boolean =
    res.isSuccessful

  /** Tests for redirection status code. */
  def isRedirection(): Boolean =
    res.isRedirection

  /** Tests for client error status code. */
  def isClientError(): Boolean =
    res.isClientError

  /** Tests for server error status code. */
  def isServerError(): Boolean =
    res.isServerError

  /** Gets cookies. */
  def getCookies(): Cookies =
    cookies

  private[scurry] def scamperHttpMessage: ScamperHttpResponse = res

  private[scurry] def setScamperHttpMessage(res: ScamperHttpResponse): Unit =
    this.res = res
