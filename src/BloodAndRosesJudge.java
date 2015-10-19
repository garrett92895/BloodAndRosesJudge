import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BloodAndRosesJudge {
    private File file;
    private Thread enemyThread;
    private PipedInputStream inputStream;
    private PipedOutputStream outputStream;

    public BloodAndRosesJudge(File file) {
        this.file = file;
    }

    public void run() {
        try {
            JarFile jar = new JarFile(file);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURL()});
            this.invokeMain(jar, classLoader);
            this.setSystemStreams();

        } catch (NoSuchMethodException e) {
            System.err.println("No main method was found");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void invokeMain(JarFile jar, URLClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            JarEntry element = entries.nextElement();

            if (element.getName().endsWith(".class")) {

                //Load the class In the Jar File.
                String className = element.getName();
                className.replaceAll(".class", "");
                className.replaceAll("/", ".");

                Class<?> driverClass = classLoader.loadClass(className);

                //Check all methods for main.
                boolean mainCalled = false;
                for (int i = 0; i < driverClass.getMethods().length && !mainCalled; i++) {
                    if (driverClass.getMethods()[i].getName().equalsIgnoreCase("main")) {
                        mainCalled = true;
                        Method mainMethod = driverClass.getMethod("main", String[].class);

                        enemyThread = new Thread(() -> {
                            try {
                                mainMethod.invoke(null, (Object) new String[1]);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });

                        enemyThread.start();
                    }
                }
            }
        }
    }

    private void setSystemStreams() throws IOException {
        outputStream = new PipedOutputStream();
        inputStream = new PipedInputStream(outputStream);
        PrintStream ps = new PrintStream(outputStream);

        System.setOut(ps);
        System.setIn(inputStream);
    }
}
