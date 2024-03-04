package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class SituatedAgent extends CapableAgent {
  private final Set<AgentBody> bodies;

  protected SituatedAgent(AbstractBuilder builder) {
    super(builder);
    this.bodies = ImmutableSet.copyOf(builder.bodies);
  }

  public Set<AgentBody> getBodies() {
    return this.bodies;
  }

  public static class Builder extends AbstractBuilder<Builder, SituatedAgent> {

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public SituatedAgent build() {
      return new SituatedAgent(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends SituatedAgent>
          extends CapableAgent.AbstractBuilder<S, T> {

    private final Set<AgentBody> bodies;

    public AbstractBuilder() {
      this.bodies = new HashSet<>();
    }

    public S addBody(AgentBody body) {
      this.bodies.add(body);
      return getBuilder();
    }

    public S addBodies(Set<AgentBody> bodies) {
      this.bodies.addAll(bodies);
      return getBuilder();
    }

    public abstract T build();
  }
}

