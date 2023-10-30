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

import scamper.http.multipart.Part as ScamperPart
import scamper.http.types.{ DispositionType, MediaType }

private object toScamperPart extends Converter:
  def apply(map: JMap[String, AnyRef]): ScamperPart =
    val name = Option(map.get("name")).map {
      case value: CharSequence => value.toString
      case _                   => bad("part name")
    }.getOrElse(throw IllegalArgumentException("Missing part name"))

    val fileName = Option(map.get("fileName")).map {
      case value: CharSequence => value.toString
      case _                   => bad("part fileName")
    }

    val contentType = Option(map.get("contentType")).map {
      case value: CharSequence => convert("part contentType", value.toString, MediaType(_))
      case _                   => bad("part contentType")
    }

    map.get("content") match
      case null                => throw IllegalArgumentException("Missing part content")
      case value: CharSequence => toPart(name, value.toString, fileName, contentType)
      case value: Array[Byte]  => toPart(name, value, fileName, contentType)
      case value: File         => toPart(name, value, fileName, contentType)
      case value: Path         => toPart(name, value.toFile, fileName, contentType)
      case _                   => bad("part content")

  private def toPart(name: String, content: String, fileName: Option[String], contentType: Option[MediaType]): ScamperPart =
    ScamperPart(
      getDispositionType(name, fileName),
      contentType.getOrElse(MediaType.plain),
      content
    )

  private def toPart(name: String, content: Array[Byte], fileName: Option[String], contentType: Option[MediaType]): ScamperPart =
    ScamperPart(
      getDispositionType(name, fileName),
      contentType.getOrElse(MediaType.octetStream),
      content
    )

  private def toPart(name: String, content: File, fileName: Option[String], contentType: Option[MediaType]): ScamperPart =
    val effectiveFileName = fileName.getOrElse(content.getName)
    ScamperPart(
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
