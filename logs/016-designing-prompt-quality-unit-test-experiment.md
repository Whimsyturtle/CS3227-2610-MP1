# Designing Prompt Quality Unit Test Experiment

# My Request(s)
## Request 1
Asked for a semi-formal experiment comparing a basic and a detailed prompt for generating unit tests, using the meeting-time feature, and requested suitable quantitative metrics from CS2103 materials.

## Request 2
Asked how the experiment should handle an LLM-generated test that discovers an existing production bug.

## Request 3
Asked whether setting temperature to zero could reduce variability enough to avoid repeated trials under the deadline.

## Request 4
Asked whether the prompts should also cover feature flaws or usability problems, rather than only implementation defects.

## Request 5
Pushed back that the detailed prompt was incomplete and should explicitly incorporate the CS2103 test-case design heuristics.

# Your Response(s)
## Response 1
Recommended a controlled A/B case study framed as low-specification versus structured-testing guidance. Corrected the proposed scope from the nonexistent `FindCommand` to `MeetingCommand`, `WakefulnessPlanner`, meeting parsing, and related value objects. Proposed behavioural obligation, partition, boundary, error-path, mutation, branch, line, and redundancy metrics, while controlling the model, repository state, tools, and conversation context. Recommended repeated runs where feasible, but allowed a single-run exploratory comparison with an explicit validity caveat.

## Response 2
Conceded that requiring every generated suite to pass could suppress legitimate bug-revealing tests. Revised both prompts to require compilation while preserving and reporting tests that appear to expose production defects. Recommended freezing production code, independently classifying failures as confirmed defects, incorrect tests, or specification ambiguities, and fixing confirmed bugs only after all trials.

## Response 3
Explained that Codex does not expose a temperature setting, while API temperature controls would not make an agentic run deterministic. Recommended controlling the model, reasoning effort, Codex version, base commit, permissions, tools, and follow-up procedure. Confirmed that one run per condition is defensible if reported as a project-specific case study rather than a general result.

## Response 4
Recommended excluding feature flaws and usability concerns from the quantitative unit-testing metrics because they require system, acceptance, or UX judgment. Suggested recording them separately as qualitative observations and distinguishing implementation defects, incorrect test expectations, specification ambiguities, and feature-design concerns.

## Response 5
Agreed that merely naming equivalence partitioning and boundary value analysis was underspecified. Expanded the detailed prompt to require coverage of meaningful partitions, below/at/above boundary values, every valid input in a positive case, isolated invalid inputs before invalid combinations, and economical at-least-once or pairwise combinations. Kept exact project-specific cases out of the prompt so the LLM still had to derive them.

# What You Got Wrong (if any)

The first proposed prompts required the generated suite to pass, which could encourage deletion or weakening of a legitimate bug-revealing test. The first detailed prompt also mentioned equivalence partitioning and boundary value analysis without stating the associated CS2103 input-selection and combination heuristics. Both issues were corrected after the user's pushback.

# Any Other Interesting Information (if any)

N/A
