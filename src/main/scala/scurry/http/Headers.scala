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
import java.util.{ Date, LinkedList as JLinkedList }
import java.util.stream.Stream

import scamper.http.*

/** Defines HTTP headers. */
class Headers private[scurry] (headers: Seq[Header]):
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
      .map(JInteger(_))
      .getOrElse(null)

  /**
   * Gets header value as `Long`.
   *
   * @param name header name
   */
  def getLong(name: String): JLong =
    headers.getHeader(name)
      .map(_.longValue)
      .map(JLong(_))
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

  /**
   * Generates stream of mapped headers using supplied function.
   *
   * @param f mapper function
   */
  def map[T](f: (String, String) => T): Stream[T] =
    headers.foldLeft(Stream.builder[T]()) { (stream, header) =>
      stream.add(f(header.name, header.value))
    }.build()

  /**
   * Iterates over headers passing each name and value to supplied function.
   *
   * @param f function
   */
  def forEach[T](f: (String, String) => T): Unit =
    headers.foreach { header => f(header.name, header.value) }
