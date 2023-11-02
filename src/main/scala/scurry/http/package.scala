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

import java.util.LinkedList as JLinkedList

import scala.collection.mutable.ListBuffer

/**
 * Defines type alias to `scamper.http.BodyWriter`.
 *
 * A body writer has the following functional interface:
 *
 * {{{
 *  trait BodyWriter:
 *    def write(out: OutputStream): Unit
 * }}}
 */
type BodyWriter = scamper.http.BodyWriter

private type JBoolean = java.lang.Boolean
private type JShort = java.lang.Short
private type JInteger = java.lang.Integer
private type JLong = java.lang.Long

private type JIterator[T] = java.util.Iterator[T]
private type JList[T] = java.util.List[T]
private type JMap[K, V] = java.util.Map[K, V]

private type RealHttpMessage = scamper.http.HttpMessage
private type RealHttpRequest = scamper.http.HttpRequest
private type RealHttpResponse = scamper.http.HttpResponse
private type RealQueryString = scamper.http.QueryString
private type RealHeader = scamper.http.Header
private type RealCookie = scamper.http.cookies.Cookie
private type RealCookieStore = scamper.http.cookies.CookieStore
private type RealMultipart = scamper.http.multipart.Multipart
private type RealPart = scamper.http.multipart.Part
private type RealHttpClient = scamper.http.client.HttpClient
private type RealHttpServer = scamper.http.server.HttpServer
private type RealClientSettings = scamper.http.client.ClientSettings
private type RealServerApplication = scamper.http.server.ServerApplication
private type RealRouter = scamper.http.server.Router

private val RealHttpRequest = scamper.http.HttpRequest
private val RealHttpResponse = scamper.http.HttpResponse
private val RealQueryString = scamper.http.QueryString
private val RealHeader = scamper.http.Header
private val RealMultipart = scamper.http.multipart.Multipart
private val RealPart = scamper.http.multipart.Part
private val RealHttpClient = scamper.http.client.HttpClient
private val RealHttpServer = scamper.http.server.HttpServer

private def toSeq[T](list: JList[T]): Seq[T] =
  val buffer = new ListBuffer[T]
  list.forEach(buffer.+=)
  buffer.toSeq

private def asJMap[K, V](map: JMap[?, ?]): JMap[K, V] =
  map.asInstanceOf[JMap[K, V]]

private def asJList[T](list: JList[?]): JList[T] =
  list.asInstanceOf[JList[T]]

private def toJList[T](list: IterableOnce[T]): JList[T] =
  list.iterator.foldLeft(new JLinkedList[T]) { (jlist, item) =>
    jlist.add(item)
    jlist
  }

private def toJList[In, Out](list: IterableOnce[In], f: In => Out): JList[Out] =
  list.iterator.foldLeft(new JLinkedList[Out]) { (jlist, item) =>
    jlist.add(f(item))
    jlist
  }
