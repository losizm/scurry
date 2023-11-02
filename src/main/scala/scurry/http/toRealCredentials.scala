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

import scamper.http.auth.*

private object toRealCredentials extends Converter:
  def apply(map: JMap[String, AnyRef]): Credentials =
    map.requireString("scheme").toLowerCase match
      case "basic"  => toBasicCredentials(map)
      case "bearer" => toBearerCredentials(map)
      case other    => bad(s"Unrecognized authorization scheme: $other")

  private def toBasicCredentials(map: JMap[String, AnyRef]): Credentials =
    map.optionString("token") match
      case Some(value) => BasicCredentials(value)
      case None        => toBasicCredentialsUserPassword(map)

  private def toBasicCredentialsUserPassword(map: JMap[String, AnyRef]): Credentials =
    val user = map.requireString("user")
    val password = map.requireString("password")
    BasicCredentials(user, password)

  private def toBearerCredentials(map: JMap[String, AnyRef]): Credentials =
    val token = map.requireString("token")
    BearerCredentials(token)
