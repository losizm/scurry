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

import groovy.lang.GroovyShell

import scamper.http.ResponseStatus.Registry.Forbidden

class HttpResponseSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val sh = GroovyShell()

  it should "create response" in {
    info("creating original response")
    val res = sh.evaluate("""
      import java.time.Instant

      def res = new scurry.http.HttpResponse(
        statusCode: 401,
        wwwAuthenticate: [
          scheme: 'basic',
          realm: 'Example',
          params: [charset: 'utf-8', region: 'US']
        ],
        headers: [
          'Content-Length': 12,
          Connection: 'close'
        ],
        cookies: [
          [
            name: 'sessionid',
            value: '30c35e72-7fb3-4768-be43-552a75b1f775',
            domain: '.example.com',
            path: '/',
            secure: true,
            httpOnly: false,
            expires: Instant.parse('2023-11-30T23:59:59.999Z')
          ],
          [name: 'lang', value: 'en-US']
        ],
        body: 'Who are you?'
      )

      assert(res.httpVersion == 'HTTP/1.1')
      assert(res.statusCode == 401)
      assert(res.reasonPhrase == 'Unauthorized')
      println res.headers.iterator().join(' *** ')
      assert(res.headers.size() == 6)
      assert(res.headers['Content-Type'] == 'text/plain; charset=UTF-8')
      assert(res.headers['Content-Length'] == '12')
      assert(res.headers['Connection'] == 'close')
      assert(res.headers.getValues('Set-Cookie').size() == 2)
      assert(res.headers['WWW-Authenticate'] == 'Basic realm="Example", charset=utf-8, region=US')
      assert(res.cookies.size() == 2)
      assert(res.cookies.sessionid.name == 'sessionid')
      assert(res.cookies.sessionid.value == '30c35e72-7fb3-4768-be43-552a75b1f775')
      assert(res.cookies.lang.name == 'lang')
      assert(res.cookies.lang.value == 'en-US')
      assert(res.body.toString(80) == 'Who are you?')
      res
    """).asInstanceOf[HttpResponse]


    info("creating client side response")
    val clientSideResponse = ClientSideHttpResponse(res.realHttpMessage)
    sh.setVariable("res", clientSideResponse)
    sh.evaluate("""
      assert(res.httpVersion == 'HTTP/1.1')
      assert(res.statusCode == 401)
      assert(res.reasonPhrase == 'Unauthorized')
      assert(res.headers.size() == 6)
      assert(res.headers['Content-Type'] == 'text/plain; charset=UTF-8')
      assert(res.headers['Content-Length'] == '12')
      assert(res.headers['Connection'] == 'close')
      assert(res.headers.getValues('Set-Cookie').size() == 2)
      assert(res.headers['WWW-Authenticate'] == 'Basic realm="Example", charset=utf-8, region=US')
      assert(res.cookies.size() == 2)
      assert(res.cookies.sessionid.name == 'sessionid')
      assert(res.cookies.sessionid.value == '30c35e72-7fb3-4768-be43-552a75b1f775')
      assert(res.cookies.lang.name == 'lang')
      assert(res.cookies.lang.value == 'en-US')
      assert(res.wwwAuthenticate.scheme == 'basic')
      assert(res.wwwAuthenticate.realm == 'Example')
      assert(res.wwwAuthenticate.params.charset == 'utf-8')
      assert(res.wwwAuthenticate.params.region == 'US')

      res.wwwAuthenticate = [scheme: 'bearer',
        error: 'invalid_token',
        error_description: 'The access token expired'
      ]
      assert(res.wwwAuthenticate.scheme == 'bearer')
      assert(res.wwwAuthenticate.error == 'invalid_token')
      assert(res.wwwAuthenticate.error_description == 'The access token expired')
    """)

    info("creating side response")
    val serverSideResponse = ServerSideHttpResponse(res.realHttpMessage.setStatus(Forbidden))
    sh.setVariable("res", serverSideResponse)
    sh.evaluate("""
      assert(res.httpVersion == 'HTTP/1.1')
      assert(res.statusCode == 403)
      assert(res.reasonPhrase == 'Forbidden')
      println res.headers.iterator().join(' *** ')
      assert(res.headers.size() == 6)
      assert(res.headers['Content-Type'] == 'text/plain; charset=UTF-8')
      assert(res.headers['Content-Length'] == '12')
      assert(res.headers['Connection'] == 'close')
      assert(res.headers.getValues('Set-Cookie').size() == 2)
      assert(res.headers['WWW-Authenticate'] == 'Basic realm="Example", charset=utf-8, region=US')
      assert(res.cookies.size() == 2)
      assert(res.cookies.sessionid.name == 'sessionid')
      assert(res.cookies.sessionid.value == '30c35e72-7fb3-4768-be43-552a75b1f775')
      assert(res.cookies.lang.name == 'lang')
      assert(res.cookies.lang.value == 'en-US')
      assert(res.wwwAuthenticate.scheme == 'basic')
      assert(res.wwwAuthenticate.realm == 'Example')
      assert(res.wwwAuthenticate.params.charset == 'utf-8')
      assert(res.wwwAuthenticate.params.region == 'US')

      res.wwwAuthenticate = [
        scheme: 'bearer',
        error: 'insufficient_scope',
        error_description: 'The access token lacks required permission',
        scope: 'example api'
      ]
      assert(res.wwwAuthenticate.scheme == 'bearer')
      assert(res.wwwAuthenticate.error == 'insufficient_scope')
      assert(res.wwwAuthenticate.error_description == 'The access token lacks required permission')
      assert(res.wwwAuthenticate.scope.size() == 2)
      assert(res.wwwAuthenticate.scope[0] == 'example')
      assert(res.wwwAuthenticate.scope[1] == 'api')
    """)
  }
