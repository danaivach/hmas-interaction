package ch.unisg.ics.interactions.hmas.interaction.shapes;

import ch.unisg.ics.interactions.hmas.core.hostables.BaseSignifier;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.ActionSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Signifier;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.Optional;
import java.util.Set;

public class ValueSpecification extends AbstractValueSpecification {

  private final Optional<String> value;
  private final Optional<String> defaultValue;

  protected ValueSpecification(AbstractBuilder builder) {
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

  public static class Builder extends AbstractBuilder<Builder, ValueSpecification> {

    public Builder() {
      super(XSD.ANYURI.stringValue());
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public ValueSpecification build() {
      return new ValueSpecification(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends ValueSpecification>
          extends AbstractValueSpecification.AbstractBuilder<S, T>{

    protected Optional<String> value;
    protected Optional<String> defaultValue;

    public AbstractBuilder(String dataType) {
      super(dataType);
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public AbstractBuilder(Set<String> dataTypes) {
      super(dataTypes);
      this.value = Optional.empty();
      this.defaultValue = Optional.empty();
    }

    public S setValueAsString(String value) {
      validateIRI(value);
      this.value = Optional.of(value);
      return getBuilder();
    }

    public S setValue(IRI value) {
      this.value = Optional.of(value.stringValue());
      return getBuilder();
    }

    public S setDefaultValueAsString(String value) {
      this.defaultValue = Optional.of(value);
      return getBuilder();
    }

    public S setDefaultValue(IRI value) {
      this.defaultValue = Optional.of(value.stringValue());
      return getBuilder();
    }

    public abstract T build();
  }

}