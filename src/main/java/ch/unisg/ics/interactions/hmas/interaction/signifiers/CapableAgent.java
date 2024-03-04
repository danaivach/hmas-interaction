package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.Agent;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class CapableAgent extends Agent {
  private final Set<Ability> abilities;

  protected CapableAgent(AbstractBuilder builder) {
    super(builder);
    this.abilities = ImmutableSet.copyOf(builder.abilities);
  }

  public Set<Ability> getAbilities() {
    return this.abilities;
  }

  public static class Builder extends AbstractBuilder<Builder, CapableAgent> {

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public CapableAgent build() {
      return new CapableAgent(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends CapableAgent>
          extends Agent.AbstractBuilder<S, T> {

    private final Set<Ability> abilities;

    public AbstractBuilder() {
      this.abilities = new HashSet<>();
    }

    public S addAbility(Ability ability) {
      this.abilities.add(ability);
      return (S) this;
    }

    public S addAbilities(Set<Ability> abilities) {
      this.abilities.addAll(abilities);
      return (S) this;
    }

    public abstract T build();
  }
}


