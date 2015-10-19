import java.io.File;
import java.io.IOException;

public class GameDriver {
    public static void main(String[] args) {
        File file = new File(args[0]);
        if(file.exists()) {
            if (file.getName().contains("jar")) {
                BloodAndRosesJudge judge = new BloodAndRosesJudge(file);
                judge.run();
            } else {
                System.out.println("File not a jar file");
            }
        } else {
            System.out.println("File at " + file.getAbsolutePath() + " not found.");
        }

    }
}
