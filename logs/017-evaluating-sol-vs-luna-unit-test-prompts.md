# Evaluating GPT-5.6 Sol and Luna Unit-Test Prompts

All four branches start from commit `532c3f2` and change only the same six files under `src/test`. Conditions 1/1b use the basic prompt; 2/2b use the structured test-design prompt.

## Results

| Branch | Model | Prompt | Partition coverage | Boundary coverage | Error-path coverage | Redundancy | Tests | Execution | Found duration bug |
| --- | --- | --- | ---: | ---: | ---: | ---: | ---: | --- | --- |
| `exp-unit-test-1` | Sol | Basic | 64/73 (87.7%) | 19/30 (63.3%) | 26/34 (76.5%) | 1/26 (3.8%) | 26 | Compiles; 25 pass, 1 fails on bug | Yes |
| `exp-unit-test-2` | Sol | Structured | **72/73 (98.6%)** | **29/30 (96.7%)** | **34/34 (100%)** | 4/56 (7.1%) | 56 | Compiles; 55 pass, 1 fails on bug | Yes |
| `exp-unit-test-1b` | Luna | Basic | 60/73 (82.2%) | 18/30 (60.0%) | 26/34 (76.5%) | 1/26 (3.8%) | 26 | Compiles; all 26 pass | No |
| `exp-unit-test-2b` | Luna | Structured | 70/73 (95.9%) | 24/30 (80.0%) | 32/34 (94.1%) | **1/35 (2.9%)** | 35 | Compiles; 34 pass, 1 fails on bug | Yes |

The failure is a confirmed production defect: an oversized numeric duration leaks `NumberFormatException` instead of the parser's intended `QuorumException`.

## Findings

- **1b vs 2b:** Luna improved substantially with the structured prompt: +13.7 percentage points in partitions, +20.0 in boundaries, and +17.6 in error paths. It also changed from missing the duration bug to exposing it, at the cost of 9 more tests.
- **1 vs 1b:** With the basic prompt, Sol covered four more partitions and one more boundary point. Error-path coverage was equal, but only Sol found the duration bug.
- **2 vs 2b:** Sol's structured suite was the most thorough: +2 partitions, +5 boundary points, and +2 error paths over Luna. Luna used 35 rather than 56 test methods and had lower redundancy, so it was more compact but less complete.
- **Prompt effect:** Structured test-design instructions substantially improved both models' behavioural input coverage.

Overall, `exp-unit-test-2` is the strongest suite for thoroughness. `exp-unit-test-2b` offers the better test-count/coverage trade-off. One run per condition is only a project-specific case study, so this does not establish general Sol-versus-Luna superiority.

## Scoring notes

- The fixed manual rubric contains 73 equivalence partitions, 30 boundary points, and 34 error paths across the six requested targets. A point counts only when an outcome-sensitive assertion exercises it; confirmed bug-revealing tests still count.
- Boundary scoring uses the closest representable values. For example, `WakefulnessPlanner`'s 14-hour boundary uses `14h - 1ns`, `14h`, and `14h + 1ns`.
- Redundancy is behavioural only: a test method is redundant if removing it leaves the suite's partition, boundary, and error-path coverage unchanged. The rate is redundant test methods divided by total test methods; it does not measure unique mutant detection.
- The manual rubric was created after the generated branches existed, so its scores should be described as retrospective.
