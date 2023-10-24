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

import java.lang.{ Boolean as JBoolean, Integer as JInteger }
import java.io.{ BufferedReader, File, InputStream, OutputStream, Reader }
import java.nio.file.Path
import java.util.{ HashMap as JHashMap, LinkedList as JLinkedList }

import scamper.http.{ BodyParser, HttpMessage, HttpRequest, HttpResponse }

/** Encapsulates HTTP message body. */
class Body private[scurry](res: HttpMessage):
  /**
   * Gets decoded message body as `String`.
   *
   * @param length maximum length of body
   */
  def toString(length: Int): String =
    res.as(using BodyParser.string(length))

  /**
   * Gets decoded message body as `Array[Byte]`.
   *
   * @param length maximum length of body
   */
  def toBytes(length: Int): Array[Byte] =
    res.as(using BodyParser.bytes(length))

  /**
   * Gets decoded message body as `InputStream`.
   *
   * @param length maximum length of body
   */
  def toInputStream(length: Long): InputStream =
    res.as(using BodyParser.stream(length.max(Int.MaxValue).toInt))

  /**
   * Gets decoded message body as `BufferedReader`.
   *
   * @param length maximum length of body
   */
  def toReader(length: Long): BufferedReader =
    res.as(using BodyParser.reader(length.max(Int.MaxValue).toInt))

  /**
   * Writes decoded message body to `File`.
   *
   * If `dest` is directory, then new file is created in directory.
   *
   * @param dest file location
   * @param length maximum length of body
   *
   * @return file to which message body is written
   */
  def toFile(dest: File, length: Long): File =
    res.as(using BodyParser.file(dest, length))

  /**
   * Writes decoded message body to `File`.
   *
   * If `dest` is directory, then new file is created in directory.
   *
   * @param dest file location
   * @param length maximum length of body
   *
   * @return file to which message body is written
   */
  def toPath(dest: Path, length: Long): Path =
    toFile(dest.toFile, length).toPath
