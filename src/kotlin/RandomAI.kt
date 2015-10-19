package kotlin

import java.AI
import java.Decision
import java.util.*

/**
 * Kotlin implementation of src/java/BogoAI.java
 * Created by stephen on 10/18/15.
 */
public class RandomAI(val rand: Random = Random()) : AI
{
    override fun getDecision() : Decision
            = (if (rand.nextInt(2) == 0) Decision.Blood else Decision.Rose)

    override fun receiveEnemyDecision(enemyDecision : Decision)
    {
        // Random AI's ignore logic and have this worrisome, short-term memory
        // loss. *sigh* C'est la vie... In brief, their actions make no sense
    }
}