package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import io.vavr.control.Either;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class CompoundInput implements Input {
  private String clazz;
  private String qualifiedValueShape;
  private Optional<String> path = Optional.empty();
  private final Set<Input> inputs = new HashSet<>();
  private Optional<Group> group = Optional.empty();
  private Optional<Integer> order = Optional.empty();
  private Optional<Integer> minCount = Optional.empty();
  private Optional<Integer> maxCount = Optional.empty();
  private Optional<Integer> qualifiedMinCount = Optional.empty();
  private Optional<Integer> qualifiedMaxCount = Optional.empty();
  private Optional<String> dataType = Optional.empty();
  private Optional<Either<Double, String>> hasValue = Optional.empty();

  public String getClazz() {
    return this.clazz;
  }

  public Set<Input> getInputs() {
    return this.inputs;
  }

  public Optional<String> getPath() {
    return this.path;
  }

  public String getQualifiedValueShape() {
    return this.qualifiedValueShape;
  }

  public Optional<Group> getGroup() {
    return this.group;
  }

  public Optional<Integer> getOrder() {
    return this.order;
  }

  public Optional<Integer> getMinCount() {
    return this.minCount;
  }

  public Optional<Integer> getMaxCount() {
    return this.maxCount;
  }

  public Optional<Integer> getQualifiedMinCount() {
    return this.qualifiedMinCount;
  }

  public Optional<Integer> getQualifiedMaxCount() {
    return this.qualifiedMaxCount;
  }

  public Optional<String> getDataType() {
    return this.dataType;
  }

  public Optional<Either<Double, String>> getHasValue() {
    return this.hasValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CompoundInput that = (CompoundInput) o;
    return Objects.equals(clazz, that.clazz)
        && Objects.equals(qualifiedValueShape, that.qualifiedValueShape)
        && Objects.equals(path, that.path)
        && Objects.equals(inputs, that.inputs)
        && Objects.equals(group, that.group)
        && Objects.equals(order, that.order)
        && Objects.equals(minCount, that.minCount)
        && Objects.equals(maxCount, that.maxCount)
        && Objects.equals(qualifiedMinCount, that.qualifiedMinCount)
        && Objects.equals(qualifiedMaxCount, that.qualifiedMaxCount)
        && Objects.equals(dataType, that.dataType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clazz, qualifiedValueShape, path, inputs, group, order);
  }

  public static class Builder {
    private final CompoundInput input;

    public Builder() {
      this.input = new CompoundInput();
    }

    public Builder withClazz(String clazz) {
      this.input.clazz = clazz;
      return this;
    }

    public Builder withQualifiedValueShape(String qualifiedValueShape) {
      this.input.qualifiedValueShape = qualifiedValueShape;
      return this;
    }

    public Builder withInput(Input shape) {
      this.input.inputs.add(shape);
      return this;
    }

    public Builder withPath(String path) {
      this.input.path = Optional.of(path);
      return this;
    }

    public Builder withGroup(Group group) {
      this.input.group = Optional.of(group);
      return this;
    }

    public Builder withOrder(Integer order) {
      this.input.order = Optional.of(order);
      return this;
    }

    public Builder withMinCount(Integer minCount) {
      this.input.minCount = Optional.of(minCount);
      return this;
    }

    public Builder withMaxCount(Integer maxCount) {
      this.input.maxCount = Optional.of(maxCount);
      return this;
    }

    public Builder withQualifiedMinCount(Integer qualifiedMinCount) {
      this.input.qualifiedMinCount = Optional.of(qualifiedMinCount);
      return this;
    }

    public Builder withQualifiedMaxCount(Integer qualifiedMaxCount) {
      this.input.qualifiedMaxCount = Optional.of(qualifiedMaxCount);
      return this;
    }

    public Builder withDataType(String dataType) {
      this.input.dataType = Optional.of(dataType);
      return this;
    }

    public Builder withHasValue(Either<Double, String> hasValue) {
      this.input.hasValue = Optional.of(hasValue);
      return this;
    }

    public CompoundInput build() {
      return this.input;
    }
  }
}
