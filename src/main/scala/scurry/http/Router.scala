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

import java.io.File
import java.net.InetAddress
import java.nio.file.Path
import java.util.HashMap as JHashMap
import java.util.concurrent.atomic.AtomicReference

import scala.collection.mutable.ListBuffer

import scamper.http.RequestMethod
import scamper.http.server.{ HttpServer as RealHttpServer, Router as RealRouter, * }
import scamper.http.websocket.WebSocketApplication

/** Defines router. */
abstract class Router private[scurry] (router: RealRouter):
  import Router.*

  /** Gets mount path. */
  def mountPath: String =
    router.mountPath

  /**
   * Resolves router path to its absolute path.
   *
   * @param path router path
   */
  def toAbsolutePath(path: String): String =
    router.toAbsolutePath(path)

  /**
   * Adds request handler.
   *
   * @param handler request handler
   *
   * @return this
   */
  def incoming(handler: HttpRequest => AnyRef): this.type =
    router.incoming(toRequestHandler(handler))
    this

  /**
   * Adds request handler at specified path and request methods.
   *
   * @param path request path
   * @param methods request methods
   * @param handler request handler
   *
   * @return this
   */
  def incoming(path: String, methods: AnyRef, handler: HttpRequest => AnyRef): this.type =
    router.incoming(path, toRequestMethods(methods)*)(toRequestHandler(handler))
    this

  /**
   * Adds request handler for GET requests at specified path.
   *
   * @param path request path
   * @param handler request handler
   *
   * @return this
   */
  def get(path: String, handler: HttpRequest => AnyRef): this.type =
    router.get(path)(toRequestHandler(handler))
    this

  /**
   * Adds request handler for POST requests at specified path.
   *
   * @param path request path
   * @param handler request handler
   *
   * @return this
   */
  def post(path: String, handler: HttpRequest => AnyRef): this.type =
    router.post(path)(toRequestHandler(handler))
    this

  /**
   * Adds request handler for PUT requests at specified path.
   *
   * @param path request path
   * @param handler request handler
   *
   * @return this
   */
  def put(path: String, handler: HttpRequest => AnyRef): this.type =
    router.put(path)(toRequestHandler(handler))
    this

  /**
   * Adds request handler for DELETE requests at specified path.
   *
   * @param path request path
   * @param handler request handler
   *
   * @return this
   */
  def delete(path: String, handler: HttpRequest => AnyRef): this.type =
    router.delete(path)(toRequestHandler(handler))
    this

  /**
   * Adds websocket handler for requests at specified path.
   *
   * @param path request path
   * @param handler websocket handler
   *
   * @return this
   */
  def websocket[T](path: String, handler: WebSocket => T): this.type =
    router.websocket(path)(toWebSocketApplication(handler))
    this

  /**
   * Mounts file server at specified path.
   *
   * @param path request path
   * @param source source directory
   * @param defaults default file names
   *
   * @return this
   */
  @annotation.varargs
  def files(path: String, source: AnyRef, defaults: String*): this.type =
    source match
      case null           => throw NullPointerException("source")
      case source: File   => router.files(path, source, defaults*)
      case source: Path   => router.files(path, source.toFile, defaults*)
      case source: String => router.files(path, File(source), defaults*)
      case value          => throw IllegalArgumentException(s"File required for source (${value.getClass})")
    this

  /**
   * Mounts routing module at specified path.
   *
   * @param path request path
   * @param module routing module
   *
   * @return this
   */
  def route(path: String, module: Router => AnyRef): this.type =
    router.route(path)(toRouterApplication(module))
    this

  /**
   * Adds response filter.
   *
   * @param filter response filter
   *
   * @return this
   */
  def outgoing(filter: HttpResponse => AnyRef): this.type =
    router.outgoing(toResponseFilter(filter))
    this

  /**
   * Adds error handler.
   *
   * @param handler error handler
   *
   * @return this
   */
  def recover(handler: (HttpRequest, Throwable) => AnyRef): this.type =
    router.recover(toErrorHandler(handler))
    this

  /**
   * Adds lifecycle hook.
   *
   * @param hook lifecycle hook
   *
   * @return this
   */
  def trigger[T](hook: AnyRef => T): this.type =
    router.trigger(toLifecyleHook(hook))
    this

private object Router:
  private class RouterWrapper(router: RealRouter) extends Router(router)

  def toRequestMethods(methods: AnyRef): Seq[RequestMethod] =
    methods match
      case null                  => Nil
      case method: String        => Seq(RequestMethod(method))
      case method: RequestMethod => Seq(method)
      case methods: Array[?]     => methods.map(_.asInstanceOf[AnyRef]).flatMap(toRequestMethods).toSeq
      case methods: Seq[?]       => methods.map(_.asInstanceOf[AnyRef]).flatMap(toRequestMethods).toSeq
      case methods: JList[?]     => toSeq(asJList(methods)).flatMap(toRequestMethods).toSeq
      case method                => throw IllegalArgumentException(s"Invalid method (${method.getClass})")

  def toRequestHandler(handler: HttpRequest => AnyRef): RequestHandler =
    req => handler(ServerSideHttpRequest(req)) match
      case null                 => throw NullPointerException("Message from request handler")
      case msg: RealHttpMessage => msg
      case msg: HttpMessage     => msg.realHttpMessage
      case msg: JMap[?, ?]      => toRealHttpMessage(asJMap(msg))
      case msg                  => throw IllegalArgumentException(s"Message from request handler (${msg.getClass})")

  def toResponseFilter(filter: HttpResponse => AnyRef): ResponseFilter =
    res => filter(ServerSideHttpResponse(res)) match
      case null                  => throw NullPointerException("Response from response filter")
      case msg: RealHttpResponse => msg
      case msg: HttpResponse     => msg.realHttpMessage
      case msg: JMap[?, ?]       => toRealHttpResponse(asJMap(msg))
      case msg                   => throw IllegalArgumentException(s"Response from response filter (${msg.getClass})")

  def toWebSocketApplication[T](app: WebSocket => T): WebSocketApplication[T] =
    session => app(WebSocket(session))

  def toRouterApplication(app: Router => AnyRef): RouterApplication =
    router => app(RouterWrapper(router))

  def toErrorHandler(handler: (HttpRequest, Throwable) => AnyRef): ErrorHandler =
    req => { err => handler(ServerSideHttpRequest(req), err) match
      case null                  => throw NullPointerException("Response from error handler")
      case msg: RealHttpResponse => msg
      case msg: HttpResponse     => msg.realHttpMessage
      case msg: JMap[?, ?]       => toRealHttpResponse(asJMap(msg))
      case msg                   => throw IllegalArgumentException(s"Response from error handler (${msg.getClass})")
    }

  def toLifecyleHook[T](hook: AnyRef => T): LifecycleHook =
    new LifecycleHook with CriticalService:
      def process(evt: LifecycleEvent): Unit =
        evt match
          case LifecycleEvent.Start(server) =>
            val obj = JHashMap[String, AnyRef]
            obj.put("type", "start")
            obj.put("server", settings.ActiveServerSettings(server))
            hook(obj)

          case LifecycleEvent.Stop(server) =>
            val obj = JHashMap[String, AnyRef]
            obj.put("type", "stop")
            obj.put("server", settings.ActiveServerSettings(server))
            hook(obj)
