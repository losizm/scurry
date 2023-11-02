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

import java.util.LinkedHashMap as JLinkedHashMap

import scala.jdk.javaapi.CollectionConverters.asJava

import scamper.http.auth.{ WwwAuthenticate as RealWwwAuthenticate, * }

private trait WwwAuthenticate:
  res: HttpResponse =>

  def getWwwAuthenticate(): AnyRef =
    import scamper.http.auth.WwwAuthenticate

    res.realHttpMessage.wwwAuthenticate match
      case Nil   => null
      case value => toChallenge(value.head)

  def setWwwAuthenticate(challenge: AnyRef): HttpResponse =
    import scamper.http.auth.WwwAuthenticate

    challenge match
      case value: Challenge  => res.setRealHttpMessage(res.realHttpMessage.setWwwAuthenticate(value))
      case value: JMap[?, ?] => res.setRealHttpMessage(res.realHttpMessage.setWwwAuthenticate(toRealChallenge(asJMap(value))))
      case value             => throw IllegalArgumentException(s"Invalid challenge: (${value.getClass})")
    res

  def removeWwwAuthenticate(): HttpResponse =
    import scamper.http.auth.WwwAuthenticate

    res.setRealHttpMessage(res.realHttpMessage.wwwAuthenticateRemoved)
    res

  private def toChallenge(value: Challenge): AnyRef =
    val challenge = new JLinkedHashMap[String, AnyRef]

    value match
      case value: BasicChallenge =>
        challenge.put("scheme", "basic")
        challenge.put("realm", value.realm)
        challenge.put("params", asJava(value.params))

      case value: BearerChallenge =>
        challenge.put("scheme", "bearer")
        value.realm.foreach(challenge.put("realm", _))
        value.error.foreach(challenge.put("error", _))
        value.errorDescription.foreach(challenge.put("error_description", _))
        value.errorUri.foreach(challenge.put("error_uri", _))
        challenge.put("scope", toJList(value.scope))

    challenge
