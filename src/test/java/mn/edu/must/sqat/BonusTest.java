package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BonusTest {

    @Test
    @DisplayName("BONUS-01: all-defs, present streak and score true paths; streak resets after a five-day run")
    void allDefsThresholdAndStreakTrue() {
        assertEquals(8,
                DataFlowTargets.bonus(new int[] {1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5}, 5));
    }

    @Test
    @DisplayName("BONUS-02: score@49 reaches score@54 when threshold is false and best is at least five")
    void allDefsScoreZeroReachesUse() {
        assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("BONUS-03: present@35 reaches line 50 false; score@49 reaches return; best remains below five")
    void initialDefinitionsReachFalsePredicates() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {0}, 1));
    }

    @Test
    @DisplayName("BONUS-04: streak@46 reaches streak@41 and the true outcome at line 42")
    void resetStreakCanGrowAgain() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {0, 1}, 2));
    }

    @Test
    @DisplayName("BONUS-05: five separated presences must not produce a five-day best streak")
    void interruptedStreaksDoNotReachFive() {
        assertEquals(0,
                DataFlowTargets.bonus(new int[] {1, 0, 1, 0, 1, 0, 1, 0, 1}, 6));
    }

    @Test
    @DisplayName("BONUS-06: invalid null attendance and negative threshold throw IllegalArgumentException")
    void invalidInputsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 2));
        assertThrows(IllegalArgumentException.class,
                () -> DataFlowTargets.bonus(new int[] {1, 2, 3}, -1));
    }

    @Test
    @DisplayName("BONUS-07: present@40 must not replace streak@41 when best@43 is updated")
    void separatedPresencesKillWrongBestDefinition() {
        assertEquals(0,
                DataFlowTargets.bonus(new int[] {1, 0, 1, 0, 1, 0, 1, 1}, 6));
    }

    @Test
    @DisplayName("BONUS-08: score@51 reaches return when threshold is zero and best is below five")
    void thresholdZeroWithoutAttendanceReturnsFive() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {}, 0));
    }
}
