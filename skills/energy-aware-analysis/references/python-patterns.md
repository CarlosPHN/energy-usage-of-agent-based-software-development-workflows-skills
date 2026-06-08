# Python Energy Anti-Patterns

## CPU
- `for i in range(len(x)):` instead of `for item in x:` (extra attribute lookups)
- List comprehensions over generators where lazy evaluation is appropriate
- Using `+=` for string concatenation in loops (O(n²) memory copies) — use `''.join(list)` instead
- `pandas` loops with `.iterrows()` instead of vectorized operations
- Deep recursion without `functools.lru_cache` or `functools.cache`
- Using `time.sleep()` in tight loops for polling instead of event-based waits

## Memory
- Creating copies with `[:]` or `.copy()` when read-only views suffice
- Holding file contents entirely in memory when iteration would do (`for line in f:` vs `f.readlines()`)
- Unbounded `list.append` in loops (pre-allocate with `[None] * N` if size known)
- Using `pickle`/`json` on very large data instead of streaming parsers (ijson, orjson)
- Global variables that accumulate data without cleanup

## I/O
- Open/close files inside loops instead of opening once outside
- Using blocking I/O in async contexts (`asyncio` + `requests` instead of `aiohttp`)
- Reading entire files with `.read()` when line-by-line processing works
- Repeated CSV/JSON parsing of the same file
- Writing single rows at a time to files instead of buffering writes

## Network
- Making HTTP requests inside loops without connection reuse (use `requests.Session()`)
- N+1 queries with ORMs (Django/ SQLAlchemy) — missing `select_related` / `prefetch_related`
- Using synchronous ORM load in async views
- Fetching entire tables when aggregated queries would do
