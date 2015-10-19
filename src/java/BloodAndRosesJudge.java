package java;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BloodAndRosesJudge {
    private File file;

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
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void invokeMain(JarFile jar, URLClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            JarEntry element = entries.nextElement();

            if (element.getName().endsWith(".class")) {

                //Load the class In the Jar File.
                String className = element.getName().replaceAll(".class", "").replaceAll("/", ".");

                Class<?> driverClass = classLoader.loadClass(className);

                //Check all methods for main.
                boolean mainCalled = false;
                for (int i = 0; i < driverClass.getMethods().length && !mainCalled; i++) {
                    if (driverClass.getMethods()[i].getName().equalsIgnoreCase("main")) {
                        mainCalled = true;
                        Method mainMethod = driverClass.getMethod("main", String[].class);

                        Thread enemyThread = new Thread(() -> {
                            try {
                                mainMethod.invoke(null, (Object) new String[1]);
                            }
                            catch (IllegalAccessException | InvocationTargetException e) {
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
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream(outputStream);
        PrintStream ps = new PrintStream(outputStream);

        System.setOut(ps);
        System.setIn(inputStream);
    }
}
