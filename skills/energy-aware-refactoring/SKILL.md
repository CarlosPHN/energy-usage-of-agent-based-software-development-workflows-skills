---
name: energy-aware-refactoring
description: >
  Refactor source code to reduce energy consumption by detecting and fixing CPU, memory, disk I/O, and network inefficiencies. A fully standalone skill that analyzes source code directly (no external dependencies), applies safe and deterministic refactorings, and preserves functional behavior exactly. Supports Python, JavaScript, TypeScript, Java, Go, and C/C++. Use this skill whenever the user wants to automatically improve code energy efficiency, reduce CPU/memory/IO usage, apply green refactorings, or optimize power consumption. Triggered by phrases like "reduce energy", "optimize efficiency", "refactor for performance", "green code", "power consumption", "energy hotspots", "make this more efficient", "reduce resource usage", or when sharing code and asking to make it consume less energy. Activate it proactively when the user asks for code optimization, efficiency improvements, or resource usage reduction, even if they don't explicitly mention "energy".
---

# Energy-Aware Refactoring Skill

Analyzes source code for energy inefficiencies and applies safe, deterministic refactorings to reduce CPU, memory, disk I/O, and network consumption — preserving functional behavior exactly. This is a fully standalone skill with no external dependencies.

## Input

A **file path** or **directory path** containing source code. The skill automatically detects the language from file extensions.

## Process

### Step 1: Analyze Code for Inefficiencies

Read all source files and scan for these categories of energy-inefficient patterns. Each category references the corresponding Green Software Foundation (GSF) pattern and supporting academic research. This is a static analysis — do not execute the code.

#### CPU Inefficiencies
- **O(n²) nested loops** over large datasets (O(n²) → O(n) with hash map lookups, early breaks, or algorithm restructure)
- **Redundant computations in loops** → hoist invariant calculations outside the loop
- **String concatenation with `+=` / `+` in loops** → collect in a list/array and join once
- **Busy-wait/spinloops** (`while True: sleep(...)`) → replace with event-driven pattern
- **Excessive recursion without memoization** → add memoization or convert to iterative
- **Unnecessary type conversions in hot paths** → consolidate types, avoid boxing/unboxing
- **`range(len(...))` pattern when index not needed** → use direct iteration or `enumerate`

#### Memory Inefficiencies
- **Object allocations inside loops** → move creation outside loop, reuse instances
- **Widget/component creation without cleanup** (recreating UI elements on every call) → create once, update via `.config()` / property setter
- **`readlines()` loading whole file** → iterate over file object directly
- **Deep clone via serialize/deserialize** (`JSON.parse(JSON.stringify(obj))`) → use structured clone
- **Large data copies instead of views** → use slices, references, or buffer views

