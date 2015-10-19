package scala

import java.io._
import java.lang.reflect.InvocationTargetException
import java.net.{URL, URLClassLoader}
import java.{Decision, AI, util}
import java.util.Scanner
import java.util.jar.{JarEntry, JarFile}

/**
 * Amateur implementation of src/java/BloodAndRosesJudge.java
 * Created by stephen on 10/18/15.
 */
class JudgeOfBAR(val file: File) {

  val NUM_OF_GAMES : Int = 50

  def run() = {
    try {
      val jarFile = new JarFile(file)
      val arry: Array[URL] = new Array[URL](1)
      arry.update(0, file.toURI.toURL)
      val cl = new URLClassLoader(arry)

      this.invokeMain(jarFile, cl)
      this.setSystemStreams()
      this.runSimulation()
    }
    catch {
      case e:NoSuchMethodException => System.err.println("No main method was found")
      case e:IOException => e.printStackTrace()
    }
  }
  def runSimulation() = {
    val scan = new Scanner(System.in)
    var enemyGamesWon : Short = 0 // no need to waste space
    for (i <- 1 to NUM_OF_GAMES) {
      println("startGame")

      val result = if (this.runGame(scan)) {
        enemyGamesWon += 1
        "win"
      } else "loss"

      println(s"endGame,$result")
    }

    println(s"endSimulation,${Math.round(Math.floor(enemyGamesWon / NUM_OF_GAMES).toFloat)}")
  }

  val BLOOD_STRING: String = "blood"
  val ROSE_STRING: String = "rose"

  def runGame(scan: Scanner): Boolean = {
    val ai = this.chooseAI()
    var resourcePoints: Short = 70
    var enemyResourcePoints: Short = 70

    while (resourcePoints > 0 && enemyResourcePoints > 0) {
      println(s"conflictInitiation,${ai.name}")
      val botDecision = ai.getDecision
      val enemyDecision = Decision.valueOf(scan.nextLine())

      val result = if (botDecision == Decision.Blood) {
        if (enemyDecision == Decision.Blood) {
          val decrement = 2
          resourcePoints -= decrement
          enemyResourcePoints -= decrement
        }
        else {
          // enemy chose the Rose
          enemyResourcePoints -= 3
        }
        BLOOD_STRING
      }
      else {
        // the bot chose the Rose
        if (enemyDecision == Decision.Blood)
          resourcePoints -= 3
        else {
          // enemy chooses rose
          resourcePoints -= 1
          enemyResourcePoints -= 1
        }
        ROSE_STRING
      }
      println(s"conflictResult,${ai.name},$result")
    }
    enemyResourcePoints >= 0
  }

  def chooseAI() : AI with scalAI = {
    // TODO: factory switcher of AI implementations
    new RandomAI()
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
