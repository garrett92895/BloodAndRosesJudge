package scala

import java.io._
import java.lang.reflect.InvocationTargetException
import java.net.{URL, URLClassLoader}
import java.util
import java.util.jar.{JarEntry, JarFile}

/**
 * Amature implementation of src/java/BloodAndRosesJudge.java
 * Created by stephen on 10/18/15.
 */
class JudgeOfBAR(val file: File) {

  def run = {
    try {
      val jarFile = new JarFile(file)
      val arry: Array[URL] = new Array[URL](1)
      arry.update(0, file.toURI.toURL)
      val cl = new URLClassLoader(arry)

      this.invokeMain(jarFile, cl)
      this.setSystemStreams()
    }
    catch {
      case e:NoSuchMethodException => System.err.println("No main method was found")
      case e:IOException => e.printStackTrace()
    }
  }

  private def invokeMain(jarFile: JarFile, classLoader: URLClassLoader) : Unit = {
    val entries: util.Enumeration[JarEntry] = jarFile.entries()
    while (entries.hasMoreElements) {
      val element = entries.nextElement()

      val elementName: String = element.getName
      if (elementName.endsWith(".class")) {
        val className = elementName.replaceAll(".class", "").replaceAll("/", ".")

        val driverClass = classLoader.loadClass(className)
        var mainCalled = false
        for (method <- driverClass.getMethods if !mainCalled) {
          if (method.getName.equalsIgnoreCase("main")) {
            mainCalled = true

            new Thread(new Runnable {
              override def run(): Unit = {

                try {
                  method.invoke(null, new Array[String](1))
                }
                catch {
                  case e: InvocationTargetException =>
                  case e: IllegalAccessException => e.printStackTrace()
                }
              }
            }).start()
          }
        }
      }
    }
  }

  private def setSystemStreams() : Unit = {
    val outputStream = new PipedOutputStream()
    val inputStream = new PipedInputStream(outputStream)
    val printStream = new PrintStream(outputStream)

    System.setOut(printStream)
    System.setIn(inputStream)
  }

}
