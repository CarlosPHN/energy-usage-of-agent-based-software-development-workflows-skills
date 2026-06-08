---
name: energy-aware-analysis
description: >
  Analyze source code for energy inefficiencies and provide a structured report with an Energy Efficiency Score (0-100), identified hotspots, and prioritized recommendations. Supports Python, JavaScript, TypeScript, Java, Go, and C/C++. Use this skill whenever the user asks to analyze code energy consumption, find performance hotspots, identify energy-inefficient patterns, evaluate code carbon footprint, or optimize energy usage in software. Also triggered by phrases like "energy-aware refactoring", "green code", "power consumption analysis", "energy hotspots", "code efficiency audit", or "sustainable software analysis". Always use this skill when the user shares a code file or project path and asks for a performance or energy audit, even if they don't explicitly use the word "energy". Activate it proactively when the user mentions they want to reduce resource usage or optimize CPU/memory/IO patterns.
---

# Energy-Aware Code Analysis Skill

Analyze source code statically (without execution) to detect energy inefficiencies, estimate their relative impact, and produce a structured JSON report.

## Input

- A **file path** or **directory path** containing source code.
- The analysis detects the language(s) automatically from file extensions.

## Analysis Process

Follow these steps in order:

### 1. Code Structure Analysis

1. **Explore the project** — list files, identify the project structure, and read relevant source files.
2. **Identify** modules, functions/methods, classes, and dependencies.
3. **Build a high-level execution map** — understand how data flows and which components likely consume the most resources.

### 2. Detection of Inefficient Patterns

For each relevant source file, scan for these categories of energy-inefficient patterns:

#### CPU Inefficiencies
- High-complexity loops (O(n²), O(n³)) processing large datasets
- Nested iterations over collections without breaks
- Redundant computations (same calculation repeated in loops)
- Unnecessary type conversions or boxing/unboxing in hot paths
- Excessive recursion without memoization
- Tight loops that could use vectorized operations
- Busy-waiting / spinloops instead of event-driven patterns

#### Memory Inefficiencies
- Unnecessary object allocations inside loops
- Large unused data structures retained in scope
- Inefficient string concatenation in loops (e.g., `+=` in Python/Java loops)
- Copying large data structures instead of using references/views
- Memory leaks (event listeners not removed, caches without eviction)
- Using mutable defaults or holding references longer than needed

#### Disk I/O Inefficiencies
- Reading/writing files one line/byte at a time instead of buffered I/O
- Repeated file open/close operations in loops
- Parsing large files entirely into memory when streaming would work
- Unnecessary serialization/deserialization cycles
- Using sync I/O where async would not block

#### Network Inefficiencies
- Repeated API calls in loops without batching
- Making N+1 database queries instead of using joins or batch queries
- Fetching more data than needed (SELECT *, over-fetching in REST)
- Not using connection pooling or reusing connections
- Chatty protocols (many small requests instead of fewer large ones)

#### Language-Specific Patterns
- For language-specific patterns, consult the corresponding reference file in `references/`.

### 3. Energy Impact Estimation

For each detected issue, estimate the **relative energy cost** on a scale of **Low / Medium / High** considering:

- **CPU**: How many wasted cycles per invocation? How frequently is this code path executed?
- **Memory**: How much extra allocation? How long is the data retained?
- **Disk I/O**: How many unnecessary read/write operations?
- **Network**: How many extra bytes transferred? How many extra round trips?

### 4. Prioritization

Rank findings by weighting:
- **Severity** × **Frequency of execution** (hot path vs. cold path)
- Issues in critical execution paths rank higher
- Issues with easy, low-risk fixes rank slightly higher than equivalent hard-to-fix issues

## Output Format

Produce a **single JSON object** with this exact structure:

```json
{
  "skill": "energy-aware-analysis",
  "input_path": "<path analyzed>",
  "languages_detected": ["python", "javascript"],
  "files_analyzed": 12,
  "energy_efficiency_score": 65,
  "summary": {
    "total_issues": 8,
    "critical": 2,
    "high": 3,
    "medium": 2,
    "low": 1,
    "top_areas": ["CPU-intensive loops in data_processor.py",
                  "N+1 queries in user_service.js"]
  },
  "hotspots": [
    {
      "file": "src/data_processor.py",
      "line": 45,
      "function": "process_records",
      "issue": "O(n²) nested loop over large dataset",
      "severity": "critical",
      "impact": {
        "cpu": "high",
        "memory": "medium",
        "disk": "none",
        "network": "none"
      },
      "explanation": "The nested loop iterates over all records for each record, creating O(n²) complexity. With 10k+ records this causes quadratic CPU waste and unnecessary energy consumption.",
      "snippet": "for i in range(len(records)):\n    for j in range(len(records)):\n        ...",
      "optimization": "Use a hash map (dict) for lookups to reduce to O(n), or restructure the algorithm to avoid pairwise comparison."
    }
  ],
  "prioritized_issues": [
    {
      "rank": 1,
      "file": "src/data_processor.py",
      "line": 45,
      "function": "process_records",
      "issue": "O(n²) nested loop over large dataset",
      "severity": "critical",
      "explanation": "Quadratic complexity on the hottest code path. Each request triggers this loop.",
      "snippet": "for i in range(len(records)):\n    for j in range(len(records)):\n        ...",
      "optimization": "Use a hash map (dict) for lookups to reduce to O(n).",
      "estimated_savings": "Could reduce CPU energy by ~90% on this code path"
    }
  ],
  "recommendations": [
    "Refactor nested loops in data_processor.py to use hash-based lookups",
    "Add database indexing and batch queries in user_service.js",
    "Replace string concatenation in loops with list join in logger.py"
  ]
}
```

### Output Field Specifications

- `energy_efficiency_score`: Integer 0–100. 80+ = efficient, 50–79 = moderate issues, <50 = significant inefficiencies.
- `hotspots`: Array of the most impactful issues found (≤ 10 items).
- `prioritized_issues`: All issues sorted by rank (1 = most impactful).
- `recommendations`: Actionable, ordered by expected benefit.

## Constraints

- Do **NOT** modify any source code.
- Do **NOT** execute or compile the program.
- All analysis must be **static** — based on code reading and pattern recognition.
- Impacts are **estimations** — do not claim exact measurements.
- If the path does not exist or is not readable, report an error in the JSON (`"error": "path not found"`).

## Files referenced by this skill

When analyzing, read the relevant language reference file(s) from `references/` for language-specific anti-patterns:
- `references/python-patterns.md` — Python-specific energy anti-patterns
- `references/javascript-patterns.md` — JavaScript/TypeScript-specific energy anti-patterns
- `references/java-patterns.md` — Java-specific energy anti-patterns
- `references/go-patterns.md` — Go-specific energy anti-patterns
- `references/cpp-patterns.md` — C/C++-specific energy anti-patterns
