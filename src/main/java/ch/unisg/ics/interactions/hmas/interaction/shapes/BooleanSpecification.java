package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.Optional;

public class BooleanSpecification extends AbstractValueSpecification {

  private final Optional<Boolean> value;
  private final Optional<Boolean> defaultValue;

  protected BooleanSpecification(Builder builder) {
    super(builder);
    this.value = builder.value;
    this.defaultValue = builder.defaultValue;
  }

  public Optional<Boolean> getValue() {
    return this.value;
  }

  public Optional<Boolean> getDefaultValue() {
    return this.defaultValue;
  }

  public static class Builder extends AbstractValueSpecification.AbstractBuilder<Builder, BooleanSpecification> {

    protected Optional<Boolean> value;
    protected Optional<Boolean> defaultValue;

    public Builder() {
      super(XSD.BOOLEAN.stringValue());
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public Builder setValue(Boolean value) {
      this.value = Optional.of(value);
      return this;
    }

    public Builder setDefaultValue(Boolean value) {
      this.defaultValue = Optional.of(value);
      return this;
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    @Override
    public BooleanSpecification build() {
      return new BooleanSpecification(this);
    }
  }

}
