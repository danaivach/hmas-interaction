package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import io.vavr.control.Either;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class AbstractIOSpecification extends AbstractResource {

  private final Set<String> requiredSemanticTypes;
  private final Optional<String> qualifiedValueShape;
  private final Optional<String> requiredProperties;
  private final Optional<String> name;
  private final Optional<String> description;
  private final Optional<Group> group;
  private final Optional<Integer> order;
  private final Optional<Integer> minCount;
  private final Optional<Integer> maxCount;
  private final Optional<Integer> qualifiedMinCount;
  private final Optional<Integer> qualifiedMaxCount;
  private final Optional<Double> minInclusive;
  private final Optional<Double> maxInclusive;
  private final Optional<Either<Double, String>> defaultValue;
  private final Optional<String> requiredDataType;
  private final Optional<Either<Double, String>> hasValue;

  protected AbstractIOSpecification(AbstractBuilder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
    this.requiredSemanticTypes = builder.requiredSemanticTypes;
    this.qualifiedValueShape = builder.qualifiedValueShape;
    this.requiredProperties = builder.requiredProperties;
    this.name = builder.name;
    this.description = builder.description;
    this.group = builder.group;
    this.order = builder.order;
    this.minCount = builder.minCount;
    this.maxCount = builder.maxCount;
    this.qualifiedMinCount = builder.qualifiedMinCount;
    this.qualifiedMaxCount = builder.qualifiedMaxCount;
    this.minInclusive = builder.minInclusive;
    this.maxInclusive = builder.maxInclusive;
    this.defaultValue = builder.defaultValue;
    this.requiredDataType = builder.requiredDataType;
    this.hasValue = builder.hasValue;
  }

  public Set<String> getRequiredSemanticTypes() {
    return this.requiredSemanticTypes;
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
    AbstractIOSpecification that = (AbstractIOSpecification) o;
    return Objects.equals(requiredSemanticTypes, that.requiredSemanticTypes) &&
            Objects.equals(qualifiedValueShape, that.qualifiedValueShape) &&
            Objects.equals(requiredProperties, that.requiredProperties) &&
            //Objects.equals(inputs, that.inputs) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
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
            requiredSemanticTypes, qualifiedValueShape, requiredProperties, name, description, group, order,
            minCount, maxCount, qualifiedMinCount, qualifiedMaxCount, minInclusive, maxInclusive, defaultValue,
            requiredDataType, hasValue
    );
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends AbstractIOSpecification>
          extends AbstractResource.AbstractBuilder<S, T> {

    protected Set<String> requiredSemanticTypes;
    protected Optional<String> qualifiedValueShape;
    protected Optional<String> requiredProperties;
    protected Optional<String> name;
    protected Optional<String> description;
    protected Optional<Group> group;
    protected Optional<Integer> order;
    protected Optional<Integer> minCount;
    protected Optional<Integer> maxCount;
    protected Optional<Integer> qualifiedMinCount;
    protected Optional<Integer> qualifiedMaxCount;
    protected Optional<Double> minInclusive;
    protected Optional<Double> maxInclusive;
    protected Optional<Either<Double, String>> defaultValue;
    protected Optional<String> requiredDataType;
    protected Optional<Either<Double, String>> hasValue;

    public AbstractBuilder() {
      super(SHACL.TERM.NODE_SHAPE);
      this.requiredSemanticTypes = new HashSet<>();
      this.qualifiedValueShape = Optional.empty();
      this.requiredProperties = Optional.empty();
      this.name = Optional.empty();
      this.description = Optional.empty();
      this.group = Optional.empty();
      this.order = Optional.empty();
      this.minCount = Optional.empty();
      this.maxCount = Optional.empty();
      this.qualifiedMinCount = Optional.empty();
      this.qualifiedMaxCount = Optional.empty();
      this.minInclusive = Optional.empty();
      this.maxInclusive = Optional.empty();
      this.defaultValue = Optional.empty();
      this.requiredDataType = Optional.empty();
      this.hasValue = Optional.empty();
    }

    public S setName(String name) {
      this.name = Optional.of(name);
      return (S) this;
    }

    public S setDescription(String description) {
      this.description = Optional.of(description);
      return (S) this;
    }

    public S setMinInclusive(Double minInclusive) {
      this.minInclusive = Optional.of(minInclusive);
      return (S) this;
    }

    public S setMaxInclusive(Double maxInclusive) {
      this.maxInclusive = Optional.of(maxInclusive);
      return (S) this;
    }

    public S setDefaultValue(Either<Double, String> defaultValue) {
      this.defaultValue = Optional.of(defaultValue);
      return (S) this;
    }

    public S setRequiredSemanticTypes(Set<String> clazz) {
      this.requiredSemanticTypes = clazz;
      return (S) this;
    }

    public S setQualifiedValueShape(String qualifiedValueShape) {
      this.qualifiedValueShape = Optional.of(qualifiedValueShape);
      return (S) this;
    }

    public S setPath(String path) {
      this.requiredProperties = Optional.of(path);
      return (S) this;
    }

    public S setGroup(Group group) {
      this.group = Optional.of(group);
      return (S) this;
    }

    public S setOrder(Integer order) {
      this.order = Optional.of(order);
      return (S) this;
    }

    public S setMinCount(Integer minCount) {
      this.minCount = Optional.of(minCount);
      return (S) this;
    }

    public S setMaxCount(Integer maxCount) {
      this.maxCount = Optional.of(maxCount);
      return (S) this;
    }

    public S setQualifiedMinCount(Integer qualifiedMinCount) {
      this.qualifiedMinCount = Optional.of(qualifiedMinCount);
      return (S) this;
    }

    public S setQualifiedMaxCount(Integer qualifiedMaxCount) {
      this.qualifiedMaxCount = Optional.of(qualifiedMaxCount);
      return (S) this;
    }

    public S setDataType(String dataType) {
      this.requiredDataType = Optional.of(dataType);
      return (S) this;
    }

    public S setHasValue(Either<Double, String> hasValue) {
      this.hasValue = Optional.of(hasValue);
      return (S) this;
    }

    public abstract T build();
  }
}
