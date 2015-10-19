public interface AI {
    public Decision getDecision();
    public void receiveEnemyDecision(Decision enemyDecision);
}
