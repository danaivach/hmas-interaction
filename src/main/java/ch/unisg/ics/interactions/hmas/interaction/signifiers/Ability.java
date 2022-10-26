package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractHostable;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class Ability {

  private final Set<String> semanticTypes;

  protected Ability(AbstractBuilder builder){
    this.semanticTypes = ImmutableSet.copyOf(builder.semanticTypes);
  }

  public Set<String> getSemanticTypes() {
    return this.semanticTypes;
  }

  public static class Builder extends AbstractBuilder<Builder, Ability> {

    public Ability build() {
      return new Ability(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Ability> {

    protected final Set<String> semanticTypes;

    public AbstractBuilder() {
      this.semanticTypes= new HashSet<>();
    }

    public S addSemanticType(final String type) {
      this.semanticTypes.add(type);
      return (S) this;
    }

    public S addHostedResources(final Set<String> types) {
      this.semanticTypes.addAll(types);
      return (S) this;
    }

    protected abstract T build();
  }
}
