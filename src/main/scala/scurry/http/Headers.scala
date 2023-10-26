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

import java.lang.{ Integer as JInteger, Long as JLong }
import java.time.Instant
import java.util.{ Collections, Date, Iterator as JIterator, LinkedList as JLinkedList }

import scala.jdk.javaapi.CollectionConverters.asJava

import scamper.http.*

/** Encapsulates HTTP headers. */
class Headers private[scurry] (headers: Seq[Header]):
  /**
   * Creates headers from supplied fields.
   *
   * @param fields header fields
   */
  def this(fields: JMap[String, AnyRef]) =
    this(toScamperHeaders(fields))

  /**
   * Gets header value.
   *
   * @param name header name
   */
  def get(name: String): String =
    headers.getHeaderValueOrElse(name, null)

  /**
   * Gets header value as `Integer`.
   *
   * @param name header name
   */
  def getInteger(name: String): JInteger =
    headers.getHeader(name)
      .map(_.intValue)
      .map(JInteger.valueOf)
      .getOrElse(null)

  /**
   * Gets header value as `Long`.
   *
   * @param name header name
   */
  def getLong(name: String): JLong =
    headers.getHeader(name)
      .map(_.longValue)
      .map(JLong.valueOf)
      .getOrElse(null)

  /**
   * Gets header value as `Instant`.
   *
   * @param name header name
   */
  def getInstant(name: String): Instant =
    headers.getHeader(name)
      .map(_.dateValue)
      .getOrElse(null)

  /**
   * Gets header value as `Date`.
   *
   * @param name header name
   */
  def getDate(name: String): Date =
    headers.getHeader(name)
      .map(_.dateValue)
      .map(Date.from)
      .getOrElse(null)

  /**
   * Gets header values.
   *
   * @param name header name
   */
  def getValues(name: String): JList[String] =
    headers.getHeaderValues(name).foldLeft(JLinkedList()) { (values, value) =>
      values.add(value)
      values
    }

  /** Gets iterator to headers. */
  def iterator(): JIterator[AnyRef] =
    asJava(headers.iterator)

  private[scurry] def toScamperHeaders: Seq[Header] =
    headers
