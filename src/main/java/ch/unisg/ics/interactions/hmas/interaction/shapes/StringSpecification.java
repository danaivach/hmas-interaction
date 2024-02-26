package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.Optional;

public class StringSpecification extends AbstractValueSpecification {

  private final Optional<String> value;
  private final Optional<String> defaultValue;

  protected StringSpecification(Builder builder) {
    super(builder);
    this.value = builder.value;
    this.defaultValue = builder.defaultValue;
  }

  public Optional<String> getValue() {
    return this.value;
  }

  public Optional<String> getDefaultValue() {
    return this.defaultValue;
  }

  public static class Builder extends AbstractValueSpecification.AbstractBuilder<Builder, StringSpecification> {

    protected Optional<String> value;
    protected Optional<String> defaultValue;

    public Builder() {
      super(XSD.STRING.stringValue());
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public Builder setValue(String value) {
      this.defaultValue.ifPresent(defaultVal -> validateNewValue(value, defaultVal));
      this.value = Optional.of(value);
      return this;
    }

    public Builder setDefaultValue(String value) {
      this.value.ifPresent(val -> validateNewDefaultValue(val, value));
      this.defaultValue = Optional.of(value);
      return this;
    }

    @Override
    public StringSpecification build() {
      return new StringSpecification(this);
    }
  }
}