package ch.unisg.ics.interactions.hmas.interaction.signifiers;

public class Ability {

  protected Ability(AbstractBuilder builder){

  }

  public static class Builder extends AbstractBuilder<Builder, Ability> {

    public Ability build() {
      return new Ability(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Ability> {

    protected abstract T build();
  }
}
