package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.core.hostables.BaseSignifier;

import java.util.HashSet;
import java.util.Set;

public class Signifier extends BaseSignifier {

  private final AbstractResource resource;
  private final Set<Ability> recommendedAbilities;

  protected Signifier(AbstractBuilder builder) {
    super(builder);
    this.resource = builder.resource;
    this.recommendedAbilities = builder.recommendedAbilities;
  }

  public AbstractResource getResource() {
    return this.resource;
  }

  public Set<Ability> getRecommendedAbilities() {
    return this.recommendedAbilities;
  }

  public static class Builder extends AbstractBuilder<Builder, Signifier> {

    public Builder(AbstractResource resource) {
      super(resource);
    }

    public Signifier build() {
      return new Signifier(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Signifier>
          extends BaseSignifier.AbstractBuilder<S, T> {

    protected AbstractResource resource;
    protected Set<Ability> recommendedAbilities;

    public AbstractBuilder(AbstractResource resource) {
      this.resource = resource;
      this.recommendedAbilities = new HashSet<>();
    }

    public S addRecommendedAbility(Ability ability) {
      this.recommendedAbilities.add(ability);
      return (S) this;
    }

    public S addRecommendedAbilities(Set<Ability> abilities) {
      this.recommendedAbilities.addAll(abilities);
      return (S) this;
    }

    public abstract T build();
  }
}
