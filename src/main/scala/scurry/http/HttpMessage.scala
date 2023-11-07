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

import scamper.http.MessageBuilder
import scamper.http.cookies.{ toRequestCookies, toResponseCookies }

/** Encapsulates HTTP message. */
sealed abstract class HttpMessage private[scurry] ():
  private[scurry] type RealMessageType <: RealHttpMessage

  /** Gets message start line. */
  def getStartLine(): String =
    realHttpMessage.startLine.toString

  /** Gets HTTP version. */
  def getHttpVersion(): String =
    realHttpMessage.version.toString

  /** Gets message headers. */
  def getHeaders(): Headers =
    Headers(realHttpMessage.headers)

  /** Gets cookies. */
  def getCookies(): Cookies

  /** Gets message body. */
  def getBody(): Body =
    Body(realHttpMessage)

  private[scurry] def realHttpMessage: RealMessageType

  private[scurry] def setRealHttpMessage(msg: RealMessageType): Unit

  private[scurry] def realMessageBuilder: MessageBuilder[RealMessageType] =
    realHttpMessage.asInstanceOf[MessageBuilder[RealMessageType]]

/** Encapsulates HTTP request. */
class HttpRequest private[scurry] (private var req: RealHttpRequest) extends HttpMessage:
  /**
   * Creates HTTP request from supplied request.
   *
   * {{{
   *  import scurry.http.HttpRequest
   *
   *  def request = new HttpRequest(
   *    method: "POST",
   *    target: "https://api.example.com/messages",
   *    headers: [
   *      "Content-Type": "text/plain; charset=UTF-8",
   *      "Authorization": "Bearer 94c2f320-7120-4338-8e40-42bc2581dd05"
   *    ],
   *    cookies: [
   *     [name: "sessionid", value: "230148A1-030E-4680-B55E-743B6ABBA6FA"],
   *     [name: "region", value: "us-se"]
   *    ],
   *    // Supply body as Array[Byte], String, File, Path, InputStream, Reader,
   *    // QueryString, Multipart, or BodyWriter
   *    body: "Hello, world!"
   *  )
   * }}}
   *
   * @see [[Body]], [[BodyWriter]], [[Multipart]], [[QueryString]]
   */
  def this(req: JMap[String, AnyRef]) =
    this(toRealHttpRequest(req))

  private[scurry] type RealMessageType = RealHttpRequest

  private lazy val query = QueryString(req.query)
  private lazy val cookies = Cookies(req.cookies)

  /** Gets request method. */
  def getMethod(): String =
    req.method.toString

  /** Gets target URI. */
  def getTarget(): String =
    req.target.toString

  /** Gets target path. */
  def getPath(): String =
    req.path

  /** Gets query string. */
  def getQuery(): QueryString =
    query

  /** Gets cookies. */
  def getCookies(): Cookies =
    cookies

  private[scurry] def realHttpMessage: RealHttpRequest = req

  private[scurry] def setRealHttpMessage(req: RealHttpRequest): Unit =
    this.req = req

/** Encapsulates HTTP response. */
class HttpResponse  private[scurry] (private var res: RealHttpResponse) extends HttpMessage:
  /**
   * Creates HTTP response from supplied response.
   *
   * {{{
   *  import java.time.Instant
   *  import scurry.http.HttpResponse
   *
   *  def now = Instant.now()
   *  def file = './path/to/example.json' as File
   *  def lastModified = file.lastModified()
   *
   *  def response = new HttpResponse(
   *    statusCode: 200,
   *    headers: [
   *      "Content-Type": "application/json",
   *      "Content-Length": file.size(),
   *      "Last-Modified": Instant.ofEpochMilli(lastModified),
   *      "Date": now
   *    ],
   *    cookies: [
   *     [
   *       name: "sessionid",
   *       value: "230148A1-030E-4680-B55E-743B6ABBA6FA",
   *       domain: ".example.com",
   *       secure: true,
   *       expires: now + 300
   *     ],
   *     [name: "region", value: "us-se", domain: ".example.com"]
   *    ],
   *    // Supply body as Array[Byte], String, File, Path, InputStream, Reader,
   *    // QueryString, Multipart, or BodyWriter
   *    body: file
   *  )
   * }}}
   *
   * @see [[Body]], [[BodyWriter]], [[Multipart]], [[QueryString]]
   */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res))

  private[scurry] type RealMessageType = RealHttpResponse

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

  private[scurry] def realHttpMessage: RealHttpResponse = res

  private[scurry] def setRealHttpMessage(res: RealHttpResponse): Unit =
    this.res = res
