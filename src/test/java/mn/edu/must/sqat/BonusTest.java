package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BonusTest {

    @Test
    @DisplayName("BONUS-AD-01: definitions reach uses on true threshold and five-day streak paths")
    void allDefsThresholdAndStreakTrue() {
        assertEquals(8,
                DataFlowTargets.bonus(new int[] {1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5}, 5));
    }

    @Test
    @DisplayName("BONUS-AD-02: score@49 reaches score@54 when threshold is false")
    void allDefsScoreZeroReachesUse() {
        assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 6));
    }
}
