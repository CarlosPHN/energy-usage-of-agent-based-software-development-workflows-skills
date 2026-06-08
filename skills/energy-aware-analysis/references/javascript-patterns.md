# JavaScript/TypeScript Energy Anti-Patterns

## CPU
- Array operations inside `map`/`filter`/`reduce` chains that recompute the same value
- Deeply nested `Promise` chains instead of `async/await` (readability, not energy, but leads to wasteful retries)
- `for...in` over arrays (much slower than `for...of` or indexed loops)
- Using `lodash` `_.forEach` over native `Array.forEach` or `for...of`
- Inefficient DOM queries inside loops (not caching `document.querySelector` results)
- Using `JSON.parse(JSON.stringify(obj))` for deep cloning instead of structuredClone

## Memory
- Closures capturing large objects unnecessarily (memory retained via reference)
- Unbounded caches (e.g., accumulating results in a Map without eviction policy)
- Creating large intermediate arrays with `.map().filter().reduce()` instead of a single loop
- Detached DOM elements still referenced in JavaScript (memory leaks)
- Event listeners added inside loops without cleanup

## I/O
- `fs.readFileSync` for large files (use streams: `fs.createReadStream`)
- Writing logs synchronously in hot paths
- Parsing large JSON entirely with `JSON.parse` when streaming parsers suffice
- Repeated `fs.stat` calls for the same file

## Network
- Sequential `await fetch()` in loops — use `Promise.all()` for independent requests
- Not using HTTP connection pooling (`keep-alive`)
- GraphQL over-fetching (requesting entire objects when fields suffice)
- Polling with `setInterval` instead of WebSockets or Server-Sent Events
