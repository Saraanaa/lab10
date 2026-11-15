package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BonusTest {

    @Test
    @DisplayName("BONUS-01: present@35 -> 40, streak@36 -> 41, best@37 -> 43, score@49 -> 51 for a full positive streak")
    void bonusAllDefsAndThresholdBranch() {
        assertEquals(8, DataFlowTargets.bonus(new int[] {1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5}, 5));
    }

    @Test
    @DisplayName("BONUS-02: present@35 -> 50 false, best@37 -> 54 true for a streak above five without threshold met")
    void bonusStreakWinsWithoutThreshold() {
        assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("BONUS-03: streak@36 -> 46 kills earlier streak defs and score@49 -> 51 handles zero threshold")
    void bonusResetsStreakAndHandlesEmptyOrZeroCases() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {0, 0, 0, 0}, 0));
    }

    @Test
    @DisplayName("BONUS-04: invalid input throws IllegalArgumentException for null and negative threshold")
    void bonusRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 2));
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[] {1, 2, 3}, -1));
    }
}
