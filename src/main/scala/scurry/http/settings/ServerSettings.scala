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

/** Defines server settings. */
trait ServerSettings:
  /** Tests for SSL enabled. */
  def isSslEnabled(): Boolean

  /** Gets host address. */
  def getHost(): String

  /** Gets port number. */
  def getPort(): Int

  /** Gets backlog size. */
  def getBacklogSize(): Int

  /** Gets queue size. */
  def getQueueSize(): Int

  /** Gets pool size. */
  def getPoolSize(): Int

  /** Gets buffer size. */
  def getBufferSize(): Int

  /** Gets read timeout. */
  def getReadTimeout(): Int

  /** Gets header limit. */
  def getHeaderLimit(): Int

  /** Tests for keep-alive enabled. */
  def isKeepAliveEnabled(): Boolean
