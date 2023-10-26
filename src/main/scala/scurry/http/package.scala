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

/** Defines type alias to `java.util.Map`. */
private type JMap[K, V] = java.util.Map[K, V]

/** Defines type alias to `java.util.List`. */
private type JList[T] = java.util.List[T]

/** Casts map type parameters. */
private def toMap[K, V](map: JMap[?, ?]): JMap[K, V] =
  map.asInstanceOf[JMap[K, V]]

/** Casts list type parameters. */
private def toList[T](list: JList[?]): JList[T] =
  list.asInstanceOf[JList[T]]
