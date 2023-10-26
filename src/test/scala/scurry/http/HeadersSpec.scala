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

import java.time.Instant.parse as Instant
import java.util.Date.from as Date

import groovy.lang.GroovyShell

class HeadersSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val sh = GroovyShell()

  it should "create headers" in {
    val headers = sh.evaluate("""
      new scurry.http.Headers(
        'Content-Type': 'application/json',
        'Content-Length': 1024,
        'Set-Cookie': ['session-id=1C52182F-A682-42E7-8F8D-ED1A63540A90', 'language=en_US'],
        Date: 'Wed, 25 Oct 2023 17:59:02 GMT',
        Connection: 'close',
      )
    """).asInstanceOf[Headers]

    assert(headers.get("content-type") == "application/json")
    assert(headers.getValues("Content-Type").size == 1)
    assert(headers.getValues("Content-Type").get(0) == "application/json")

    assert(headers.get("content-length") == "1024")
    assert(headers.getInteger("content-length") == 1024)
    assert(headers.getLong("content-length") == 1024L)
    assert(headers.getValues("Content-Length").size == 1)
    assert(headers.getValues("Content-Length").get(0) == "1024")

    assert(headers.get("SET-COOKIE") == "session-id=1C52182F-A682-42E7-8F8D-ED1A63540A90")
    assert(headers.getValues("Set-Cookie").size == 2)
    assert(headers.getValues("Set-Cookie").get(0) == "session-id=1C52182F-A682-42E7-8F8D-ED1A63540A90")
    assert(headers.getValues("Set-Cookie").get(1) == "language=en_US")

    assert(headers.get("Date") == "Wed, 25 Oct 2023 17:59:02 GMT")
    assert(headers.getDate("Date") == Date(Instant("2023-10-25T17:59:02Z")))
    assert(headers.getInstant("Date") == Instant("2023-10-25T17:59:02Z"))
    assert(headers.getValues("Date").size == 1)
    assert(headers.getValues("Date").get(0) == "Wed, 25 Oct 2023 17:59:02 GMT")

    assert(headers.get("Connection") == "close")
    assert(headers.getValues("connection").size == 1)
    assert(headers.getValues("connection").get(0) == "close")
  }
