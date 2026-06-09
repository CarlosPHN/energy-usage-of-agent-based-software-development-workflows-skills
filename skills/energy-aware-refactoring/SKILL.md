---
name: energy-aware-refactoring
description: >
  Refactor source code to fix energy inefficiencies detected by the energy-aware-analysis skill. Takes a source code path (and optionally a pre-existing analysis JSON), applies safe, deterministic, energy-aware refactorings to reduce CPU, memory, disk I/O, and network consumption while preserving functional behavior exactly. Supports Python, JavaScript, TypeScript, Java, Go, and C/C++. Use this skill whenever the user wants to automatically fix energy inefficiencies, apply green refactorings, reduce code power consumption, or optimize code based on a previous energy analysis. Also triggered by phrases like "apply energy optimizations", "fix energy hotspots", "refactor for efficiency", "green refactoring", "reduce CPU/memory/IO", "optimize based on analysis", or when the user provides an analysis JSON and asks to fix the issues. Activate it proactively when the user has run an energy analysis and asks "how do I fix these issues" or "can you apply the optimizations."
---

# Energy-Aware Refactoring Skill

This skill takes the output of `energy-aware-analysis` and applies concrete, safe code refactorings to reduce energy consumption across CPU, memory, disk I/O, and network usage — without altering the program's functional behavior.

## Pipeline

The skill runs in two modes:

### Mode 1: Automatic pipeline (recommended)
The user provides a **source code path**. The skill:
1. Runs `energy-aware-analysis` on that path to generate the analysis JSON
2. Reads the analysis results
3. Applies refactorings for each prioritized issue
4. Verifies functional equivalence

### Mode 2: Manual input
The user provides both a **source code path** and a **pre-existing analysis JSON** (or path to one). The skill skips step 1 and proceeds directly to refactoring.

## Process

### Step 1: Obtain the Analysis

If no analysis JSON is provided, run `energy-aware-analysis` on the input path. Read the resulting JSON — it contains:
- `energy_efficiency_score` — baseline score
- `hotspots` — array of detected issues with severity, impact, snippet, and optimization suggestions
- `prioritized_issues` — ranked list of all issues
- `recommendations` — ordered actionable suggestions

### Step 2: Map Issues to Refactorings

For each issue in `prioritized_issues` (highest rank first), map the `issue` and `optimization` fields to concrete code changes using this table:

#### CPU Refactorings
| Pattern Detected | Refactoring | Expected Saving |
|---|---|---|
| O(n²) nested loops | Replace with hash map lookups, early breaks, or restructured algorithm | CPU -70-95% |
| Redundant computations in loops | Hoist invariant calculations outside the loop | CPU -20-60% |
| String concatenation with `+=` / `+` in loops | Collect parts in a list/array/buffer and join at the end | CPU/Memory -50-80% |
| Busy-wait/spinloops | Replace with event-driven patterns (callbacks, events, async) | CPU -70-90% during idle |
| Excessive recursion | Add memoization or convert to iterative | CPU/Memory -40-80% |
| Unnecessary type conversions in hot paths | Consolidate types, avoid boxing/unboxing | CPU -10-30% |
| `range(len(...))` pattern | Use direct iteration or `enumerate` | CPU -5-15% |

#### Memory Refactorings
| Pattern Detected | Refactoring | Expected Saving |
|---|---|---|
| Object allocations inside loops | Move object creation outside loop, reuse instances | Memory -30-60% |
| Large data retained unnecessarily | Release references early, use scoped variables | Memory -20-50% |
| `readlines()` loading whole file | Iterate over file object directly (`for line in f:`) | Memory -40-60% |
| Deep clone via serialize/deserialize | Use structured clone or manual copy | CPU/Memory -50-80% |
| Large data copies instead of views | Use slices, references, or buffer views | Memory -30-50% |

#### Disk I/O Refactorings
| Pattern Detected | Refactoring | Expected Saving |
|---|---|---|
| File open/close inside loops | Open once before loop, close after | I/O -90-95% |
| Single-byte/line reads | Use buffered I/O (read in chunks) | I/O -70-90% |
| Sync I/O blocking | Use async I/O or move to worker thread | CPU -30-50% |
| Repeated serialization cycles | Cache serialized form, serialize once | I/O -40-60% |

#### Network Refactorings
| Pattern Detected | Refactoring | Expected Saving |
|---|---|---|
| N+1 queries (query in loop) | Use batch query, JOIN, or GraphQL batching | Network -80-95% |
| Sequential API calls in loop | Parallelize with `Promise.all`, goroutines, etc. | Network -60-80% latency |
| Over-fetching data | Select only needed fields (projection) | Network -30-70% |
| Multiple small requests | Batch into fewer larger requests | Network -40-60% |
| No connection reuse | Add connection pooling | Network/CPU -20-40% |

