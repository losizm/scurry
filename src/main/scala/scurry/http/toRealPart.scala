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
import java.nio.file.Path

import scamper.http.types.{ DispositionType, MediaType }

private object toRealPart extends Converter:
  def apply(map: JMap[String, AnyRef]): RealPart =
    val name = map.requireString("name")
    val fileName = map.optionString("fileName")

    val contentType = map.optionString("contentType")
      .map(value => convert("contentType", value, MediaType(_)))

    map.get("content") match
      case null                => bad("Missing part content")
      case value: CharSequence => toPart(name, value.toString, fileName, contentType)
      case value: Array[Byte]  => toPart(name, value, fileName, contentType)
      case value: File         => toPart(name, value, fileName, contentType)
      case value: Path         => toPart(name, value.toFile, fileName, contentType)
      case value               => bad(s"Invalid content type: ${value.getClass}")

  private def toPart(name: String, content: String, fileName: Option[String], contentType: Option[MediaType]): RealPart =
    RealPart(
      getDispositionType(name, fileName),
      contentType.getOrElse(MediaType.plain),
      content
    )

  private def toPart(name: String, content: Array[Byte], fileName: Option[String], contentType: Option[MediaType]): RealPart =
    RealPart(
      getDispositionType(name, fileName),
      contentType.getOrElse(MediaType.octetStream),
      content
    )

  private def toPart(name: String, content: File, fileName: Option[String], contentType: Option[MediaType]): RealPart =
    val effectiveFileName = fileName.getOrElse(content.getName)
    RealPart(
      getDispositionType(name, Some(effectiveFileName)),
      contentType.getOrElse(getMediaType(effectiveFileName)),
      content
    )

  private def getDispositionType(name: String, fileName: Option[String]): DispositionType =
    fileName match
      case Some(value) => DispositionType("form-data", "name" -> name, "filename" -> value)
      case None        => DispositionType("form-data", "name" -> name)

  private def getMediaType(fileName: String): MediaType =
    MediaType.forFileName(fileName).getOrElse(MediaType.octetStream)
