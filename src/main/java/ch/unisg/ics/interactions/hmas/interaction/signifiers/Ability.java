package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;

public class Ability extends AbstractResource {

  protected Ability(AbstractBuilder builder) {
    super(INTERACTION.TERM.ABILITY, builder);
  }

  public static class Builder extends AbstractBuilder<Builder, Ability> {
    public static final HMAS TYPE = INTERACTION.TERM.ABILITY;

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public Ability build() {
      return new Ability(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Ability>
          extends AbstractResource.AbstractBuilder<S, T> {

    public AbstractBuilder() {
      super(INTERACTION.TERM.ABILITY);
    }

    protected AbstractBuilder(HMAS type) {
      super(type);
    }

    public abstract T build();
  }
}
