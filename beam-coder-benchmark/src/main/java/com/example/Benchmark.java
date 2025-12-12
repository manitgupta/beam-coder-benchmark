package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;

public class Benchmark {

  private static final int NUM_OBJECTS = 1_000_000;
  private static final int WARMUP_ITERATIONS = 5;
  private static final int BENCHMARK_ITERATIONS = 10;

  public static void main(String[] args) throws IOException {
    System.out.println("Generating data...");
    List<TestObject> data = generateData(NUM_OBJECTS);
    System.out.println("Data generated. Starting benchmark...");

    Coder<TestObject> serializableCoder = SerializableCoder.of(TestObject.class);
    // Use AvroCoder with reflection for the POJO
    Coder<TestObject> avroCoder = AvroCoder.of(TestObject.class);

    System.out.println("\n--- SerializableCoder ---");
    runBenchmark(serializableCoder, data);

    System.out.println("\n--- AvroCoder ---");
    runBenchmark(avroCoder, data);
  }

  private static void runBenchmark(Coder<TestObject> coder, List<TestObject> data) throws IOException {
    // Warmup
    for (int i = 0; i < WARMUP_ITERATIONS; i++) {
        measureSerialization(coder, data.subList(0, 1000));
    }

    long totalSerializeTime = 0;
    long totalDeserializeTime = 0;
    long totalBytes = 0;

    for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
      Result res = measureSerialization(coder, data);
      totalSerializeTime += res.serializeTimeMs;
      totalDeserializeTime += res.deserializeTimeMs;
      totalBytes = res.totalBytes; // Size is constant for the same data
    }

    System.out.printf("Avg Serialize Time: %.2f ms%n", (double) totalSerializeTime / BENCHMARK_ITERATIONS);
    System.out.printf("Avg Deserialize Time: %.2f ms%n", (double) totalDeserializeTime / BENCHMARK_ITERATIONS);
    System.out.printf("Total Size: %.2f MB%n", totalBytes / (1024.0 * 1024.0));
    System.out.printf("Avg Size per Object: %.2f bytes%n", (double) totalBytes / data.size());
  }

  private static Result measureSerialization(Coder<TestObject> coder, List<TestObject> data) throws IOException {
    long start = System.currentTimeMillis();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    for (TestObject obj : data) {
      coder.encode(obj, bos);
    }
    long serializeTime = System.currentTimeMillis() - start;
    byte[] bytes = bos.toByteArray();

    start = System.currentTimeMillis();
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    for (int i = 0; i < data.size(); i++) {
      coder.decode(bis);
    }
    long deserializeTime = System.currentTimeMillis() - start;

    return new Result(serializeTime, deserializeTime, bytes.length);
  }

  private static List<TestObject> generateData(int count) {
    List<TestObject> list = new ArrayList<>(count);
    Random rand = new Random(12345); // Fixed seed for reproducibility
    for (int i = 0; i < count; i++) {
      String id = "id-" + i;
      String name = "name-" + rand.nextInt(10000);
      int age = rand.nextInt(100);
      List<String> tags = Arrays.asList("tag1", "tag2", "tag-" + rand.nextInt(100));
      Map<String, Integer> scores = new HashMap<>();
      scores.put("math", rand.nextInt(100));
      scores.put("science", rand.nextInt(100));
      boolean active = rand.nextBoolean();
      
      list.add(new TestObject(id, name, age, tags, scores, active));
    }
    return list;
  }

  private static class Result {
    long serializeTimeMs;
    long deserializeTimeMs;
    long totalBytes;

    Result(long s, long d, long b) {
      this.serializeTimeMs = s;
      this.deserializeTimeMs = d;
      this.totalBytes = b;
    }
  }
}
