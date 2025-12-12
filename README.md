# AvroCoder vs SerializableCoder Benchmark Results

## Overview
We created a benchmark to compare `SerializableCoder` (Java Serialization) and `AvroCoder` (Apache Avro) for a typical POJO in an Apache Beam pipeline.

## Benchmark Setup
- **Object Type**: `TestObject` (POJO with String, int, List, Map, boolean)
- **Count**: 1,000,000 objects
- **Iterations**: 10 (after warmup)
- **Environment**: Local execution

## Results (1,000,000 Objects)

| Metric | SerializableCoder | AvroCoder | Difference |
| :--- | :--- | :--- | :--- |
| **Avg Serialize Time** | ~2360 ms | ~384 ms | **~6.1x Faster** |
| **Avg Deserialize Time** | ~9567 ms | ~754 ms | **~12.7x Faster** |
| **Avg Size per Object** | ~492 bytes | ~59 bytes | **~8.3x Smaller** |
| **Total Size** | 468.85 MB | 56.04 MB | **~8.3x Smaller** |

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

