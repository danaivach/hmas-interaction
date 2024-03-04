package ch.unisg.ics.interactions.hmas.interaction.signifiers;

public class AgentProfile extends ResourceProfile {

  protected AgentProfile(AbstractBuilder builder) {
    super(builder);
  }

  public SituatedAgent getAgent() {
    return (SituatedAgent) this.getResource();
  }

  public static class Builder extends AbstractBuilder<AgentProfile.Builder, AgentProfile> {

    public Builder(SituatedAgent agent) {
      super(agent);
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public AgentProfile build() {
      return new AgentProfile(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends ResourceProfile>
          extends ResourceProfile.AbstractBuilder<S, T> {

    public AbstractBuilder(SituatedAgent agent) {
      super(agent);
    }

    public abstract T build();
  }
}
