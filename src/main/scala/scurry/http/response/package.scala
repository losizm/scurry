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
package response

/** 100 (Continue) */
class Continue private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 100 (Continue). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 100))

/** 101 (Switching Protocols) */
class SwitchingProtocols private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 101 (Switching Protocols). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 101))

/** 103 (Early Hints) */
class EarlyHints private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 103 (Early Hints). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 103))

/** 200 (OK) */
class Ok private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 200 (OK). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 200))

/** 201 (Created) */
class Created private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 201 (Created). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 201))

/** 202 (Accepted) */
class Accepted private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 202 (Accepted). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 202))

/** 203 (Non-Authoritative Information) */
class NonAuthoritativeInformation private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 203 (Non-Authoritative Information). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 203))

/** 204 (No Content) */
class NoContent private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 204 (No Content). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 204))

/** 205 (Reset Content) */
class ResetContent private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 205 (Reset Content). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 205))

/** 206 (Partial Content) */
class PartialContent private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 206 (Partial Content). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 206))

/** 300 (Multiple Choices) */
class MultipleChoices private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 300 (Multiple Choices). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 300))

/** 301 (Moved Permanently) */
class MovedPermanently private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 301 (Moved Permanently). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 301))

/** 302 (Found) */
class Found private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 302 (Found). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 302))

/** 303 (See Other) */
class SeeOther private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 303 (See Other). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 303))

/** 304 (Not Modified) */
class NotModified private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 304 (Not Modified). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 304))

/** 305 (Use Proxy) */
class UseProxy private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 305 (Use Proxy). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 305))

/** 307 (Temporary Redirect) */
class TemporaryRedirect private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 307 (Temporary Redirect). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 307))

/** 308 (Permanent Redirect) */
class PermanentRedirect private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 308 (Permanent Redirect). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 308))

/** 400 (Bad Request) */
class BadRequest private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 400 (Bad Request). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 400))

/** 401 (Unauthorized) */
class Unauthorized private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 401 (Unauthorized). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 401))

/** 402 (Payment Required) */
class PaymentRequired private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 402 (Payment Required). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 402))

/** 403 (Forbidden) */
class Forbidden private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 403 (Forbidden). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 403))

/** 404 (Not Found) */
class NotFound private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 404 (Not Found). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 404))

/** 405 (Method Not Allowed) */
class MethodNotAllowed private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 405 (Method Not Allowed). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 405))

/** 406 (Not Acceptable) */
class NotAcceptable private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 406 (Not Acceptable). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 406))

/** 407 (Proxy Authentication Required) */
class ProxyAuthenticationRequired private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 407 (Proxy Authentication Required). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 407))

/** 408 (Request Timeout) */
class RequestTimeout private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 408 (Request Timeout). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 408))

/** 409 (Conflict) */
class Conflict private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 409 (Conflict). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 409))

/** 410 (Gone) */
class Gone private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 410 (Gone). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 410))

/** 411 (Length Required) */
class LengthRequired private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 411 (Length Required). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 411))

/** 412 (Precondition Failed) */
class PreconditionFailed private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 412 (Precondition Failed). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 412))

/** 413 (Payload Too Large) */
class PayloadTooLarge private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 413 (Payload Too Large). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 413))

/** 414 (URI Too Long) */
class UriTooLong private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 414 (URI Too Long). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 414))

/** 415 (Unsupported Media Type) */
class UnsupportedMediaType private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 415 (Unsupported Media Type). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 415))

/** 416 (Range Not Satisfiable) */
class RangeNotSatisfiable private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 416 (Range Not Satisfiable). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 416))

/** 417 (Expectation Failed) */
class ExpectationFailed private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 417 (Expectation Failed). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 417))

/** 422 (Unprocessable Entity) */
class UnprocessableEntity private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 422 (Unprocessable Entity). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 422))

/** 425 (Too Early) */
class TooEarly private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 425 (Too Early). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 425))

/** 426 (Upgrade Required) */
class UpgradeRequired private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 426 (Upgrade Required). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 426))

/** 428 (Precondition Required) */
class PreconditionRequired private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 428 (Precondition Required). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 428))

/** 429 (Too Many Requests) */
class TooManyRequests private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 429 (Too Many Requests). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 429))

/** 431 (Request Header Fields Too Large) */
class RequestHeaderFieldsTooLarge private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 431 (Request Header Fields Too Large). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 431))

/** 451 (Unavailable For Legal Reasons) */
class UnavailableForLegalReasons private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 451 (Unavailable For Legal Reasons). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 451))

/** 500 (Internal Server Error) */
class InternalServerError private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 500 (Internal Server Error). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 500))

/** 501 (Not Implemented) */
class NotImplemented private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 501 (Not Implemented). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 501))

/** 502 (Bad Gateway) */
class BadGateway private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 502 (Bad Gateway). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 502))

/** 503 (Service Unavailable) */
class ServiceUnavailable private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 503 (Service Unavailable). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 503))

/** 504 (Gateway Timeout) */
class GatewayTimeout private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 504 (Gateway Timeout). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 504))

/** 505 (HTTP Version Not Supported) */
class HttpVersionNotSupported private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 505 (HTTP Version Not Supported). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 505))

/** 511 (Network Authentication Required) */
class NetworkAuthenticationRequired private[scurry] (res: RealHttpResponse) extends HttpResponse(res):
  /** Creates 511 (Network Authentication Required). */
  def this(res: JMap[String, AnyRef]) =
    this(toRealHttpResponse(res, 511))
