# Reflections

My workflow for using AI evolved as the project progressed. For the first few commits, I used the web-based LLM chat (ChatGPT), sometimes without any code context, and other times with code context (done by uploading a zipped copy of the repository). I chose this "higher control" workflow as I believed that early architectural decisions were very important, and would shape the project's direction significantly.

Using this workflow, I could control exactly what context the LLM received, manually inspect its suggestions, and decide what to copy, execute, and commit. This corresponded largely to Scenario I in Week 1's lecture.

However, as the project grew, even small changes began to affect many files. This meant manually transferring changes (which were often scattered) became slow and error-prone, as I found myself missing edits or not updating the LLM chat with new context. As such, I moved to Codex Chat, a coding assistant that could inspect the current repository, edit files, and run commands. This corresponded to Scenario III in Week 1's lecture (though I did not spawn multiple agents, to maintain greater control).

Using this workflow, I traded some control for making (repository-wide) changes more efficient and consistent. Nevertheless, I maintained project ownership by ensuring prompts were well-scoped, checking chain-of-thought and output logs, performing thorough diff reviews, and having continuous integration setup (i.e. automated build, checkstyle, and unit tests). Moreover, when checking the LLM's output, I made sure to check that the LLM's design was aligned with mine, and good software engineering principles were adhered to.

## Interesting Prompt Examples

### 1. Designing the Initial Plan

I initially used one broad prompt asking for a CS2103-style, step-by-step project plan (which combined planning the project scope, architecture, and Java files). However, the LLM over-delivered by providing a semester-long project plan. When I requested for something simpler, it then put everything into 1 class, leading me to have to clarify that while I wanted less features, I still wanted good software engineering principles like separation of concerns.

Afterwards, I asked it to propose a commit sequence, which initially seemed correct. However, I found that in its proposed sequence, Gradle referenced a main class that did not exist yet. This showed me that plausible LLM output is not necessarily accurate. Next time, instead of using a single prompt, I would separate it out into exploration, planning, and production prompts.

_(See [001](../logs/001-building-quorum-javafx-timezone-app-cs2103-style.md))_

### 2. Exploring Data Persistence Designs Before Implementation

Following 001, I began in exploration mode by asking for several data persistence designs and explicitly mentioned format, file location, supporting manual editing, handling data corruption, and ensuring testability. This was effective at surfacing various designs and their respective trade-offs.

However, I believe the most value occurred during the follow-up dialogue, which was akin to planning mode. When I proposed using `toString()` as a storage contract, the LLM challenged my design. After multiple rounds of discussion, we converged on having separate codec and storage responsibilities.

Once I accepted that design, I switched to production mode, and asked the LLM to implement it. However, its implementation added extra exception types, unexplained BOM handling, and crash-safe logic that exceeded my needs. Through this, I learnt that while an LLM can produce technically feasible solutions, it can be over-engineered. This meant that I still had to exercise judgement on whether the implementation was appropriate for my project.

_(See [008](../logs/008-implementing-local-roster-persistence.md))_

### 3. Comparing Basic and Structured Test Prompts

Week 1's lecture mentioned using detailed prompts as a way to improve LLM output, but also noted that it may be less critical for advanced models. As such, given that I was using GPT 5.6 Sol, I was unsure on whether a more detailed prompt would actually produce better results than a more general prompt.

Rather than rely on subjective, qualitative impressions, I chose unit test generation to conduct a small experiment, as the resulting test methods could be compared quantitatively (via partition, boundary, and error-path coverage, redundancy, and whether they exposed production defects). I did so by comparing a basic unit test production prompt against a structured prompt that explicitly mentioned course techniques (e.g. equivalence partitions, boundary value analysis, positive/negative test heuristics, strategies for combining input), and ran the experiment for both GPT 5.6 Sol (stronger model) and GPT 5.6 Luna (weaker model).

