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

import scamper.http.client.ClientSettings

/**
 * Defines HTTP client.
 *
 * @define asterisk *
 */
class HttpClient private[scurry] (settings: ClientSettings):
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
   *   keepAlive: false,
   *   storeCookies: true,
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
  def this() = this(ClientSettings())

  private lazy val httpClient = settings.toHttpClient()

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
   *   // Supply body as String, Array[Byte], File, Path, InputStream, Reader,
   *   // or scurry.http.BodyWriter
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
   * @see [[Body]], [[BodyWriter]]
   */
  def send[T](req: JMap[String, AnyRef], handler: JMap[String, AnyRef] => T): T =
    httpClient.send(toScamperHttpRequest(req)) { res =>
      handler(HttpMessageReader(res))
    }
