package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class Ability {

  private final Set<String> semanticTypes;

  protected Ability(AbstractBuilder builder) {
    this.semanticTypes = ImmutableSet.copyOf(builder.semanticTypes);
  }

  public Set<String> getSemanticTypes() {
    return this.semanticTypes;
  }

  public static class Builder extends AbstractBuilder<Builder, Ability> {

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public Ability build() {
      return new Ability(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Ability> {

    protected final Set<String> semanticTypes;

    public AbstractBuilder() {
      this.semanticTypes = new HashSet<>();
    }

    abstract protected S getBuilder();

    public S addSemanticType(final String type) {
      this.semanticTypes.add(type);
      return getBuilder();
    }

    public S addSemanticTypes(final Set<String> types) {
      this.semanticTypes.addAll(types);
      return getBuilder();
    }

    protected abstract T build();
  }
}
