package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.Optional;

public class DoubleSpecification extends AbstractValueSpecification {

  private final Optional<Double> value;
  private final Optional<Double> defaultValue;
  private final Optional<Double> minInclusiveValue;
  private final Optional<Double> maxInclusiveValue;

  protected DoubleSpecification(Builder builder) {
    super(builder);
    this.value = builder.value;
    this.defaultValue = builder.defaultValue;
    this.minInclusiveValue = builder.minInclusiveValue;
    this.maxInclusiveValue = builder.maxInclusiveValue;
  }

  public Optional<Double> getValue() {
    return this.value;
  }

  public Optional<Double> getDefaultValue() {
    return this.defaultValue;
  }

  public Optional<Double> getMinInclusiveValue() {
    return this.minInclusiveValue;
  }

  public Optional<Double> getMaxInclusiveValue() {
    return this.maxInclusiveValue;
  }

  public static class Builder extends AbstractValueSpecification.AbstractBuilder<Builder, DoubleSpecification> {


    protected Optional<Double> value;
    protected Optional<Double> defaultValue;
    protected Optional<Double> minInclusiveValue;
    protected Optional<Double> maxInclusiveValue;

    public Builder() {
      super(XSD.DOUBLE.stringValue());
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public Builder setValue(Double value) {
      this.value = Optional.of(value);
      return this;
    }

    public Builder setDefaultValue(Double value) {
      this.defaultValue = Optional.of(value);
      return this;
    }

    public Builder setMinInclusiveValue(Double value) {
      this.minInclusiveValue = Optional.of(value);
      return this;
    }

    public Builder setMaxInclusiveValue(Double value) {
      this.maxInclusiveValue = Optional.of(value);
      return this;
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    @Override
    public DoubleSpecification build() {
      return new DoubleSpecification(this);
    }
  }

}
