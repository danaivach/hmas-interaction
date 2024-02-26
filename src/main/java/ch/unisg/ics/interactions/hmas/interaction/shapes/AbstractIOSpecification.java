package ch.unisg.ics.interactions.hmas.interaction.shapes;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;

import java.util.HashSet;
import java.util.Set;

public class AbstractIOSpecification extends AbstractResource implements IOSpecification {

  private final Set<String> requiredSemanticTypes;

  protected AbstractIOSpecification(AbstractBuilder builder) {
    super(SHACL.TERM.SHAPE, builder);
    this.requiredSemanticTypes = builder.requiredSemanticTypes;
  }

  public Set<String> getRequiredSemanticTypes() {
    return requiredSemanticTypes;
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends AbstractIOSpecification>
          extends AbstractResource.AbstractBuilder<S, T> {

    protected final Set<String> requiredSemanticTypes;

    public AbstractBuilder() {
      super(SHACL.TERM.SHAPE);
      this.requiredSemanticTypes = new HashSet<>();
    }

    public S addRequiredSemanticType(final String type) {
      this.requiredSemanticTypes.add(type);
      return (S) this;
    }

    public S addRequiredSemanticTypes(final Set<String> types) {
      this.requiredSemanticTypes.addAll(types);
      return (S) this;
    }

    public abstract T build();
  }
}
