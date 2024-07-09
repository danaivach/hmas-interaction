package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.Optional;

public class IntegerSpecification extends AbstractValueSpecification {

  private final Optional<Integer> value;
  private final Optional<Integer> defaultValue;
  private final Optional<Integer> minInclusiveValue;
  private final Optional<Integer> maxInclusiveValue;

  protected IntegerSpecification(Builder builder) {
    super(builder);
    this.value = builder.value;
    this.defaultValue = builder.defaultValue;
    this.minInclusiveValue = builder.minInclusiveValue;
    this.maxInclusiveValue = builder.maxInclusiveValue;
  }

  public Optional<Integer> getValue() {
    return this.value;
  }

  public Optional<Integer> getDefaultValue() {
    return this.defaultValue;
  }

  public static class Builder extends AbstractValueSpecification.AbstractBuilder<Builder, IntegerSpecification> {

    protected Optional<Integer> value;
    protected Optional<Integer> defaultValue;
    protected Optional<Integer> minInclusiveValue;
    protected Optional<Integer> maxInclusiveValue;

    public Builder() {
      super(XSD.INT.stringValue());
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public Builder setValue(Integer value) {
      this.value = Optional.of(value);
      return this;
    }

    public Builder setDefaultValue(Integer value) {
      this.defaultValue = Optional.of(value);
      return this;
    }

    public Builder setMinInclusiveValue(Integer value) {
      this.minInclusiveValue = Optional.of(value);
      return this;
    }

    public Builder setMaxInclusiveValue(Integer value) {
      this.maxInclusiveValue = Optional.of(value);
      return this;
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    @Override
    public IntegerSpecification build() {
      return new IntegerSpecification(this);
    }
  }

}
