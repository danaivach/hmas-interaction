package ch.unisg.ics.interactions.hmas.interaction.shapes;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;

import java.util.HashSet;
import java.util.Set;

public class AbstractIOSpecification extends AbstractResource implements IOSpecification {

  private final Set<String> requiredSemanticTypes;
  private final Boolean isRequired;

  protected AbstractIOSpecification(AbstractBuilder builder) {
    super(SHACL.TERM.SHAPE, builder);
    this.requiredSemanticTypes = builder.requiredSemanticTypes;
    this.isRequired = builder.isRequired;
  }

  public Set<String> getRequiredSemanticTypes() {
    return requiredSemanticTypes;
  }

  public Boolean isRequired() {
    return isRequired;
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends AbstractIOSpecification>
          extends AbstractResource.AbstractBuilder<S, T> {

    protected final Set<String> requiredSemanticTypes;
    protected Boolean isRequired;

    public AbstractBuilder() {
      super(SHACL.TERM.SHAPE);
      this.requiredSemanticTypes = new HashSet<>();
      this.isRequired = false;
    }

    public S addRequiredSemanticType(final String type) {
      this.requiredSemanticTypes.add(type);
      return getBuilder();
    }

    public S addRequiredSemanticTypes(final Set<String> types) {
      this.requiredSemanticTypes.addAll(types);
      return getBuilder();
    }

    public S setRequired(Boolean required) {
      this.isRequired = required;
      return getBuilder();
    }

    public abstract T build();
  }
}
