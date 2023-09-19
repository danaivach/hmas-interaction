package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import io.vavr.control.Either;

import java.util.Objects;
import java.util.Optional;

public class SimpleInputSpecification implements InputSpecification {
  private String requiredProperties;
  private String requiredDataType;
  private Optional<String> name = Optional.empty();
  private Optional<String> description = Optional.empty();
  private Optional<Integer> order = Optional.empty();
  private Optional<Integer> minCount = Optional.empty();
  private Optional<Integer> maxCount = Optional.empty();
  private Optional<Integer> qualifiedMinCount = Optional.empty();
  private Optional<Integer> qualifiedMaxCount = Optional.empty();
  private Optional<Double> minInclusive = Optional.empty();
  private Optional<Double> maxInclusive = Optional.empty();
  private Optional<Either<Double, String>> defaultValue = Optional.empty();
  private Optional<Group> group = Optional.empty();
  private Optional<Either<Double, String>> hasValue = Optional.empty();

  public String getRequiredProperties() {
    return this.requiredProperties;
  }

  public Optional<String> getName() {
    return this.name;
  }

  public Optional<String> getDescription() {
    return this.description;
  }

  public String getRequiredDataType() {
    return this.requiredDataType;
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

  public Optional<Double> getMinInclusive() {
    return this.minInclusive;
  }

  public Optional<Double> getMaxInclusive() {
    return this.maxInclusive;
  }

  public Optional<Either<Double, String>> getDefaultValue() {
    return this.defaultValue;
  }

  public Optional<Group> getGroup() {
    return this.group;
  }

  public Optional<Either<Double, String>> getHasValue() {
    return this.hasValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SimpleInputSpecification that = (SimpleInputSpecification) o;
    return Objects.equals(requiredProperties, that.requiredProperties)
        && Objects.equals(requiredDataType, that.requiredDataType)
        && Objects.equals(name, that.name)
        && Objects.equals(description, that.description)
        && Objects.equals(order, that.order)
        && Objects.equals(minCount, that.minCount)
        && Objects.equals(maxCount, that.maxCount)
        && Objects.equals(qualifiedMinCount, that.qualifiedMinCount)
        && Objects.equals(qualifiedMaxCount, that.qualifiedMaxCount)
        && Objects.equals(minInclusive, that.minInclusive)
        && Objects.equals(maxInclusive, that.maxInclusive)
        && Objects.equals(defaultValue, that.defaultValue)
        && Objects.equals(group, that.group)
        && Objects.equals(hasValue, that.hasValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requiredProperties, requiredDataType, name, description, order, minCount, maxCount, qualifiedMinCount,
        qualifiedMaxCount, minInclusive, maxInclusive, defaultValue, group);
  }

  public static class Builder {
    private final SimpleInputSpecification input;

    public Builder(String path) {
      this.input = new SimpleInputSpecification();
      this.input.requiredProperties = path;
    }

    public Builder withName(String name) {
      this.input.name = Optional.of(name);
      return this;
    }

    public Builder withDescription(String description) {
      this.input.description = Optional.of(description);
      return this;
    }

    public Builder withDataType(String dataType) {
      this.input.requiredDataType = dataType;
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

    public Builder withMinInclusive(Double minInclusive) {
      this.input.minInclusive = Optional.of(minInclusive);
      return this;
    }

    public Builder withMaxInclusive(Double maxInclusive) {
      this.input.maxInclusive = Optional.of(maxInclusive);
      return this;
    }

    public Builder withDefaultValue(Either<Double, String> defaultValue) {
      this.input.defaultValue = Optional.of(defaultValue);
      return this;
    }

    public Builder withGroup(Group group) {
      this.input.group = Optional.of(group);
      return this;
    }

    public Builder withHasValue(Either<Double, String> hasValue) {
      this.input.hasValue = Optional.of(hasValue);
      return this;
    }

    public SimpleInputSpecification build() {
      return this.input;
    }
  }
}