With the structured prompt, Sol's partition, boundary, and error-path coverage increased from 87.7% to 98.6% (+10.9%), 63.3% to 96.7% (+33.4%), and 76.5% to 100% (+23.5%) respectively. Luna's coverage increased from 82.2% to 95.9% (+13.7%), 60.0% to 80.0% (+20.0%), and 76.5% to 94.1% (+17.6%) across the same metrics. Additionally, Luna's structured prompt also caught a bug that was missed by its basic prompt.

However, the most unexpected finding came from the agentic environment rather than the prompts. While reviewing Luna's chain-of-thought logs, I saw it ran Git commands to explore other trials' branches. Through this, it learnt about the tests Sol made, including the bug Sol found, instead of deriving tests only from its assigned prompt and branch (i.e. Git metadata had become an unintended side channel between trials). This surprised me because I had assumed that starting each trial from the same commit was sufficient isolation, but in reality, a Scenario III AI could autonomously inspect context that I had not considered. This incident made me internalize the lecture's warnings on the risks of AI being unpredictable, having access to excessive context, and being able to execute commands.

Because of this, I re-ran the trials, after deleting local build caches and local experiment branches, and also tightening Codex's permissions to block Git commands. In the future, I also plan on monitoring logs and limiting permissions, especially when running LLM experiments.

_(See [016](../logs/016-designing-prompt-quality-unit-test-experiment.md), [017](../logs/017-evaluating-sol-vs-luna-unit-test-prompts.md), [018](../logs/018-evaluating-unit-test-prompt-results-across-sol-and-luna.md))_

## What I Found LLMs Good At

1. Exploration: It could help me quickly compare alternatives (e.g. it helped me reject exceptions for normal exit, distinguish tags from first-class groups, and preserve meaningful spaces in participant names), while I still made the final choice.
2. Test Generation: It could automatically create test cases based on observable behavior, expose bugs (e.g. oversized duration bug, `U+2028` persistence bug), and other edge cases I might not have considered manually.
3. First Drafts: It could efficiently create first drafts (e.g. test commit planning, JavaFX integration, and documentation files), saving time even when the output still needed to be iterated upon.
4. Mechanically Verifiable Work: These tasks (e.g. adding small commands, moving packages, refactoring classes) usually needed little manual changes.
5. Fixing Concrete Issues: When reviewing its own work, sometimes, it identifies issues and corrects them without my intervention, resembling Week 1 lecture's Reflexion loop. On the other hand, when I identify a concrete issue myself, it also usually effectively fixes the issue.

## What I Found LLMs Bad At

1. Appropriate Level of Detail: It often adds more detail than the intended reader needs (e.g. overdocumented obvious getters with redundant Javadoc comments, added repetitive or out-of-scope explanations to the DG). From this, I learnt to tell the LLM to be concise, and define the intended scope in my prompts, then apply the KISS principle during review.
2. Project Intent: I found that providing context (via repository access) did not guarantee understanding of project intent (e.g. it missed parser conventions, preferred using non-standard terminology, and repeatedly treated one meeting algorithm as the product's permanent identity).
3. Appropriate Architecture: It sometimes alternated between mega-classes and premature abstractions (e.g. universal UI recorder test class, overly-complex GUI).
4. Naming and Responsibility: It sometimes chose names whose apparent scope did not match the code (e.g. `parseIndex` containing participant-specific error messages, `WakefulnessEvaluation` not clearly describing a result).
5. Course-Specific Knowledge: It was less reliable when dealing with information specific to CS2103 (e.g. it confidently said to use Java 17 instead of Java 25). From this, I learnt to explicitly provide current course materials, and to verify its claims against primary sources.
6. Repository-Wide Correctness: It sometimes generated code that looked correct in isolation, but was inconsistent with the rest of the project (e.g. build exposed reference to a deleted method, manual review uncovered misplaced tag responsibility). From this, I learnt that generated code still needed verification through automated checks and manual review.
7. Summarization: I found that concise AI-generated logs could sometimes lose important context (e.g. the data persistence summary initially collapsed 17 turns into 6 turns).
