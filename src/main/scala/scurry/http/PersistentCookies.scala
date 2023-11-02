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

import java.lang.Boolean as JBoolean
import java.net.{ URI, URL }
import java.util.LinkedHashMap as JLinkedHashMap

import scala.jdk.javaapi.CollectionConverters.asJava

import scamper.http.Uri
import scamper.http.cookies.PersistentCookie

/** Provides access to persistent cookies. */
class PersistentCookies private[scurry] (cookies: RealCookieStore):
  /** Tests for empty. */
  def isEmpty(): Boolean =
    cookies.size == 0

  /** Gets cookie count. */
  def size(): Int =
    cookies.size

  /**
   * Clears cookies.
   *
   * @param expiredOnly specifies whether to clear expired cookies only
   *
   * @return this
   */
  def clear(expiredOnly: Boolean): this.type =
    cookies.clear(expiredOnly)
    this

  /**
   * Gets applicable cookies for given target.
   *
   * @param target request target
   */
  def get(target: AnyRef): JList[AnyRef] =
    val targetUri = target match
      case null                => throw NullPointerException("target")
      case value: Uri          => value
      case value: CharSequence => Uri(value.toString)
      case value: URI          => Uri(value.toString)
      case value: URL          => Uri(value.toString)
      case value               => throw IllegalArgumentException(s"URI required for target (${value.getClass})")
    toJList(cookies.get(targetUri))

  /** Gets iterator to cookies. */
  def iterator(): JIterator[AnyRef] =
    asJava(cookies.list.map(toCookie).iterator)

  private[scurry] def realCookieStore: RealCookieStore =
    cookies

  private def toCookie(c: PersistentCookie): JMap[String, AnyRef] =
    val cookie = JLinkedHashMap[String, AnyRef]()
    cookie.put("name", c.name)
    cookie.put("value", c.value)
    cookie.put("domain", c.domain)
    cookie.put("path", c.path)
    cookie.put("secureOnly", JBoolean.valueOf(c.secureOnly))
    cookie.put("hostOnly", JBoolean.valueOf(c.hostOnly))
    cookie.put("httpOnly", JBoolean.valueOf(c.httpOnly))
    cookie.put("persistent", JBoolean.valueOf(c.persistent))
    cookie.put("creation", c.creation)
    cookie.put("lastAccess", c.lastAccess)
    cookie.put("expiry", c.expiry)
    cookie
