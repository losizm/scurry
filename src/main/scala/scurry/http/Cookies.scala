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

import java.lang.{ Boolean as JBoolean, Long as JLong }
import java.util.{ Iterator as JIterator, LinkedHashMap as JHashMap }

import scala.jdk.javaapi.CollectionConverters.asJava

import scamper.http.cookies.{ Cookie as ScamperCookie, SetCookie }

/** Encapsulates HTTP cookies. */
class Cookies private[scurry] (cookies: Seq[ScamperCookie]):
  /** Creates cookies from supplied cookies. */
  def this(cookies: Array[JMap[String, AnyRef]]) =
    this(toScamperCookies(cookies.toSeq))

  /**
   * Gets cookie with given name.
   *
   * @param name cookie name
   *
   * @throws IllegalStateException if cookies are not closed
   */
  def get(name: String): AnyRef =
    toScamperCookies.find(_.name == name)
      .map(toCookie)
      .getOrElse(null)

  /** Gets iterator to cookies. */
  def iterator(): JIterator[AnyRef] =
    asJava(cookies.map(toCookie).iterator)

  private[scurry] def toScamperCookies: Seq[ScamperCookie] =
    if cookies == null then
      throw IllegalStateException("cookies are not closed")
    cookies

  private def toCookie(c: ScamperCookie): JMap[String, AnyRef] =
    val cookie = JHashMap[String, AnyRef]()
    cookie.put("name", c.name)
    cookie.put("value", c.value)
    c match
      case c: SetCookie =>
        cookie.put("domain", c.domain.getOrElse(null))
        cookie.put("path", c.path.getOrElse(null))
        cookie.put("expires", c.expires.getOrElse(null))
        cookie.put("maxAge", c.maxAge.map(JLong.valueOf).getOrElse(null))
        cookie.put("secure", JBoolean.valueOf(c.secure))
        cookie.put("httpOnly", JBoolean.valueOf(c.httpOnly))
      case _ =>
    cookie
