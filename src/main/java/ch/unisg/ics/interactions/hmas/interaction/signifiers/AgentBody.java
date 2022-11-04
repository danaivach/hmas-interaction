package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;

public class AgentBody extends Artifact {

  protected AgentBody(AbstractBuilder builder) {
    super(INTERACTION.TERM.AGENT_BODY, builder);
  }

  public static class Builder extends AbstractBuilder<Builder, AgentBody> {

    public AgentBody build() {
      return new AgentBody(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends AgentBody>
          extends Artifact.AbstractBuilder<S, T> {

    public abstract T build();
  }
}
