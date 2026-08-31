# Reflections

## Workflow Evolution

My workflow for using AI evolved as the project progressed. For the first few commits, I used the web-based LLM chat (ChatGPT), sometimes without any code context, and other times with code context (done by uploading a zipped copy of the repository). I chose this "higher control" workflow as I believed that early architectural decisions were very important, and would shape the project's direction significantly.

Using this workflow, I could control exactly what context the LLM received, manually inspect its suggestions, and decide what to copy, execute, and commit. This corresponded largely to Scenario I in Week 1's lecture.

However, as the project grew, even small changes began to affect many files. This meant manually transferring changes (which were often scattered) became slow and error-prone, as I found myself missing edits or not updating the LLM chat with new context. As such, I moved to Codex Chat, a coding assistant that could inspect the current repository, edit files, and run commands. This corresponded to Scenario III in Week 1's lecture (though I did not spawn multiple agents, to maintain greater control).

Using this workflow, I traded some control for making (repository-wide) changes more efficient and consistent. Nevertheless, I maintained project ownership by [exercising engineering judgement](#how-i-exercised-engineering-judgment), aligning with Week 2 lecture's recommended review sequence for AI-generated code.

## Interesting Prompt Examples

### 1. Designing the Initial Plan

I initially used one broad prompt asking for a CS2103-style, step-by-step project plan (which combined planning the project scope, architecture, and Java files). However, the LLM over-delivered by providing a semester-long project plan. When I requested for something simpler, it then put everything into 1 class, leading me to have to clarify that while I wanted less features, I still wanted good software engineering principles like separation of concerns.

Afterwards, I asked it to propose a commit sequence, which initially seemed correct. However, I found that in its proposed sequence, Gradle referenced a main class that did not exist yet, even though the LLM claimed that every commit would compile and run. As such, next time, instead of using a single prompt, I would separate it out into exploration, planning, and production prompts.

_(See [001](../logs/001-building-quorum-javafx-timezone-app-cs2103-style.md))_

### 2. Exploring Data Persistence Designs Before Implementation

Following 001, I began in exploration mode by asking for several data persistence designs and explicitly mentioned format, file location, supporting manual editing, handling data corruption, and ensuring testability. This was effective at surfacing various designs and their respective trade-offs.

However, I believe the most value occurred during the follow-up dialogue, which was akin to planning mode. When I proposed using `toString()` as a storage contract, the LLM challenged my design. After multiple rounds of discussion, we converged on having separate codec and storage responsibilities.

Once I accepted that design, I switched to production mode, and asked the LLM to implement it. However, its implementation added extra exception types, unexplained BOM handling, and crash-safe logic that exceeded my needs, despite being technically feasible.

_(See [008](../logs/008-implementing-local-roster-persistence.md))_

### 3. Comparing Basic and Structured Test Prompts

Week 1's lecture mentioned using detailed prompts as a way to improve LLM output, but also noted that it may be less critical for advanced models. As such, given that I was using GPT 5.6 Sol, I was unsure on whether a more detailed prompt would actually produce better results than a more general prompt.

Rather than rely on subjective, qualitative impressions, I chose unit test generation to conduct a small experiment, as the resulting test methods could be compared quantitatively (via partition, boundary, and error-path coverage, redundancy, and whether they exposed production defects). I did so by comparing a basic unit test production prompt against a structured prompt that explicitly mentioned course techniques covered in Week 2's lecture (e.g. equivalence partitions, boundary value analysis, positive/negative test heuristics, strategies for combining input), and ran the experiment for both GPT 5.6 Sol (stronger model) and GPT 5.6 Luna (weaker model).

With the structured prompt, Sol's partition, boundary, and error-path coverage increased from 87.7% to 98.6% (+10.9%), 63.3% to 96.7% (+33.4%), and 76.5% to 100% (+23.5%) respectively. Luna's coverage increased from 82.2% to 95.9% (+13.7%), 60.0% to 80.0% (+20.0%), and 76.5% to 94.1% (+17.6%) across the same metrics. Additionally, Luna's structured prompt also caught a bug that was missed by its basic prompt.

However, the most unexpected finding came from the agentic environment rather than the prompts. While reviewing Luna's chain-of-thought logs, I saw it ran Git commands to explore other trials' branches. Through this, it learnt about the tests Sol made, including the bug Sol found, instead of deriving tests only from its assigned prompt and branch (i.e. Git metadata had become an unintended side channel between trials). This surprised me because I had assumed that starting each trial from the same commit was sufficient isolation, but in reality, a Scenario III AI could autonomously inspect context that I had not considered. This incident made me internalize the lecture's warnings on the risks of AI being unpredictable, having access to excessive context, and being able to execute commands.

Because of this, I re-ran the trials, after deleting local build caches and local experiment branches, and also tightening Codex's permissions to block Git commands.

_(See [016](../logs/016-designing-prompt-quality-unit-test-experiment.md), [017](../logs/017-evaluating-sol-vs-luna-unit-test-prompts.md), [018](../logs/018-evaluating-unit-test-prompt-results-across-sol-and-luna.md))_

## What I Found LLMs Good At

1. Exploration: It could help me quickly compare alternatives (e.g. it helped me [reject exceptions for normal exit](../logs/007-refactoring-command-execution.md), [distinguish tags from first-class groups](../logs/012-designing-tags-and-meeting-time-ranking.md), and [preserve meaningful spaces in participant names](../logs/028-preventing-duplicate-roster-names.md)), while I still made the final choice.
2. Test Generation: It could automatically create test cases based on observable behavior, expose bugs (e.g. the [oversized duration bug](../logs/019-refining-meeting-tests-and-ui-test-doubles.md) and [`U+2028` persistence bug](../logs/021-testing-tag-participant-and-roster-models.md)), and other edge cases I might not have considered manually.
3. First Drafts: It could efficiently create first drafts (e.g. [test commit planning](../logs/020-planning-unit-test-commits.md), [JavaFX integration](../logs/027-implementing-a-minimal-javafx-chat-interface.md), and documentation files such as the [User Guide](../logs/031-creating-and-refining-quorum-user-guide.md) and [Developer Guide](../logs/033-creating-and-refining-quorum-developer-guide.md)), saving time even when the output still needed to be iterated upon.
4. Mechanically Verifiable Work: These tasks (e.g. [adding a small command](../logs/011-implementing-zones-command.md), [moving packages](../logs/029-refactoring-parser-and-engine-into-logic-package.md), and [refactoring command classes](../logs/007-refactoring-command-execution.md)) usually needed little manual changes.
5. Fixing Concrete Issues: When reviewing its own work, sometimes, it [identifies issues and corrects them without my intervention](../logs/013-implementing-tag-and-untag-commands.md), resembling Week 1 lecture's Reflexion loop. On the other hand, when I identify a concrete issue myself, it also usually effectively fixes the issue.

## What I Found LLMs Bad At

1. Appropriate Level of Detail: It often adds more detail than the intended reader needs (e.g. [overdocumented obvious getters with redundant Javadoc comments](../logs/004-fixing-checkstyle-issues-in-quorum.md), [added repetitive or out-of-scope explanations to the DG](../logs/034-refining-quorum-developer-guide-diagrams-and-prose.md)).
2. Project Intent: I found that providing context (via repository access) did not guarantee understanding of project intent (e.g. it [missed parser conventions and preferred using non-standard terminology](../logs/011-implementing-zones-command.md), and [repeatedly treated one meeting algorithm as the product's permanent identity](../logs/031-creating-and-refining-quorum-user-guide.md)).
3. Appropriate Architecture: It sometimes alternated between mega-classes and premature abstractions (e.g. a [universal UI recorder test class](../logs/019-refining-meeting-tests-and-ui-test-doubles.md), an [overly-complex GUI](../logs/027-implementing-a-minimal-javafx-chat-interface.md)).
4. Naming and Responsibility: It sometimes chose names whose apparent scope did not match the code (e.g. [`parseIndex` containing participant-specific error messages](../logs/005-implementing-delete-command.md), [`WakefulnessEvaluation` not clearly describing a result](../logs/015-implementing-and-refining-meeting-time-ranking.md)).
5. Course-Specific Knowledge: It was less reliable when dealing with information specific to CS2103 (e.g. it [confidently said to use Java 17 instead of Java 25](../logs/002-fixing-invalid-source-release-25-gradle-error.md)).
6. Repository-Wide Correctness: It sometimes generated code that looked correct in isolation, but was inconsistent with the rest of the project (e.g. the [build exposed a reference to a deleted method](../logs/003-adding-checkstyle-and-ci-cd-to-quorum.md), and [manual review uncovered misplaced tag responsibility](../logs/013-implementing-tag-and-untag-commands.md)).
7. Summarization: I found that concise AI-generated logs could sometimes lose important context (e.g. the [data persistence summary initially collapsed 17 turns into 6 turns](../logs/008-implementing-local-roster-persistence.md)).

## How I Exercised Engineering Judgment

1. Automated and Manual Verification: I used automated build, checkStyle, and unit tests for mechanically verifiable changes, but still performed manual review to check design, intent, architecture, naming, and documentation. In that sense, passing automated checks was necessary but not sufficient.
2. Judging Appropriateness: Aside from considering whether a proposed solution could work, I also considered whether its complexity was justified by the project's requirements. This led me to reject technically valid designs whose additional abstractions or features did not provide sufficient value.
3. Verifying External Claims: For course-specific or unfamiliar claims, I checked them against appropriate, up-to-date primary sources before accepting them.
4. Reviewing the Process: Aside from reviewing the final artifacts, I also inspected how the outputs were produced (e.g. the [Git side channel between experiment trials](../logs/017-evaluating-sol-vs-luna-unit-test-prompts.md) and omissions in [generated conversation summaries](../logs/008-implementing-local-roster-persistence.md) showed that seemingly valid outputs could still have come from an unreliable process).
