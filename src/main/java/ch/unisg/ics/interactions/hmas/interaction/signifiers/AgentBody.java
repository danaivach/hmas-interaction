package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;

public class AgentBody extends Artifact {

  protected AgentBody(AbstractBuilder builder) {
    super(builder);
  }

  public static class Builder extends AbstractBuilder<Builder, AgentBody> {

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public AgentBody build() {
      return new AgentBody(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends AgentBody>
          extends Artifact.AbstractBuilder<S, T> {

    public abstract T build();
  }
}
