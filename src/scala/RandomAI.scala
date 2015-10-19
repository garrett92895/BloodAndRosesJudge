package scala

import java.{Decision, AI}
import java.util.Random

/**
 * Scala implementation of the src/java/BogoAI.java
 * Created by stephen on 10/18/15.
 */
class RandomAI(var rand: Random = new Random()) extends AI {
  override def getDecision: Decision =
    if (rand.nextInt(2) == 0) Decision.Blood else Decision.Rose

  override def receiveEnemyDecision(enemyDecision: Decision): Unit = {
    // Intentionally left blank, for reason explained twice previously
  }
}
