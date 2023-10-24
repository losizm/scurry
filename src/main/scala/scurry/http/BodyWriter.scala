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

import java.io.{ OutputStream, OutputStreamWriter, Reader }

/** Defines body writer. */
@FunctionalInterface
trait BodyWriter:
  /**
   * Writes body to given output stream.
   *
   * @param out output stream
   */
  def write(out: OutputStream): Unit

private class ReaderBodyWriter(in: Reader) extends BodyWriter:
  def write(out: OutputStream): Unit =
    val writer = OutputStreamWriter(out)
    val buf = Array[Char](8192)
    var len = 0

    while { len = in.read(buf); len != 0 } do
      writer.write(buf, 0, len)
    in.close()
