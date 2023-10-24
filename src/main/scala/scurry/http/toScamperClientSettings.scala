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
import java.lang.{ Boolean as JBoolean, Integer as JInteger, Short as JShort }
import java.nio.file.Path
import java.util.{ HashMap as JHashMap, LinkedList as JLinkedList }
import javax.net.ssl.TrustManager

import scala.collection.mutable.ListBuffer

import scamper.http.types.{ stringToMediaRange, stringToContentCodingRange }
import scamper.http.client.ClientSettings

private object toScamperClientSettings extends Converter:
  def apply(map: JMap[String, AnyRef]): ClientSettings =
    val settings = ClientSettings()
    toInt(map, "bufferSize").foreach(settings.bufferSize)
    toInt(map, "continueTimeout").foreach(settings.continueTimeout)
    toInt(map, "readTimeout").foreach(settings.readTimeout)
    toBoolean(map, "keepAlive").foreach(settings.keepAlive)
    toBoolean(map, "storeCookies").foreach(value => if value then settings.cookies())
    settings.accept(toSeq(map, "accept", stringToMediaRange))
    settings.acceptEncoding(toSeq(map, "acceptEncoding", stringToContentCodingRange))
    setResolveTo(map, settings)
    setTrust(map, settings)
    settings

  private def toInt(map: JMap[String, AnyRef], name: String): Option[Int] =
    Option(map.get(name)).map {
      case value: JShort   => value.intValue
      case value: JInteger => value
      case _               => bad(name)
    }

  private def toBoolean(map: JMap[String, AnyRef], name: String): Option[Boolean] =
    Option(map.get(name)).map {
      case value: JBoolean => value
      case _               => bad(name)
    }

  private def toSeq[Out](map: JMap[String, AnyRef], name: String, converter: String => Out): Seq[Out] =
    val list = ListBuffer[Out]()
    Option(map.get(name)).map {
      case value: String    => list += convert(name, value, converter)
      case values: JList[?] =>
        values.forEach {
          case value: String => list += convert(name, value, converter)
          case _             => bad(name)
        }
      case _ => bad(name)
    }
    list.toSeq

  private def setResolveTo(map: JMap[String, AnyRef], settings: ClientSettings): Unit =
    Option(map.get("resolveTo")).foreach {
      case value: JMap[?, ?]   =>
        try
          (value.get("host"), value.get("port"), value.get("secure")) match
            case (null, null, null) =>
            case (host: String, null,           secure: JBoolean) => settings.resolveTo(host, None, secure)
            case (host: String, port: JShort,   secure: JBoolean) => settings.resolveTo(host, port.intValue, secure)
            case (host: String, port: JInteger, secure: JBoolean) => settings.resolveTo(host, port, secure)
        catch case _: Exception => bad("resolveTo")
      case _ => bad("resolveTo")
    }

  private def setTrust(map: JMap[String, AnyRef], settings: ClientSettings): Unit =
    Option(map.get("trust")).foreach {
      case value: TrustManager => settings.trust(value)
      case value: JMap[?, ?]   =>
        try
          (value.get("truststore"), value.get("type"), value.get("password")) match
            case (null, null, null) =>
            case (truststore: String, kind: String, password: String) => settings.trust(File(truststore), kind, Option(password))
            case (truststore: File,   kind: String, password: String) => settings.trust(truststore, kind, Option(password))
            case (truststore: Path,   kind: String, password: String) => settings.trust(truststore.toFile, kind, Option(password))
        catch case _: Exception => bad("trust")
      case _ => bad("trust")
    }
