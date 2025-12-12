# AvroCoder vs SerializableCoder Benchmark Results

## Overview
We created a benchmark to compare `SerializableCoder` (Java Serialization) and `AvroCoder` (Apache Avro) for a typical POJO in an Apache Beam pipeline.

## Benchmark Setup
- **Object Type**: `TestObject` (POJO with String, int, List, Map, boolean)
- **Count**: 100,000 objects
- **Iterations**: 10 (after warmup)
- **Environment**: Local execution

## Results

| Metric | SerializableCoder | AvroCoder | Difference |
| :--- | :--- | :--- | :--- |
| **Avg Serialize Time** | ~169 ms | ~38 ms | **~4.4x Faster** |
| **Avg Deserialize Time** | ~935 ms | ~111 ms | **~8.4x Faster** |
| **Avg Size per Object** | ~491 bytes | ~58 bytes | **~8.5x Smaller** |
| **Total Size** | 46.79 MB | 5.51 MB | **~8.5x Smaller** |

## Conclusion
`AvroCoder` is vastly superior to `SerializableCoder` for both performance and storage efficiency.
- **CPU**: Serialization and deserialization are much faster, which reduces CPU usage and processing time in Dataflow.
- **Network/Storage**: The serialized size is nearly an order of magnitude smaller, which significantly reduces network I/O (shuffle) and storage costs.

**Recommendation**: Always prefer `AvroCoder` (or `Schema`-based coders) over `SerializableCoder` for custom types in production pipelines.

## How to Run
```bash
cd beam-coder-benchmark
mvn clean package -DskipTests
mvn exec:java -Dexec.mainClass="com.example.Benchmark"
```
