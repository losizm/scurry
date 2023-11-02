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

import scamper.http.auth.{ Authorization as RealAuthorization, * }

private trait Authorization:
  req: HttpRequest =>

  def getAuthorization(): AnyRef =
    import scamper.http.auth.Authorization

    req.realHttpMessage.authorizationOption match
      case None        => null
      case Some(value) => toCredentials(value)

  def setAuthorization(credentials: AnyRef): HttpRequest =
    import scamper.http.auth.Authorization

    credentials match
      case value: Credentials => req.setRealHttpMessage(req.realHttpMessage.setAuthorization(value))
      case value: JMap[?, ?]  => req.setRealHttpMessage(req.realHttpMessage.setAuthorization(toRealCredentials(asJMap(value))))
      case value              => throw IllegalArgumentException(s"Invalid credentials: (${value.getClass})")

    req

  def removeAuthorization(): HttpRequest =
    import scamper.http.auth.Authorization

    req.setRealHttpMessage(req.realHttpMessage.authorizationRemoved)
    req

  private def toCredentials(value: Credentials): AnyRef =
    val creds = new JLinkedHashMap[String, AnyRef]

    value match
      case value: BasicCredentials =>
        creds.put("scheme", "basic")
        creds.put("user", value.user)
        creds.put("password", value.password)
        creds.put("token", value.token)

      case value: BearerCredentials =>
        creds.put("scheme", "bearer")
        creds.put("token", value.token)

    creds
