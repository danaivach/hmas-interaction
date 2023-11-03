package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import io.vavr.control.Either;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class InputSpecification extends AbstractResource {
  private Set<String> requiredSemanticTypes;
  private Optional<String> qualifiedValueShape = Optional.empty();
  private Optional<String> requiredProperties = Optional.empty();
  private final Set<InputSpecification> inputs = new HashSet<>();
  private Optional<String> name = Optional.empty();
  private Optional<String> description = Optional.empty();
  private Optional<Group> group = Optional.empty();
  private Optional<Integer> order = Optional.empty();
  private Optional<Integer> minCount = Optional.empty();
  private Optional<Integer> maxCount = Optional.empty();
  private Optional<Integer> qualifiedMinCount = Optional.empty();
  private Optional<Integer> qualifiedMaxCount = Optional.empty();
  private Optional<Double> minInclusive = Optional.empty();
  private Optional<Double> maxInclusive = Optional.empty();
  private Optional<Either<Double, String>> defaultValue = Optional.empty();
  private Optional<String> requiredDataType = Optional.empty();
  private Optional<Either<Double, String>> hasValue = Optional.empty();

  protected InputSpecification(AbstractBuilder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
  }

  public Set<String> getRequiredSemanticTypes() {
    return this.requiredSemanticTypes;
  }

  public Set<InputSpecification> getInputs() {
    return this.inputs;
  }

  public Optional<String> getRequiredProperties() {
    return this.requiredProperties;
  }

  public Optional<String> getQualifiedValueShape() {
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

  public Optional<String> getRequiredDataType() {
    return this.requiredDataType;
  }

  public Optional<Either<Double, String>> getHasValue() {
    return this.hasValue;
  }

  public Optional<String> getName() {
    return name;
  }

  public Optional<String> getDescription() {
    return description;
  }

  public Optional<Double> getMinInclusive() {
    return minInclusive;
  }

  public Optional<Double> getMaxInclusive() {
    return maxInclusive;
  }

  public Optional<Either<Double, String>> getDefaultValue() {
    return defaultValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InputSpecification that = (InputSpecification) o;
    return Objects.equals(requiredSemanticTypes, that.requiredSemanticTypes) &&
        Objects.equals(qualifiedValueShape, that.qualifiedValueShape) &&
        Objects.equals(requiredProperties, that.requiredProperties) &&
        Objects.equals(inputs, that.inputs) &&
        Objects.equals(name, that.name) &&
        Objects.equals(description, that.description)&&
        Objects.equals(group, that.group) &&
        Objects.equals(order, that.order) &&
        Objects.equals(minCount, that.minCount) &&
        Objects.equals(maxCount, that.maxCount) &&
        Objects.equals(qualifiedMinCount, that.qualifiedMinCount) &&
        Objects.equals(qualifiedMaxCount, that.qualifiedMaxCount) &&
        Objects.equals(minInclusive, that.minInclusive) &&
        Objects.equals(maxInclusive, that.maxInclusive) &&
        Objects.equals(defaultValue, that.defaultValue) &&
        Objects.equals(requiredDataType, that.requiredDataType) &&
        Objects.equals(hasValue, that.hasValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        requiredSemanticTypes, qualifiedValueShape, requiredProperties, inputs, name, description, group, order,
        minCount, maxCount, qualifiedMinCount, qualifiedMaxCount, minInclusive, maxInclusive, defaultValue,
        requiredDataType, hasValue
    );
  }

  public static class Builder extends AbstractBuilder<Builder, InputSpecification> {
    private final InputSpecification input;

    public Builder() {
      super(SHACL.TERM.NODE_SHAPE);
      this.input = new InputSpecification(this);
    }

    public Builder setName(String name) {
      this.input.name = Optional.of(name);
      return this;
    }

    public Builder setDescription(String description) {
      this.input.description = Optional.of(description);
      return this;
    }

    public Builder setMinInclusive(Double minInclusive) {
      this.input.minInclusive = Optional.of(minInclusive);
      return this;
    }

    public Builder setMaxInclusive(Double maxInclusive) {
      this.input.maxInclusive = Optional.of(maxInclusive);
      return this;
    }

    public Builder setDefaultValue(Either<Double, String> defaultValue) {
      this.input.defaultValue = Optional.of(defaultValue);
      return this;
    }

    public Builder setRequiredSemanticTypes(Set<String> clazz) {
      this.input.requiredSemanticTypes = clazz;
      return this;
    }

    public Builder setQualifiedValueShape(String qualifiedValueShape) {
      this.input.qualifiedValueShape = Optional.of(qualifiedValueShape);
      return this;
    }

    public Builder setInput(InputSpecification shape) {
      this.input.inputs.add(shape);
      return this;
    }

    public Builder setPath(String path) {
      this.input.requiredProperties = Optional.of(path);
      return this;
    }

    public Builder setGroup(Group group) {
      this.input.group = Optional.of(group);
      return this;
    }

    public Builder setOrder(Integer order) {
      this.input.order = Optional.of(order);
      return this;
    }

    public Builder setMinCount(Integer minCount) {
      this.input.minCount = Optional.of(minCount);
      return this;
    }

    public Builder setMaxCount(Integer maxCount) {
      this.input.maxCount = Optional.of(maxCount);
      return this;
    }

    public Builder setQualifiedMinCount(Integer qualifiedMinCount) {
      this.input.qualifiedMinCount = Optional.of(qualifiedMinCount);
      return this;
    }

    public Builder setQualifiedMaxCount(Integer qualifiedMaxCount) {
      this.input.qualifiedMaxCount = Optional.of(qualifiedMaxCount);
      return this;
    }

    public Builder setDataType(String dataType) {
      this.input.requiredDataType = Optional.of(dataType);
      return this;
    }

    public Builder setHasValue(Either<Double, String> hasValue) {
      this.input.hasValue = Optional.of(hasValue);
      return this;
    }

    public InputSpecification build() {
      return this.input;
    }
  }
}
