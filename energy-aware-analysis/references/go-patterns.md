# Go Energy Anti-Patterns

## CPU
- Using `defer` inside hot loops (deferred functions run at return, but registration has overhead)
- Reflection (`reflect` package) in performance-critical paths
- Unnecessary goroutine creation for trivial tasks (goroutine stack is 2KB minimum)
- Using `fmt.Sprintf` for simple string concatenation — use `strings.Builder`
- Busy-wait loops with `for { ... }` instead of channels or `sync.Cond`

## Memory
- Allocating large slices with `append` without pre-allocating (`make([]T, 0, cap)`)
- Passing large structs by value instead of pointer
- Holding pointers to slice elements causing the entire backing array to stay in memory
- Using `map` where a `sync.Map` or slice with binary search would be more memory-efficient
- Goroutine leaks (goroutines blocked forever waiting on channels)

## I/O
- Using `ioutil.ReadAll` on large files — use `bufio.Scanner` or streaming
- Using `fmt.Fprintln` per log line in hot paths instead of structured logging
- Opening files with `os.Open` inside loops
- Not using `bufio.Writer` for frequent small writes

## Network
- Sequential HTTP requests when goroutines + `errgroup` would parallelize
- Creating a new `http.Client` per request (reuse with transport pooling)
- Not setting `SetDeadline` on connections (goroutine leaks on hung connections)
- Using JSON for high-throughput internal RPC — consider protobuf
