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
import java.util.{ HashMap as JHashMap, LinkedList as JLinkedList }

import scamper.http.QueryString as ScamperQueryString

/** Defines query string. */
class QueryString private[scurry] (query: ScamperQueryString):
  /**
   * Creates query string from supplied encoded query string.
   *
   * @param query encoded query string
   */
  def this(query: String) = this(ScamperQueryString(query))

  /**
   * Creates query string from supplied query parameters.
   *
   * @param params query parameters
   */
  def this(params: JMap[String, AnyRef]) = this(toScamperQueryString(params))


  /** Tests empty. */
  def isEmpty(): Boolean =
    query.isEmpty

  /** Gets parameter names. */
  def getNames(): JList[String] =
    query.names.foldLeft(JLinkedList()) { (names, name) =>
      names.add(name)
      names
    }

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
      .map(JInteger(_))
      .getOrElse(null)

  /**
   * Gets parameter value as `Long`.
   *
   * @param name parameter name
   */
  def getLong(name: String): JLong =
    query.getInt(name)
      .map(JLong(_))
      .getOrElse(null)

  /**
   * Gets parameter values.
   *
   * @param name parameter name
   */
  def getValues(name: String): JList[String] =
    query.getValues(name).foldLeft(JLinkedList()) { (values, value) =>
      values.add(value)
      values
    }

  /** Gets query string as `Map[String, String]`. */
  def toMap(): JMap[String, String] =
    query.toMap.foldLeft(JHashMap()) { (map, param) =>
      map.put(param(0), param(1))
      map
    }

  /** Gets query string as `Map[String, List[String]]`. */
  def toMultiMap(): JMap[String, JList[String]] =
    query.names.foldLeft(JHashMap()) { (map, name) =>
      map.put(name, getValues(name))
      map
    }
