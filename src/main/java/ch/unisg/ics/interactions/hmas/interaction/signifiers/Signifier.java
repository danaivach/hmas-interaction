package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.BaseSignifier;

import java.util.HashSet;
import java.util.Set;

public class Signifier extends BaseSignifier {

  private final BehavioralSpecification behavioralSpecification;
  private final Set<Ability> recommendedAbilities;

  protected Signifier(AbstractBuilder builder) {
    super(builder);
    this.behavioralSpecification = builder.behavioralSpecification;
    this.recommendedAbilities = builder.recommendedAbilities;
  }

  public BehavioralSpecification getBehavioralSpecification() {
    return this.behavioralSpecification;
  }

  public Set<Ability> getRecommendedAbilities() {
    return this.recommendedAbilities;
  }

  public static class Builder extends AbstractBuilder<Builder, Signifier> {

    public Builder(BehavioralSpecification behavioralSpecification) {
      super(behavioralSpecification);
    }

    public Signifier build() {
      return new Signifier(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Signifier>
          extends BaseSignifier.AbstractBuilder<S, T> {

    protected BehavioralSpecification behavioralSpecification;
    protected Set<Ability> recommendedAbilities;

    public AbstractBuilder(BehavioralSpecification behavioralSpecification) {
      this.behavioralSpecification = behavioralSpecification;
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
