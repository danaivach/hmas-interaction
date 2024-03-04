package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.Optional;

public class FloatSpecification extends AbstractValueSpecification {

  private final Optional<Float> value;
  private final Optional<Float> defaultValue;
  private final Optional<Float> minInclusiveValue;
  private final Optional<Float> maxInclusiveValue;

  protected FloatSpecification(Builder builder) {
    super(builder);
    this.value = builder.value;
    this.defaultValue = builder.defaultValue;
    this.minInclusiveValue = builder.minInclusiveValue;
    this.maxInclusiveValue = builder.maxInclusiveValue;
  }

  public Optional<Float> getValue() {
    return this.value;
  }

  public Optional<Float> getDefaultValue() {
    return this.defaultValue;
  }

  public static class Builder extends AbstractValueSpecification.AbstractBuilder<Builder, FloatSpecification> {


    protected Optional<Float> value;
    protected Optional<Float> defaultValue;
    protected Optional<Float> minInclusiveValue;
    protected Optional<Float> maxInclusiveValue;

    public Builder() {
      super(XSD.FLOAT.stringValue());
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public Builder setValue(Float value) {
      this.defaultValue.ifPresent(defaultVal -> validateNewValue(value, defaultVal));
      this.value = Optional.of(value);
      return this;
    }

    public Builder setDefaultValue(Float value) {
      this.value.ifPresent(val -> validateNewDefaultValue(val, value));
      this.defaultValue = Optional.of(value);
      return this;
    }

    public Builder setMinInclusiveValue(Float value) {
      this.minInclusiveValue = Optional.of(value);
      return this;
    }

    public Builder setMaxInclusiveValue(Float value) {
      this.maxInclusiveValue = Optional.of(value);
      return this;
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    @Override
    public FloatSpecification build() {
      return new FloatSpecification(this);
    }
  }

}
