package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GcdTest {

    @Test
    @DisplayName("GCD-01: x@13 -> 20 and y@13 -> 15 false path for gcd(7, 0)")
    void gcdZeroDivisorUsesInitialDefs() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("GCD-02: x@13 -> 16, y@13/18 -> 16/17, x@17 -> 16/20 for gcd(12, 8)")
    void gcdLoopBodyCoversMutationAndBackEdge() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }

    @Test
    @DisplayName("GCD-03: x@13 -> 16 and x@17 -> 20 for gcd(0, 5)")
    void gcdZeroValueInFirstParameter() {
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }
}
