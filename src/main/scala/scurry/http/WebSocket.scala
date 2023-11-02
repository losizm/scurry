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

import java.io.{ InputStream, Reader }
import java.util.concurrent.CompletableFuture

import scala.jdk.javaapi.FutureConverters.asJava

import scamper.http.websocket.{ StatusCode, WebSocketSession }
import StatusCode.Registry.NormalClosure

/**
 * Encapsulates WebSocket connection.
 *
 * ## Status Codes
 *
 * | Status Code | Message                    |
 * |:-----------:|----------------------------|
 * | 1000        | Normal Closure             |
 * | 1001        | Going Away                 |
 * | 1002        | Protocol Error             |
 * | 1003        | Unsupported Data           |
 * | 1004        | Reserved                   |
 * | 1005        | No Status Received         |
 * | 1006        | Abnormal Closure           |
 * | 1007        | Invalid Frame Payload Data |
 * | 1008        | Policy Violation           |
 * | 1009        | Message Too Big            |
 * | 1010        | Mandatory Extension        |
 * | 1011        | Internal Server Error      |
 * | 1015        | TLS Handshake              |
 */
class WebSocket private[scurry] (session: WebSocketSession):
  /** Gets session identifer. */
  def getId(): String =
    session.id

  /** Gets request target. */
  def getTarget(): String =
    session.target.toString

  /** Gets protocol version. */
  def getProtocolVersion(): String =
    session.protocolVersion 

  /** Tests for secure connection. */
  def isSecure(): Boolean =
    session.isSecure

  /**
   * Gets current state of connection.
   *
   * @return `"Pending"`, `"Open"`, or `"Closed"`
   */
  def getState(): String =
    session.state.toString

  /**
   * Gets idle timeout in milliseconds.
   *
   * Timeout of zero disables this option &ndash; i.e., timeout is indefinite.
   *
   * @note If there is no activity for specified duration, then connection is
   * closed with status code `1001 (Going Away)`.
   */
  def getIdleTimeout(): Int =
    session.idleTimeout

  /**
   * Sets idle timeout.
   *
   * Timeout of zero disables this option &ndash; i.e., timeout is indefinite.
   *
   * @param milliseconds idle timeout
   *
   * @return this
   *
   * @note If there is no activity for specified duration, then connection is
   * closed with status code `1001 (Going Away)`.
   */
  def setIdleTimeout(milliseconds: Int): this.type =
    session.idleTimeout(milliseconds)
    this

  /**
   * Gets outgoing message payload limit.
   *
   * @note If message exceeds limit, then message is sent over multiple frames.
   */
  def getPayloadLimit(): Int =
    session.payloadLimit

  /**
   * Sets outgoing message payload limit.
   *
   * @param length payload limit in bytes
   *
   * @return this
   *
   * @note If message exceeds limit, then message is sent over multiple frames.
   */
  def setPayloadLimit(length: Int): this.type =
    session.payloadLimit(length)
    this

  /**
   * Gets incoming message capacity.
   *
   * @note If message exceeds capacity, then connection is closed with status
   * code `1009 (Message Too Big)`.
   */
  def getMessageCapacity(): Int =
    session.messageCapacity

  /**
   * Sets incoming message capacity.
   *
   * @param size message capacity in bytes
   *
   * @return this
   *
   * @note If message exceeds capacity, then connection is closed with status
   * code `1009 (Message Too Big)`.
   */
  def setMessageCapacity(size: Int): this.type =
    session.messageCapacity(size)
    this

  /** Opens connection. */
  def open(): Unit =
    session.open()

  /** Closes connection normally. */
  def close(): Unit =
    session.close(NormalClosure)

  /**
   * Closes connection with supplied status code.
   *
   * @param code status code
   */
  def close(code: Int): Unit =
    session.close(StatusCode(code))

  /**
   * Sends message.
   *
   * @param message outgoing message
   */
  def send(message: AnyRef): Unit =
    message match
      case null                 => throw NullPointerException("message")
      case message: String      => session.send(message)
      case message: Array[Byte] => session.send(message)
      case message: Reader      => session.send(message)
      case message: InputStream => session.send(message)
      case message              => throw IllegalArgumentException(s"Invalid message type: ${message.getClass}")

  /**
   * Sends message asynchronously.
   *
   * @param message outgoing message
   */
  def sendAsync(message: AnyRef): CompletableFuture[Unit] =
    message match
      case null                 => throw NullPointerException("message")
      case message: String      => asJava(session.sendAsync(message)).toCompletableFuture
      case message: Array[Byte] => asJava(session.sendAsync(message)).toCompletableFuture
      case message: Reader      => asJava(session.sendAsync(message)).toCompletableFuture
      case message: InputStream => asJava(session.sendAsync(message)).toCompletableFuture
      case message              => throw IllegalArgumentException(s"Invalid message type: ${message.getClass}")

  /**
   * Sends ping message.
   *
   * @param data application data to accompany ping message
   */
  def ping(data: Array[Byte]): Unit =
    session.ping(data)


  /**
   * Sends ping message asynchronously.
   *
   * @param data application data to accompany ping message
   */
  def pingAsync[T](data: Array[Byte]): CompletableFuture[Unit] =
    asJava(session.pingAsync(data)).toCompletableFuture

  /**
   * Sends pong message.
   *
   * @param data application data to accompany pong message
   */
  def pong(data: Array[Byte]): Unit =
    session.pong(data)

  /**
   * Sends pong message asynchronously.
   *
   * @param data application data to accompany pong message
   */
  def pongAsync[T](data: Array[Byte]): CompletableFuture[Unit] =
    asJava(session.pongAsync(data)).toCompletableFuture

  /**
   * Sets handler for incoming text message.
   *
   * @param handler text message handler
   *
   * @return this
   */
  def onText[T](handler: String => T): this.type =
    session.onText(handler)
    this

  /**
   * Sets handler for incoming binary message.
   *
   * @param handler binary message handler
   *
   * @return this
   */
  def onBinary[T](handler: Array[Byte] => T): this.type =
    session.onBinary(handler)
    this

  /**
   * Sets handler for incoming ping message.
   *
   * @param handler ping message handler
   *
   * @return this
   */
  def onPing[T](handler: Array[Byte] => T): this.type =
    session.onPing(handler)
    this

  /**
   * Sets handler for incoming pong message.
   *
   * @param handler pong message handler
   *
   * @return this
   */
  def onPong[T](handler: Array[Byte] => T): this.type =
    session.onPong(handler)
    this

  /**
   * Sets handler to be notified when session error occurs.
   *
   * @param handler error handler
   *
   * @return this
   */
  def onError[T](handler: Throwable => T): this.type =
    session.onError(handler)
    this

  /**
   * Sets handler to be notified when session closes.
   *
   * @param handler close handler
   *
   * @return this
   */
  def onClose[T](handler: Int => T): this.type =
    session.onClose(code => handler(code.value))
    this
