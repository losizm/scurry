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

class HttpRequestSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val sh = GroovyShell()

  it should "create request" in {
    info("creating original request")
    val req = sh.evaluate("""
      def req = new scurry.http.HttpRequest(
        method: 'POST',
        target: 'https://www.example.com/api/test?debug=true&level=3',
        authorization: [
          scheme: 'basic',
          user: 'guest',
          password: 'letmein'
        ],
        headers: [
          'Content-Type': 'application/json',
          'Content-Length': 16,
          Connection: 'close'
        ],
        cookies: [
          [name: 'sessionid', value: '30c35e72-7fb3-4768-be43-552a75b1f775'],
          [name: 'lang', value: 'en-US']
        ],
        body: '{ "key": "abc" }'
      )

      assert(req.httpVersion == 'HTTP/1.1')
      assert(req.method == 'POST')
      assert(req.target == 'https://www.example.com/api/test?debug=true&level=3')
      assert(req.headers.size() == 5)
      assert(req.headers['Content-Type'] == 'application/json')
      assert(req.headers['Content-Length'] == '16')
      assert(req.headers['Connection'] == 'close')
      assert(req.headers['Cookie'] == 'sessionid=30c35e72-7fb3-4768-be43-552a75b1f775; lang=en-US')
      assert(req.headers['Authorization'] == 'Basic Z3Vlc3Q6bGV0bWVpbg==')
      assert(req.cookies.size() == 2)
      assert(req.cookies.sessionid.name == 'sessionid')
      assert(req.cookies.sessionid.value == '30c35e72-7fb3-4768-be43-552a75b1f775')
      assert(req.cookies.lang.name == 'lang')
      assert(req.cookies.lang.value == 'en-US')
      assert(req.body.toString(80) == '{ "key": "abc" }')
      req
    """).asInstanceOf[HttpRequest]


    info("creating client side request")
    val clientSideRequest = ClientSideHttpRequest(req.realHttpMessage)
    sh.setVariable("req", clientSideRequest)
    sh.evaluate("""
      assert(req.httpVersion == 'HTTP/1.1')
      assert(req.method == 'POST')
      assert(req.target == 'https://www.example.com/api/test?debug=true&level=3')
      assert(req.headers.size() == 5)
      assert(req.headers['Content-Type'] == 'application/json')
      assert(req.headers['Content-Length'] == '16')
      assert(req.headers['Connection'] == 'close')
      assert(req.headers['Cookie'] == 'sessionid=30c35e72-7fb3-4768-be43-552a75b1f775; lang=en-US')
      assert(req.headers['Authorization'] == 'Basic Z3Vlc3Q6bGV0bWVpbg==')
      assert(req.cookies.size() == 2)
      assert(req.cookies.sessionid.name == 'sessionid')
      assert(req.cookies.sessionid.value == '30c35e72-7fb3-4768-be43-552a75b1f775')
      assert(req.cookies.lang.name == 'lang')
      assert(req.cookies.lang.value == 'en-US')
      assert(req.authorization.scheme == 'basic')
      assert(req.authorization.user == 'guest')
      assert(req.authorization.password == 'letmein')
      assert(req.authorization.token == 'Z3Vlc3Q6bGV0bWVpbg==')

      req.authorization = [scheme: 'bearer', token: '12ec1858-5a87-4b0f-b8b7-a63453be4185']
      assert(req.authorization.scheme == 'bearer')
      assert(req.authorization.token == '12ec1858-5a87-4b0f-b8b7-a63453be4185')
    """)

    info("creating side request")
    val serverSideRequest = ServerSideHttpRequest(req.realHttpMessage)
    sh.setVariable("req", serverSideRequest)
    sh.evaluate("""
      assert(req.httpVersion == 'HTTP/1.1')
      assert(req.method == 'POST')
      assert(req.target == 'https://www.example.com/api/test?debug=true&level=3')
      assert(req.headers.size() == 5)
      assert(req.headers['Content-Type'] == 'application/json')
      assert(req.headers['Content-Length'] == '16')
      assert(req.headers['Connection'] == 'close')
      assert(req.headers['Cookie'] == 'sessionid=30c35e72-7fb3-4768-be43-552a75b1f775; lang=en-US')
      assert(req.headers['Authorization'] == 'Basic Z3Vlc3Q6bGV0bWVpbg==')
      assert(req.cookies.size() == 2)
      assert(req.cookies.sessionid.name == 'sessionid')
      assert(req.cookies.sessionid.value == '30c35e72-7fb3-4768-be43-552a75b1f775')
      assert(req.cookies.lang.name == 'lang')
      assert(req.cookies.lang.value == 'en-US')
      assert(req.authorization.scheme == 'basic')
      assert(req.authorization.user == 'guest')
      assert(req.authorization.password == 'letmein')
      assert(req.authorization.token == 'Z3Vlc3Q6bGV0bWVpbg==')

      req.authorization = [scheme: 'basic', token: 'bm9ib2R5OnNlY3JldA==']
      assert(req.authorization.scheme == 'basic')
      assert(req.authorization.user == 'nobody')
      assert(req.authorization.password == 'secret')
      assert(req.authorization.token == 'bm9ib2R5OnNlY3JldA==')
    """)
  }