#### Disk I/O Inefficiencies
- **File open/close inside loops** → open once before loop, close after
- **Single-byte/line reads** → use buffered I/O (read in chunks)
- **Sync I/O blocking** when async is available → use async I/O or worker thread ([GSF: Use Async Instead of Sync](https://patterns.greensoftware.foundation/development/use-async-instead-of-sync))
- **Repeated serialization of the same data** → cache serialized form

#### Network Inefficiencies
- **N+1 queries (query in loop instead of batch)** → use batch query, JOIN, or batching
- **Sequential API calls that could be parallel** → parallelize with `Promise.all`, goroutines, etc.
- **Over-fetching data** → select only needed fields ([GSF: Reduce Transmitted Data](https://patterns.greensoftware.foundation/development/data-handling/reduce-transmitted-data))
- **Multiple small requests instead of batched** → batch into fewer larger requests ([GSF: Keep Request Counts Low](https://patterns.greensoftware.foundation/development/web-performance/keep-request-counts-low))
- **No connection reuse** → add connection pooling

### Step 2: Apply Refactorings

For each detected issue, in order of impact (high CPU/memory savings first):

| GSF Pattern | Code-Level Refactoring | Languages Affected |
|---|---|---|
| [Use compiled languages](https://patterns.greensoftware.foundation/development/media-and-code-efficiency/use-compiled-languages) | Not applicable (language choice); defer to analysis report | All interpreted (Python, JS, etc.) |
| [Cache static data](https://patterns.greensoftware.foundation/development/data-handling/cache-static-data) | Add `@lru_cache` / memoization; persist computed results | Python, JS, Java, Go |
| [Use Async instead of Sync](https://patterns.greensoftware.foundation/development/use-async-instead-of-sync) | Replace blocking calls with async/await, `Promise.all`, goroutines | JS, Python, Java, Go, C# |
| [Compress transmitted data](https://patterns.greensoftware.foundation/development/data-handling/compress-transmitted-data) | Enable gzip/brotli; compress payloads before sending | All (server config) |
| [Keep request counts low](https://patterns.greensoftware.foundation/development/web-performance/keep-request-counts-low) | Batch API calls, bundle assets, use data URIs | JS, web |
| [Minimize main thread work](https://patterns.greensoftware.foundation/development/web-performance/minimize-main-thread-work) | Offload heavy computation to Web Workers or server | JS (browser) |
| [Avoid tracking unnecessary data](https://patterns.greensoftware.foundation/development/data-handling/avoid-tracking-unnecessary-data) | Remove analytics calls that collect unused data | JS, web |
| [Reduce transmitted data](https://patterns.greensoftware.foundation/development/data-handling/reduce-transmitted-data) | Select only needed fields; paginate results | All (API layer) |

Apply refactorings in this priority order:

1. **Read** the relevant source file
2. **Understand the context** — read surrounding functions, understand data flow
3. **Apply the refactoring** using minimal, targeted edits — touch only what needs to change

**Strictly preserve:**
- Function names — never rename
- Function signatures — parameters, return types, interfaces must be identical
- Error handling — exceptions, error codes, edge cases unchanged
- I/O contracts — file formats, network shapes unchanged
- All side effects — logging, output format, UI layout unchanged

### Step 3: Verify

After all refactorings, do a static comparison against the original:
- Every function still has the same name, parameters, and return value
- The code still handles the same inputs and produces the same outputs
- No new functions, parameters, or features were added
- No existing behavior was removed

## Output

Return **only the refactored source code** — nothing else.

- No explanations
- No comments added to the code
- No metadata, no JSON, no diff
- If multiple files were refactored, return all files
- If no improvements are possible, return the exact original code unchanged

## Constraints

1. **No calling external analysis tools.** This skill performs its own analysis.
2. **No structural changes.** Do not rename functions, change signatures, or alter contracts.
3. **Only energy optimizations.** No style-only, lint-only, or cosmetic changes.
4. **No new functionality.** No new features, parameters, or config options.
5. **Preserve existing behavior.** Same inputs → same outputs, same side effects.
6. **Keep changes minimal.** Every edit must directly address a detected energy inefficiency.
7. **Return only code.** No prose, no reports, no metadata.

## References

These authoritative sources validate the energy-efficiency patterns this skill detects and applies:

### Green Software Foundation (GSF) Patterns
The GSF maintains a catalog of 23 Development patterns at [patterns.greensoftware.foundation](https://patterns.greensoftware.foundation/development/). Key patterns that inform this skill:

| Pattern | URL |
|---|---|
| Cache static data | https://patterns.greensoftware.foundation/development/data-handling/cache-static-data |
| Compress stored data | https://patterns.greensoftware.foundation/development/data-handling/compress-stored-data |
| Compress transmitted data | https://patterns.greensoftware.foundation/development/data-handling/compress-transmitted-data |
| Reduce transmitted data | https://patterns.greensoftware.foundation/development/data-handling/reduce-transmitted-data |
| Avoid tracking unnecessary data | https://patterns.greensoftware.foundation/development/data-handling/avoid-tracking-unnecessary-data |
| Use compiled languages | https://patterns.greensoftware.foundation/development/media-and-code-efficiency/use-compiled-languages |
| Minimize main thread work | https://patterns.greensoftware.foundation/development/web-performance/minimize-main-thread-work |
| Use Async instead of Sync | https://patterns.greensoftware.foundation/development/use-async-instead-of-sync |
| Keep request counts low | https://patterns.greensoftware.foundation/development/web-performance/keep-request-counts-low |
| Avoid excessive DOM size | https://patterns.greensoftware.foundation/development/web-performance/avoid-excessive-dom-size |

### Academic Research
- **Pereira, R., Couto, M., Ribeiro, F., Rua, R., Cunha, J., Fernandes, J.P., Saraiva, J. (2021).** "Ranking Programming Languages by Energy Efficiency." *Science of Computer Programming, 205*, 102609. — Empirical comparison of 27 languages showing C, Rust, C++ as most energy-efficient; Python, Ruby, Perl as least. Demonstrates that loops, string operations, and data structure choices have measurable energy impact. Available at: https://haslab.github.io/SAFER/scp21.pdf
- **Poy, G., et al. (2025).** "Impact on energy consumption of design patterns, code smells and refactoring techniques: A systematic mapping study." *Journal of Systems and Software, 225*, 112425. — Shows removing code smells tends to reduce energy consumption; design patterns can sometimes increase it.
- **Cursaru, L.M., et al. (2024).** "A Controlled Experiment on the Energy Efficiency of Source Code." *IEEE Access, 12*, 67345–67358. — Industry case study showing 29% reduction in per-user energy consumption after refactoring energy-inefficient patterns.
- **Spotify Engineering (2020).** "Reducing Backend Energy Consumption." — Case study demonstrating 23% backend energy reduction by refactoring idle polling loops into event-driven architectures.
