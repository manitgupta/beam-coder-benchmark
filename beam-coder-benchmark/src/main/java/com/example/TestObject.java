package com.example;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.coders.SerializableCoder;

/**
 * A simple POJO to test serialization efficiency.
 */
@DefaultCoder(SerializableCoder.class)
public class TestObject implements Serializable {
  private String id;
  private String name;
  private int age;
  private List<String> tags;
  private Map<String, Integer> scores;
  private boolean active;

  public TestObject() {}

  public TestObject(String id, String name, int age, List<String> tags, Map<String, Integer> scores, boolean active) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.tags = tags;
    this.scores = scores;
    this.active = active;
  }

  // Getters and Setters are required for Avro reflection in some cases, 
  // though field visibility might be enough depending on configuration.
  // Standard getters/setters are good practice.

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public int getAge() { return age; }
  public void setAge(int age) { this.age = age; }

  public List<String> getTags() { return tags; }
  public void setTags(List<String> tags) { this.tags = tags; }

  public Map<String, Integer> getScores() { return scores; }
  public void setScores(Map<String, Integer> scores) { this.scores = scores; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TestObject that = (TestObject) o;
    return age == that.age &&
        active == that.active &&
        Objects.equals(id, that.id) &&
        Objects.equals(name, that.name) &&
        Objects.equals(tags, that.tags) &&
        Objects.equals(scores, that.scores);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, age, tags, scores, active);
  }
}
