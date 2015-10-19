package java;

public interface AI {
    Decision getDecision();
    void receiveEnemyDecision( Decision enemyDecision );
}
