package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.Optional;

public class ValueSpecification extends AbstractValueSpecification {

  private final Optional<String> value;
  private final Optional<String> defaultValue;

  protected ValueSpecification(Builder builder) {
    super(builder);
    this.value = builder.value;
    this.defaultValue = builder.defaultValue;
  }

  public Optional<String> getValueAsString() {
    return this.value;
  }

  public Optional<IRI> getValue() {
    return this.value.map(s -> SimpleValueFactory.getInstance().createIRI(s));
  }

  public Optional<String> getDefaultValueAsString() {
    return this.defaultValue;
  }

  public Optional<IRI> getDefaultValue() {
    return this.defaultValue.map(s -> SimpleValueFactory.getInstance().createIRI(s));
  }

  public static class Builder extends AbstractValueSpecification.AbstractBuilder<Builder, ValueSpecification> {

    protected Optional<String> value;
    protected Optional<String> defaultValue;

    public Builder() {
      super(XSD.ANYURI.stringValue());
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public Builder setValueAsString(String value) {
      validateIRI(value);
      this.defaultValue.ifPresent(defaultVal -> validateNewValue(value, defaultVal));
      this.value = Optional.of(value);
      return this;
    }

    public Builder setValue(IRI value) {
      this.defaultValue.ifPresent(defaultVal -> validateNewValue(value.stringValue(), defaultVal));
      this.value = Optional.of(value.stringValue());
      return this;
    }

    public Builder setDefaultValueAsString(String value) {
      this.value.ifPresent(val -> validateNewDefaultValue(val, value));
      this.defaultValue = Optional.of(value);
      return this;
    }

    public Builder setDefaultValue(IRI value) {
      this.value.ifPresent(val -> validateNewDefaultValue(val, value.stringValue()));
      this.defaultValue = Optional.of(value.stringValue());
      return this;
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    @Override
    public ValueSpecification build() {
      return new ValueSpecification(this);
    }
  }

}