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
import java.util.HashMap as JHashMap

import scala.jdk.javaapi.CollectionConverters.asJava

/** Encapsulates query string. */
class QueryString private[scurry] (query: RealQueryString):
  /**
   * Creates query string from supplied encoded query string.
   *
   * @param query encoded query string
   */
  def this(query: String) = this(RealQueryString(query))

  /**
   * Creates query string from supplied parameters.
   *
   * @param params query parameters
   */
  def this(params: JMap[String, AnyRef]) = this(toRealQueryString(params))


  /** Tests empty. */
  def isEmpty(): Boolean =
    query.isEmpty

  /** Gets parameter names. */
  def getNames(): JList[String] =
    toJList(query.names)

  /**
   * Gets parameter value.
   *
   * @param name parameter name
   */
  def get(name: String): String =
    query.getOrElse(name, null)

  /**
   * Gets parameter value as `Integer`.
   *
   * @param name parameter name
   */
  def getInteger(name: String): JInteger =
    query.getInt(name)
      .map(JInteger.valueOf)
      .getOrElse(null)

  /**
   * Gets parameter value as `Long`.
   *
   * @param name parameter name
   */
  def getLong(name: String): JLong =
    query.getLong(name)
      .map(JLong.valueOf)
      .getOrElse(null)

  /**
   * Gets parameter values.
   *
   * @param name parameter name
   */
  def getValues(name: String): JList[String] =
    toJList(query.getValues(name))

  /** Gets iterator to parameters. */
  def iterator(): JIterator[AnyRef] =
    asJava(query.toSeq.map(toParam).iterator)

  private[scurry] def realQueryString: RealQueryString =
    query

  private def toParam(name: String, value: String): JMap[String, String] =
    val param = new JHashMap[String, String]
    param.put("name", name)
    param.put("value", value)
    param
