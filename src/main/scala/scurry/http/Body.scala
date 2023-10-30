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

import java.io.{ BufferedReader, File, InputStream, OutputStream }
import java.nio.file.Path

import scamper.http.BodyParser
import ScamperMultipart.bodyParser as MultipartBodyParser

/** Encapsulates HTTP message body. */
class Body private[scurry](msg: ScamperHttpMessage):
  /**
   * Drains message body.
   *
   * @param length maximum body length
   */
  def drain(length: Long): Unit =
    msg.drain(length)

  /**
   * Drains decoded message body to supplied sink.
   *
   * @param sink output stream
   * @param length maximum body length
   */
  def drain(sink: OutputStream, length: Long): Unit =
    msg.drain(sink, length)

  /**
   * Gets decoded message body as `String`.
   *
   * @param length maximum body length
   */
  def toString(length: Int): String =
    msg.as(using BodyParser.string(length))

  /**
   * Gets decoded message body as `QueryString`.
   *
   * @param length maximum body length
   */
  def toQueryString(length: Int): QueryString =
    QueryString(msg.as(using BodyParser.query(length)))

  /**
   * Gets decoded message body as `Array[Byte]`.
   *
   * @param length maximum body length
   */
  def toBytes(length: Int): Array[Byte] =
    msg.as(using BodyParser.bytes(length))

  /**
   * Gets decoded message body as `InputStream`.
   *
   * @param length maximum body length
   */
  def toInputStream(length: Long): InputStream =
    msg.as(using BodyParser.stream(length))

  /**
   * Gets decoded message body as `BufferedReader`.
   *
   * @param length maximum body length
   */
  def toReader(length: Long): BufferedReader =
    msg.as(using BodyParser.reader(length))

  /**
   * Writes decoded message body to `File`.
   *
   * @param dest file location
   * @param length maximum body length
   *
   * @return file to which message body is written
   * @note If `dest` is directory, then new file with randomly generated name is
   * created in directory.
   */
  def toFile(dest: File, length: Long): File =
    msg.as(using BodyParser.file(dest, length))

  /**
   * Writes decoded message body to `Path`.
   *
   * If `dest` is directory, then new file is created in directory.
   *
   * @param dest file location
   * @param length maximum body length
   *
   * @return file to which message body is written
   */
  def toPath(dest: Path, length: Long): Path =
    toFile(dest.toFile, length).toPath

  /**
   * Gets decoded message body as `Multipart`.
   *
   * @param dest directory location to which file content is stored
   * @param length maximum body length
   */
  def toMultipart(dest: File, length: Long): Multipart =
    Multipart(msg.as(using MultipartBodyParser(dest, length)))

  /**
   * Gets decoded message body as `Multipart`.
   *
   * @param dest directory location to which file content is stored
   * @param length maximum body length
   */
  def toMultipart(dest: Path, length: Long): Multipart =
    toMultipart(dest.toFile, length)
