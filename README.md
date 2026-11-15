# Lab 10: Data-flow analysis

This project keeps `src/main/java/mn/edu/must/sqat/DataFlowTargets.java` unchanged and uses the file line numbers from that source as the reference for the control-flow and DU analysis.

The two methods are:
- `gcd(int x, int y)`
- `bonus(int[] attendance, int threshold)`

The lab uses the following file-line mapping for the `gcd` method:
- `13`: `int x, int y` definition
- `15`: `while (y != 0)`
- `16`: `tmp = x % y`
- `17`: `x = y`
- `18`: `y = tmp`
- `20`: `return x`

## GCD: complete DU table

The implemented tests focus on the classic loop pattern and the zero-divisor exit case. The most important point is that `gcd(7, 0)` triggers the loop guard only once and therefore exercises the direct `x` and `y` definitions that reach the final return path without a loop body.

| Variable | Def line | Use line | Kind | Def-clear path | Test ID | Covered |
|---|---:|---:|---|---|---|---|
| x | 13 | 16 | c-use | 13 -> 15 -> 16 | GCD-01 | yes |
| x | 13 | 20 | c-use | 13 -> 15(false) -> 20 | GCD-01 | yes |
| x | 17 | 16 | c-use | 17 -> 18 -> 15 -> 16 | GCD-02 | yes |
| x | 17 | 20 | c-use | 17 -> 18 -> 15(false) -> 20 | GCD-02 | yes |
| y | 13 | 15 | p-use (T/F) | 13 -> 15 | GCD-01 | yes |
| y | 13 | 16 | c-use | 13 -> 15 -> 16 | GCD-02 | yes |
| y | 18 | 15 | p-use (T/F) | 18 -> 15 | GCD-02 | yes |
| y | 18 | 16 | c-use | 18 -> 15 -> 16 | GCD-02 | yes |
| tmp | 16 | 18 | c-use | 16 -> 17 -> 18 | GCD-02 | yes |

Notes:
- `gcd(12, 8)` performs two loop iterations; the `tmp` assignment at line 16 must reach the `y = tmp` statement at line 18 before the next guard check.
- `gcd(7, 0)` is the only case that reaches the final return directly; it is the canonical example of a def-clear path with no body iteration.
- `gcd(0, 5)` verifies the path where `x` is zero and the loop still begins with `y != 0` as the control predicate.

### GCD tests created
- `GCD-01`: zero-divisor exit path (`gcd(7, 0)`)
- `GCD-02`: loop-body propagation (`gcd(12, 8)`)
- `GCD-03`: zero-valued first parameter (`gcd(0, 5)`)

## Bonus: control-flow summary

The `bonus` method contains several mutually dependent variables and repeated definitions; hence all-defs coverage and all-DU-pairs coverage must be validated across multiple executions.

The most important definitions are:
- `present` at line 35 and at line 40
- `streak` at line 36, at line 41, and at line 46
- `best` at line 37 and at line 43
- `score` at line 49, at line 51, and at line 54

The semantics of the control flow are:
- `for (int a : attendance)` creates a loop with a p-use on `attendance` and a defining use of the iterator `a` on each iteration.
- `if (a > 0)` splits the path into the present and absent cases.
- `if (present >= threshold)` controls the `score = 5` def.
- `if (best >= 5)` controls the `score = score + 3` def.

The key invariant is that a single execution cannot necessarily reach every branch for every variable, so the all-defs test suite uses several concrete arrays to exercise the positive, reset, and invalid-input paths.

### Bonus DU-focused test IDs
- `BONUS-01`: `present` grows, `streak` grows, `best` updates, threshold branch is true
- `BONUS-02`: threshold branch is false but the streak branch is still true
- `BONUS-03`: reset path with `streak = 0` and zero threshold behaviour
- `BONUS-04`: invalid input (`null` and negative threshold)

### Important data-flow rule for this method
The `streak = 0` assignment at line 46 is a kill for the prior `streak` def. Any def-use pair that crosses the `else` arm and then continues through the next iteration is considered def-clear only if the path does not re-enter the killed definition. In other words, the `streak` value defined before the `else` is not allowed to reach a later use once the `else` assignment resets it.

## Test implementation

The actual JUnit tests are stored in:
- `src/test/java/mn/edu/must/sqat/GcdTest.java`
- `src/test/java/mn/edu/must/sqat/BonusTest.java`

The test names and `@DisplayName` values use only Latin letters so they remain readable in the Windows console and in Maven output.

## Verification

`mvn verify` was executed successfully and the JaCoCo report was generated under `target/site/jacoco/`.

This project intentionally keeps `DataFlowTargets.java` unchanged. The required data-flow coverage is validated through tests that exercise the control-flow paths and the def-use chains rather than by mutating the production code.
