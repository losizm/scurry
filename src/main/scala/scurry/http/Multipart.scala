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

import scala.collection.mutable.ListBuffer
import scala.jdk.javaapi.CollectionConverters.asJava

import scamper.http.multipart.Part as ScamperPart

/** Encapsulates multipart message body. */
class Multipart private[scurry] (mulitpart: ScamperMultipart):
  /**
   * Creates multipart from supplied parts.
   *
   * @note If `parts` is nonempty, then multipart is closed.
   */
  def this(parts: Array[AnyRef]) =
    this(null : ScamperMultipart)
    if parts.nonEmpty then
      addParts(parts*)
      close()

  private class Part(part: ScamperPart):
    def getName(): String = part.name
    def getType(): String = part.contentType.fullName
    def getSize(): Long = part.size
    def getFileName(): String = part.fileName.getOrElse(null)
    def getString(): String = part.getString()
    def getBytes(): Array[Byte] = part.getBytes()
    def getFile(): File = part.getFile()
    override def toString(): String = part.toString()
    private[Multipart] def toScamperPart: ScamperPart = part

  private var multipart: ScamperMultipart = null
  private val queue = ListBuffer[ScamperPart]()

  /** Tests closed. */
  def isClosed(): Boolean =
    multipart != null

  /**
   * Closes multipart.
   *
   * @note Additional parts cannot be added after multipart is closed.
   */
  def close(): this.type = synchronized {
    if multipart == null then
      multipart = ScamperMultipart(queue.toSeq)
      queue.clear()
    this
  }

  /**
   * Adds part.
   *
   * @throws IllegalStateException if multipart is closed
   */
  def addPart(part: AnyRef): this.type = synchronized {
    if multipart != null then
      throw IllegalStateException("multipart is closed")
    part match
      case null              => throw NullPointerException("part")
      case part: ScamperPart => queue += part
      case part: Part        => queue += part.toScamperPart
      case part: JMap[?, ?]  => queue += toScamperPart(asMap[String, AnyRef](part))
      case _                 => 
        println(part)
        println(part.getClass)
        throw IllegalArgumentException("Invalid part")
    this
  }

  /**
   * Adds parts.
   *
   * @throws IllegalStateException if multipart is closed
   */
  @annotation.varargs
  def addParts(parts: AnyRef*): this.type = synchronized {
    parts.foreach(addPart)
    this
  }

  /**
   * Gets part with given name.
   *
   * @param name part name
   *
   * @throws IllegalStateException if multipart is not closed
   */
  def getPart(name: String): AnyRef =
    toScamperMultipart.getPart(name)
      .map(Part(_))
      .getOrElse(null)

  /**
   * Gets all parts with given name.
   *
   * @param name part name
   *
   * @throws IllegalStateException if multipart is not closed
   */
  def getParts(name: String): JList[AnyRef] =
    toList(toScamperMultipart.getParts(name), Part(_))

  /** Gets iterator to parameters. */
  def iterator(): JIterator[AnyRef] =
    asJava(toScamperMultipart.parts.map(Part(_)).iterator)

  private[scurry] def toScamperMultipart: ScamperMultipart =
    if multipart == null then
      throw IllegalStateException("multipart is not closed")
    multipart
