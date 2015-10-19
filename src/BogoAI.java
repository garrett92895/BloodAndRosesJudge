import java.util.Random;

public class BogoAI implements AI {
    private Random rand;

    public BogoAI() {
        rand = new Random();
    }
    @Override
    public Decision getDecision() {
        Decision bogoDecision = Decision.Blood;
        int randomChoice = rand.nextInt(2);

        if(randomChoice == 0) {
            bogoDecision = Decision.Rose;
        }

        return bogoDecision;
    }

    @Override
    public void receiveEnemyDecision(Decision enemyDecision) {
        //BogoAI does not care for the past nor the future and lives solely in the present
        //therefore, it will do nothing here
    }
}
