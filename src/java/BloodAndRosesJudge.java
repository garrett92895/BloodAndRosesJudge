package java;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BloodAndRosesJudge {
    private static final int NUM_OF_GAMES = 50;
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
            this.runSimulation();

        } catch (NoSuchMethodException e) {
            System.err.println("No main method was found");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void runSimulation() {
        int gamesRemaining = NUM_OF_GAMES;
        int gamesEnemyWon = 0;
        Scanner scan = new Scanner(System.in);

        System.out.println("startSimulation");

        for(int i = gamesRemaining; i >= 0; i--) {
            System.out.println("startGame");

            String result = "loss";
            if(this.runGame(scan)) {
                ++gamesEnemyWon;
                result = "win";
            }

            System.out.println("endGame," + result);
        }

        int percentageEnemyWon = (int) Math.floor(gamesEnemyWon / NUM_OF_GAMES);

        System.out.println("endSimulation," + percentageEnemyWon);
    }

    //Returns if enemy won game
    private boolean runGame(Scanner scan) {
        AI ai = this.chooseAI();
        String name = this.chooseName();
        int resourcePoints = 70;
        int enemyResourcePoints = 70;

        while(resourcePoints > 0 && enemyResourcePoints > 0) {
            System.out.println("conflictInitiation," + name);
            Decision decision = ai.getDecision();
            Decision enemyDecision = this.parseDecision(scan.nextLine());

            String result = "blood";
            if(decision.equals(Decision.Blood)) {
                if(enemyDecision.equals(Decision.Blood)) {
                    resourcePoints -= 2;
                    enemyResourcePoints -= 2;
                }else if(enemyDecision.equals(Decision.Rose)) {
                    enemyResourcePoints -= 3;
                }
            } else if(decision.equals(Decision.Rose)) {
                result = "rose";
                if(enemyDecision.equals(Decision.Blood)) {
                    resourcePoints -= 3;
                }else if(enemyDecision.equals(Decision.Rose)) {
                    resourcePoints -= 1;
                    enemyResourcePoints -= 1;
                }
            }
            System.out.println("conflictResult," + name + "," + result);
        }

        boolean gameResult = true;
        if(enemyResourcePoints < 0) {
            gameResult = false;
        }

        return gameResult;
    }

    private Decision parseDecision(String decision) {
        Decision parsedDecision = null;

        if("blood".equals(decision.toLowerCase())) {
            parsedDecision = Decision.Blood;
        }
        else if("rose".equals(decision.toLowerCase())) {
            parsedDecision = Decision.Rose;
        }

        return parsedDecision;
    }

    private AI chooseAI() {
        //TODO
        return new BogoAI();
    }

    private String chooseName() {
        //TODO
        return "player1";
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
