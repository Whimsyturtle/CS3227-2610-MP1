# Evaluating Unit-Test Prompt Results Across Sol and Luna

# My Request(s)
## Request 1
Shared basic and structured prompts used to generate meeting-feature unit tests on two branches, proposed quality metrics, and asked for Log 16 to be read.

## Request 2
Clarified that the first branches used GPT-5.6 Sol, introduced equivalent GPT-5.6 Luna branches, and requested within-model and cross-model evaluation summarized concisely in Markdown.

## Request 3
Asked to remove JaCoCo because it made the report unnecessarily complex.

## Request 4
Asked what it meant that redundancy could not be reported without per-test mutation-kill data.

## Request 5
Accepted redefining redundancy using only distinct behavioural obligations and asked whether it could then be calculated.

## Request 6
Questioned how the reported redundancy figures had been checked.

# Your Response(s)
## Response 1
Confirmed the experiment design, ran both original suites, identified the shared oversized-duration production defect, reported test and coverage results, and recommended freezing a behavioural obligation rubric before treating manual scores as final.

## Response 2
Ran both Luna suites, applied a common retrospective rubric across all four branches, compared structured versus basic prompts and Sol versus Luna, and created `logs/017-evaluating-sol-vs-luna-unit-test-prompts.md`. Concluded that Sol structured was most thorough while Luna structured was more compact.

## Response 3
Removed all JaCoCo columns, methodology, and conclusions from the report, leaving behavioural coverage, test count, validity, and defect detection.

## Response 4
Explained that test count alone does not establish redundancy because tests covering the same obligation may detect different mutants. Proposed an obligation-only redundancy definition as a simpler alternative.

## Response 5
Redefined a test as redundant when removing it leaves partition, boundary, and error-path coverage unchanged, then added manually assessed redundancy rates to the report.

## Response 6
Conceded that the redundancy rates were derived through manual obligation mapping rather than an automated or preserved test-to-obligation matrix. Listed the methods classified as redundant and acknowledged that the figures depend on whether indirect cross-class coverage is credited.

# What You Got Wrong (if any)

Presented exact redundancy percentages before clearly documenting that they were manual, retrospective, and rubric-dependent. A preserved test-to-obligation matrix is needed to make those figures fully auditable.

# Any Other Interesting Information (if any)

The structured prompt improved behavioural coverage for both models. Three suites exposed the same production defect, while Luna's basic-prompt suite passed but missed it. The four-condition comparison remains a single-run project-specific case study.
