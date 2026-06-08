# Java Energy Anti-Patterns

## CPU
- `String` concatenation with `+` in loops — use `StringBuilder`
- Autoboxing in hot loops (`Integer i = 0; i++` creates many objects)
- Using `stream()` on collections for simple operations where a for-each loop would suffice
- Synchronized methods on hot paths where `Lock` or atomics would be better
- Excessive exception throwing/catching in normal control flow (stack traces are expensive)
- Using `Optional.get()` without `isPresent()` check (redundant object creation)
- Reflection in hot paths (cache `MethodHandle`/`Method` objects)

## Memory
- Creating large temporary arrays/collections inside methods called frequently
- Holding references to objects in static collections (memory leaks)
- Using `new String("literal")` instead of string literals
- Inefficient `HashMap` with poor `hashCode()` implementation causing O(n) lookups
- Not using primitive collections (e.g., `IntArrayList` instead of `ArrayList<Integer>`)

## I/O
- Using `FileInputStream` without buffering (wrap in `BufferedInputStream`)
- Reading/writing one character at a time with `FileReader`/`FileWriter`
- Creating `PreparedStatement` inside loops instead of using batch operations
- Using blocking I/O in reactive/async frameworks (Quarkus, WebFlux)

## Network
- N+1 queries with JPA/Hibernate — missing `@EntityGraph` or `JOIN FETCH`
- Opening `HttpURLConnection` per request instead of connection pooling
- Using REST calls where gRPC would be more efficient for internal services
- Deserializing full responses when only specific fields are needed
