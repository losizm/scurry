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

import java.lang.{ Boolean as JBoolean, Long as JLong }
import java.time.Instant

class CookiesSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val sh = GroovyShell()

  it should "create cookies" in {
    val cookies = sh.evaluate("""
      import java.time.Instant

      new scurry.http.Cookies(
        [name: 'country', value: 'US'],
        [name: 'language', value: 'en_US'],
        [
          name: 'theme',
          value: 'dark',
          domain: 'losizm.com',
          path: '/',
          expires: Instant.parse('2024-01-01T00:00:00Z'),
          secure: true,
          httpOnly: false
        ],
        [
          name: 'sessionid',
          value: 'f6a0fa9c-9e87-4805-a08f-fa4f03192d72',
          domain: 'losizm.com',
          maxAge: 30 * 60,
          httpOnly: true
        ]
      )
    """).asInstanceOf[Cookies]

    val country = cookies.get("country").asInstanceOf[JMap[String, AnyRef]]
    assert(country.get("name") == "country")
    assert(country.get("value") == "US")
    assert(country.get("domain") == null)
    assert(country.get("path") == null)
    assert(country.get("expires") == null)
    assert(country.get("maxAge") == null)
    assert(country.get("secure") == null)
    assert(country.get("httpOnly") == null)

    val language = cookies.get("language").asInstanceOf[JMap[String, AnyRef]]
    assert(language.get("name") == "language")
    assert(language.get("value") == "en_US")
    assert(language.get("domain") == null)
    assert(language.get("path") == null)
    assert(language.get("expires") == null)
    assert(language.get("maxAge") == null)
    assert(language.get("secure") == null)
    assert(language.get("httpOnly") == null)

    val theme = cookies.get("theme").asInstanceOf[JMap[String, AnyRef]]
    assert(theme.get("name") == "theme")
    assert(theme.get("value") == "dark")
    assert(theme.get("domain") == "losizm.com")
    assert(theme.get("path") == "/")
    assert(theme.get("expires") == Instant.parse("2024-01-01T00:00:00Z"))
    assert(theme.get("maxAge") == null)
    assert(theme.get("secure") == JBoolean.TRUE)
    assert(theme.get("httpOnly") == JBoolean.FALSE)

    val sessionid = cookies.get("sessionid").asInstanceOf[JMap[String, AnyRef]]
    assert(sessionid.get("name") == "sessionid")
    assert(sessionid.get("value") == "f6a0fa9c-9e87-4805-a08f-fa4f03192d72")
    assert(sessionid.get("domain") == "losizm.com")
    assert(sessionid.get("path") == null)
    assert(sessionid.get("expires") == null)
    assert(sessionid.get("maxAge") == JLong.valueOf(30 * 60))
    assert(sessionid.get("secure") == JBoolean.FALSE)
    assert(sessionid.get("httpOnly") == JBoolean.TRUE)
  }
