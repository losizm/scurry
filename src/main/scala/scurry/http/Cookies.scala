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
import java.util.LinkedHashMap as JLinkedHashMap

import scala.jdk.javaapi.CollectionConverters.asJava

import scamper.http.cookies.SetCookie

/** Provides access to HTTP cookies. */
class Cookies private[scurry] (cookies: Seq[RealCookie]):
  /** Creates cookies from supplied cookies. */
  def this(cookies: Array[AnyRef]) =
    this(toRealCookies(cookies.toSeq))

  /** Tests for empty. */
  def isEmpty(): Boolean =
    cookies.isEmpty

  /** Gets cookie count. */
  def size(): Int =
    cookies.size

  /**
   * Gets cookie with given name.
   *
   * @param name cookie name
   *
   * @throws IllegalStateException if cookies are not closed
   */
  def get(name: String): AnyRef =
    realCookies.find(_.name == name)
      .map(toCookie)
      .getOrElse(null)

  /** Gets iterator to cookies. */
  def iterator(): JIterator[AnyRef] =
    asJava(cookies.map(toCookie).iterator)

  private[scurry] def realCookies: Seq[RealCookie] =
    cookies

  private def toCookie(c: RealCookie): JMap[String, AnyRef] =
    val cookie = JLinkedHashMap[String, AnyRef]()
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
