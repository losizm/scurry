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

import groovy.lang.GroovyShell

class MultipartSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val sh = GroovyShell()

  it should "create multipart (1)" in {
    val multipart = sh.evaluate("""
      import java.nio.file.Paths

      new scurry.http.Multipart()
        .addPart(name: 'text', content: 'Hello, world!')
        .addPart(name: 'attachment', content: new File('./images/cat.svg'))
        .addPart(name: 'attachment', content: Paths.get('./images/IMG000234.jpg'), fileName: 'another-cat.jpg')
        .close()
    """).asInstanceOf[Multipart]

    import reflect.Selectable.reflectiveSelectable

    assert(multipart.isClosed())

    assert(multipart.getPart("text").asInstanceOf[{ def getName(): String }].getName() == "text")
    assert(multipart.getPart("text").asInstanceOf[{ def getType(): String }].getType() == "text/plain")
    assert(multipart.getPart("text").asInstanceOf[{ def getString(): String }].getString() == "Hello, world!")
    assert(multipart.getPart("text").asInstanceOf[{ def getFileName(): String }].getFileName() == null)
    assert(multipart.getParts("text").size == 1)
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getName(): String }].getName() == "text")
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getType(): String }].getType() == "text/plain")
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getString(): String }].getString() == "Hello, world!")
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getFileName(): String }].getFileName() == null)

    assert(multipart.getPart("attachment").asInstanceOf[{ def getName(): String }].getName() == "attachment")
    assert(multipart.getPart("attachment").asInstanceOf[{ def getFileName(): String }].getFileName() == "cat.svg")
    assert(multipart.getParts("attachment").size == 2)
    assert(multipart.getParts("attachment").get(0).asInstanceOf[{ def getName(): String }].getName() == "attachment")
    assert(multipart.getParts("attachment").get(0).asInstanceOf[{ def getFileName(): String }].getFileName() == "cat.svg")
    assert(multipart.getParts("attachment").get(1).asInstanceOf[{ def getName(): String }].getName() == "attachment")
    assert(multipart.getParts("attachment").get(1).asInstanceOf[{ def getFileName(): String }].getFileName() == "another-cat.jpg")

    assertThrows[IllegalStateException](multipart.addPart(null))
  }

  it should "create multipart (2)" in {
    val multipart = sh.evaluate("""
      import java.nio.file.Paths

      new scurry.http.Multipart(
        [name: 'text', content: 'Hello, world!'],
        [name: 'attachment', content: new File('./images/cat.svg')],
        [name: 'attachment', content: Paths.get('./images/IMG000234.jpg'), fileName: 'another-cat.jpg']
      )
    """).asInstanceOf[Multipart]

    import reflect.Selectable.reflectiveSelectable

    assert(multipart.isClosed())

    assert(multipart.getPart("text").asInstanceOf[{ def getName(): String }].getName() == "text")
    assert(multipart.getPart("text").asInstanceOf[{ def getType(): String }].getType() == "text/plain")
    assert(multipart.getPart("text").asInstanceOf[{ def getString(): String }].getString() == "Hello, world!")
    assert(multipart.getPart("text").asInstanceOf[{ def getFileName(): String }].getFileName() == null)
    assert(multipart.getParts("text").size == 1)
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getName(): String }].getName() == "text")
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getType(): String }].getType() == "text/plain")
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getString(): String }].getString() == "Hello, world!")
    assert(multipart.getParts("text").get(0).asInstanceOf[{ def getFileName(): String }].getFileName() == null)

    assert(multipart.getPart("attachment").asInstanceOf[{ def getName(): String }].getName() == "attachment")
    assert(multipart.getPart("attachment").asInstanceOf[{ def getFileName(): String }].getFileName() == "cat.svg")
    assert(multipart.getParts("attachment").size == 2)
    assert(multipart.getParts("attachment").get(0).asInstanceOf[{ def getName(): String }].getName() == "attachment")
    assert(multipart.getParts("attachment").get(0).asInstanceOf[{ def getFileName(): String }].getFileName() == "cat.svg")
    assert(multipart.getParts("attachment").get(1).asInstanceOf[{ def getName(): String }].getName() == "attachment")
    assert(multipart.getParts("attachment").get(1).asInstanceOf[{ def getFileName(): String }].getFileName() == "another-cat.jpg")

    assertThrows[IllegalStateException](multipart.addPart(null))
  }
