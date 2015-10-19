package kotlin

import java.io.File
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException
import java.net.URLClassLoader
import java.util.jar.JarFile

/**
 * Kotlin, clean-code implementation of the src/java/BloodAndRosesJudge.java
 * Created by stephen on 10/18/15.
 */
class BARJudge(val file: File)
{
    private var enemyThread : Thread? = null

    public fun run()
    {
        val jarFile = JarFile(file)
        val loader = URLClassLoader(Array(1, { int -> file.toURI().toURL() }))
        invokeMain(jar = jarFile, classLoader = loader)
        setSystemStreams()
    }

    fun invokeMain(jar : JarFile, classLoader : URLClassLoader) : Unit
    {
        for (element in jar.entries())
        {
            if (element.name.endsWith(".class"))
            {
                // Load the class from the jar file
                val className = element.name.replace(".class".toRegex(), "").replace("/".toRegex(), ".")

                // deal with null instead of throwing those pesky exceptions
                val driverClass : Class<*>? = classLoader.loadClass(className)
                if (driverClass != null)
                {
                    var mainCalled = false
                    var i = 0
                    while (i < driverClass.methods.size() && !mainCalled)
                    {
                        if (driverClass.methods[i].name.equals("main", ignoreCase = true))
                        {
                            mainCalled = true
                            val mainMethod = driverClass.getMethod("main", Array<String>::class.java)

                            enemyThread = Thread {
                                try
                                {
                                    mainMethod.invoke(null, arrayOfNulls<String>(1) as Any)
                                }
                                catch (e : IllegalAccessException)
                                {
                                    e.printStackTrace()
                                }
                                catch (e : InvocationTargetException)
                                {
                                    e.printStackTrace()
                                }
                            }

                            enemyThread?.start()
                        }
                        i++
                    }
                }
                else
                {
                    System.err.println("The driver class failed to load")
                }
            }
        }
    }

    fun setSystemStreams() : Unit
    {
        val outputStream = PipedOutputStream()
        val inputStream = PipedInputStream(outputStream)
        val ps = PrintStream(outputStream)

        System.setOut(ps)
        System.setIn(inputStream)
    }
}