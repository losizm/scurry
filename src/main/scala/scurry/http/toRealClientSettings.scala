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
import javax.net.ssl.TrustManager

import scala.collection.mutable.ListBuffer

import scamper.http.types.{ stringToMediaRange, stringToContentCodingRange }
import scamper.http.client.ClientSettings

private object toRealClientSettings extends Converter:
  def apply(map: JMap[String, AnyRef]): ClientSettings =
    val settings = ClientSettings()
    map.optionInt("bufferSize").foreach(settings.bufferSize)
    map.optionInt("continueTimeout").foreach(settings.continueTimeout)
    map.optionInt("readTimeout").foreach(settings.readTimeout)
    map.optionBoolean("keepAliveEnabled").foreach(settings.keepAlive)
    map.optionBoolean("cookieStoreEnabled").foreach(if _ then settings.cookies())
    settings.accept(toSeq(map, "accept", stringToMediaRange))
    settings.acceptEncoding(toSeq(map, "acceptEncoding", stringToContentCodingRange))
    setResolveTo(map, settings)
    setTrust(map, settings)
    settings

  private def toSeq[Out](map: JMap[String, AnyRef], name: String, converter: String => Out): Seq[Out] =
    val list = ListBuffer[Out]()
    Option(map.get(name)).map {
      case value: CharSequence => list += convert(name, value.toString, converter)
      case values: JList[?]    =>
        values.forEach {
          case value: CharSequence => list += convert(name, value.toString, converter)
          case _                   => bad(name)
        }
      case _ => bad(name)
    }
    list.toSeq

  private def setResolveTo(map: JMap[String, AnyRef], settings: ClientSettings): Unit =
    Option(map.get("resolveTo")).foreach {
      case value: JMap[?, ?]   =>
        try
          (value.get("host"), value.get("port"), value.get("secure")) match
            case (null, null, null)                               => // Ignore
            case (host: CharSequence, null,           secure: JBoolean) => settings.resolveTo(host.toString, None, secure)
            case (host: CharSequence, port: JShort,   secure: JBoolean) => settings.resolveTo(host.toString, port.intValue, secure)
            case (host: CharSequence, port: JInteger, secure: JBoolean) => settings.resolveTo(host.toString, port, secure)
        catch case cause: Exception => bad("resolveTo", cause)
      case _ => bad("resolveTo")
    }

  private def setTrust(map: JMap[String, AnyRef], settings: ClientSettings): Unit =
    Option(map.get("trust")).foreach {
      case value: TrustManager => settings.trust(value)
      case value: JMap[?, ?]   =>
        try
          (value.get("truststore"), value.get("type"), value.get("password")) match
            case (null, null, null)                                   => // Ignore
            case (truststore: CharSequence, kind: CharSequence, password: CharSequence) => settings.trust(File(truststore.toString), kind.toString, Option(password.toString))
            case (truststore: File,         kind: CharSequence, password: CharSequence) => settings.trust(truststore, kind.toString, Option(password.toString))
            case (truststore: Path,         kind: CharSequence, password: CharSequence) => settings.trust(truststore.toFile, kind.toString, Option(password.toString))
        catch case cause: Exception => bad("trust", cause)
      case _ => bad("trust")
    }
