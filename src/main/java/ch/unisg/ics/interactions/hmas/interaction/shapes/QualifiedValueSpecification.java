package ch.unisg.ics.interactions.hmas.interaction.shapes;

import java.util.HashMap;
import java.util.Map;

public class QualifiedValueSpecification extends AbstractIOSpecification {

  private final Map<String, IOSpecification> propertySpecifications;

  protected QualifiedValueSpecification(Builder builder) {
    super(builder);
    this.propertySpecifications = builder.propertySpecifications;
  }

  public Map<String, IOSpecification> getPropertySpecifications() {
    return propertySpecifications;
  }

  public static class Builder extends AbstractIOSpecification.AbstractBuilder<Builder, QualifiedValueSpecification> {

    protected final Map<String, IOSpecification> propertySpecifications;

    public Builder() {
      this.propertySpecifications = new HashMap<>();
    }

    public Builder addPropertySpecification(final String propertySemanticType, IOSpecification specification) {
      this.propertySpecifications.put(propertySemanticType, specification);
      return this;
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    @Override
    public QualifiedValueSpecification build() {
      return new QualifiedValueSpecification(this);
    }
  }

}