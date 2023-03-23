package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractHostable;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class BehavioralSpecification extends AbstractHostable {

  private final Set<Signifier> evaluabilitySignifiers;

  protected BehavioralSpecification(AbstractBuilder builder) {
    this(INTERACTION.TERM.BEHAVIORAL_SPECIFICATION, builder);
  }

  protected BehavioralSpecification(HMAS type, AbstractBuilder builder) {
    super(type, builder);
    this.evaluabilitySignifiers = ImmutableSet.copyOf(builder.evaluabilitySignifiers);
  }

  public Set<Signifier> getEvaluabilitySignifiers() {
    return this.evaluabilitySignifiers;
  }

  public static class Builder extends AbstractBuilder<Builder, BehavioralSpecification> {

    public BehavioralSpecification build() {
      return new BehavioralSpecification(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends BehavioralSpecification>
          extends AbstractHostable.AbstractBuilder<S, T> {

    private final Set<Signifier> evaluabilitySignifiers;

    protected AbstractBuilder() {
      this.evaluabilitySignifiers = new HashSet<>();
    }

    public S addEvaluabilitySignifier(Signifier signifier) {
      evaluabilitySignifiers.add(signifier);
      return (S) this;
    }

    public S addEvaluabilitySignifiers(Set<Signifier> signifiers) {
      evaluabilitySignifiers.addAll(signifiers);
      return (S) this;
    }

    public abstract T build();
  }

}
