# C/C++ Energy Anti-Patterns

## CPU
- Passing large objects by value instead of const reference
- Using `std::map` for lookups when `std::unordered_map` would be faster (or vice versa at small N)
- Excessive dynamic allocations with `new`/`delete` in hot paths (use stack allocation or pools)
- Virtual function calls in tight loops (vtable lookup overhead)
- Using `std::shared_ptr` copy in hot paths (atomic ref-count increment)
- Loop-invariant code not hoisted by the compiler
- Using exceptions for control flow in performance-critical paths

## Memory
- Frequent `malloc`/`free` calls — use custom allocators or object pools
- std::string concatenation creating many temporary objects (`operator+`)
- Not reserving capacity for `std::vector` (repeated reallocations)
- Cache-inefficient data structures (random access patterns, false sharing)
- Raw pointer management without RAII (leaks)

## I/O
- Using C-style `fprintf`/`fscanf` without buffering (though FILE* is usually buffered already)
- `std::endl` instead of `\n` (forces flush)
- Reading entire files into memory with `read()` when memory-mapped I/O (`mmap`) would suffice
- Per-byte I/O operations instead of reading blocks

## Network
- Creating/destroying sockets per request instead of reusing
- Using blocking sockets without `select`/`epoll` for single-threaded servers
- Serializing/deserializing with verbose formats (XML/JSON) instead of binary protocols
- Not using sendfile for file transfers (involves extra user-space copy)