### Step 3: Apply Each Refactoring

For each issue, in priority order:

1. **Read** the relevant source file
2. **Understand the context** — read surrounding functions, understand data flow
3. **Apply the refactoring** using Edit tool operations:
   - Keep the change minimal — touch only what needs to change
   - Use the `optimization` hint from the analysis JSON as guidance
   - Consult the corresponding language reference file in `../energy-aware-analysis/references/` for language-specific idioms:
     - `../energy-aware-analysis/references/python-patterns.md`
     - `../energy-aware-analysis/references/javascript-patterns.md`
     - `../energy-aware-analysis/references/java-patterns.md`
     - `../energy-aware-analysis/references/go-patterns.md`
     - `../energy-aware-analysis/references/cpp-patterns.md`
4. **Do not change**:
   - Public API signatures
   - Return values or their types
   - Error handling behavior (exceptions, error codes)
   - Logging messages or side effects visible to consumers
   - Configuration loading or environment variable handling
5. **Document each change** as you go (in-memory) — what you changed, why, and the original issue reference

### Step 4: Verify Functional Equivalence

After all refactorings are applied:

#### Static Verification
- **Compare signatures**: All function/method signatures must be identical to the original
- **Compare return types**: Return values must have the same type/shape
- **Check error paths**: Exceptions and error handling must be preserved
- **Review I/O contracts**: File formats, network request/response shapes must be unchanged
- **Verify edge cases**: Empty collections, null values, boundary conditions produce the same results

#### Dynamic Verification (if tests exist)
- Discover test files in the project (look for `test_*`, `*_test.go`, `*Test.java`, `*.test.js`, `*.spec.js`, `__tests__/`, etc.)
- Run the tests
- If any test fails, investigate whether the refactoring introduced a bug or the test needs updating. If it's a real bug, fix it. If the test expectation was tied to an implementation detail (not behavior), update the test.

### Step 5: Generate the Output

Produce a structured JSON output:

```json
{
  "skill": "energy-aware-refactoring",
  "input_path": "<source path>",
  "original_score": <score from analysis>,
  "changes_applied": [
    {
      "file": "<file path>",
      "line": <original line>,
      "issue": "<original issue description>",
      "issue_rank": <rank from analysis>,
      "change": "<description of what was changed>",
      "original_snippet": "<code before>",
      "refactored_snippet": "<code after>",
      "estimated_impact": {
        "cpu": "<reduction estimate>",
        "memory": "<reduction estimate>",
        "disk": "<reduction estimate>",
        "network": "<reduction estimate>"
      }
    }
  ],
  "patch": "<unified diff string covering all changes>",
  "verification": {
    "static_analysis": "passed" | "issues_found",
    "static_notes": ["<any notes about static verification>"],
    "tests": {
      "found": <count>,
      "run": <count>,
      "passed": <count>,
      "failed": <count>,
      "details": [{"name": "<test name>", "result": "passed" | "failed"}]
    }
  },
  "estimated_post_refactor_score": <estimated new score>,
  "estimated_overall_improvement": "<concise summary of improvements>"
}
```

## Constraints (Mandatory)

1. **Do NOT introduce new functionality.** No new features, no additional parameters, no config options.
2. **Do NOT change business logic.** The refactored code must produce identical outputs for identical inputs.
3. **Do NOT remove existing behavior.** Side effects, error handling, logging, and edge cases must be preserved.
4. **Only optimize for energy.** No style-only changes, no lint-only fixes. Every change must trace back to an issue in the analysis JSON.
5. **Be deterministic.** Given the same analysis JSON and source code, always produce the same refactoring.
6. **Preserve code style.** Use the same naming conventions, formatting style, and patterns as the original file.
7. **One issue at a time.** Apply changes in priority order. If applying a later change would conflict with an earlier one, skip it and note the conflict in the output.

## Output Fields

- `skill`: Always `"energy-aware-refactoring"`
- `input_path`: The source code path provided
- `original_score`: Energy efficiency score from the analysis
- `changes_applied`: Array of every change made, with before/after snippets
- `patch`: A complete unified diff showing all changes (use `git diff` if it's a git repo, or generate manually)
- `verification`: Results of static and dynamic verification
- `estimated_post_refactor_score`: Estimated new score (original + improvements per change)
- `estimated_overall_improvement`: Human-readable summary

## Error Handling

- If `energy-aware-analysis` cannot run (no JSON provided and source path invalid), return error
- If the analysis JSON has no actionable issues, report: "No refactoring opportunities found"
- If a change would conflict with functional behavior, skip it and explain why
- If tests fail after refactoring, report which tests failed and attempt to fix before giving up
