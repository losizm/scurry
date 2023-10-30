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
package settings

/** Defines client settings. */
trait ClientSettings:
  /** Gets accepted content types. */
  def getAccept(): JList[String]

  /** Gets accepted content encodings. */
  def getAcceptEncoding(): JList[String]

  /** Gets buffer size. */
  def getBufferSize(): Int

  /** Gets read timeout. */
  def getReadTimeout(): Int

  /** Gets continue timeout. */
  def getContinueTimeout(): Int

  /** Tests for keep-alive enabled. */
  def isKeepAliveEnabled(): Boolean

  /** Tests for cookie store enabled. */
  def isCookieStoreEnabled(): Boolean
