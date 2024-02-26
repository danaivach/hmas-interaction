package ch.unisg.ics.interactions.hmas.interaction.shapes;

import java.util.HashMap;
import java.util.Map;

public class QualifiedValueSpecification extends AbstractIOSpecification {

  private final Map<String, IOSpecification> propertySpecifications;
  private final Boolean isRequired;

  protected QualifiedValueSpecification(Builder builder) {
    super(builder);
    this.propertySpecifications = builder.propertySpecifications;
    this.isRequired = builder.isRequired;
  }

  public Map<String, IOSpecification> getPropertySpecifications() {
    return propertySpecifications;
  }

  public Boolean isRequired() {
    return this.isRequired;
  }

  public static class Builder extends AbstractIOSpecification.AbstractBuilder<Builder, QualifiedValueSpecification> {

    protected final Map<String, IOSpecification> propertySpecifications;
    protected Boolean isRequired;

    public Builder() {
      this.propertySpecifications = new HashMap<>();
      this.isRequired = false;
    }

    public Builder addPropertySpecification(final String propertySemanticType, IOSpecification specification) {
      this.propertySpecifications.put(propertySemanticType, specification);
      return this;
    }

    public Builder setRequired(Boolean required) {
      this.isRequired = required;
      return this;
    }

    @Override
    public QualifiedValueSpecification build() {
      return new QualifiedValueSpecification(this);
    }
  }

}