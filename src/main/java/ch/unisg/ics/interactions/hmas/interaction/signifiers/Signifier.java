package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.BaseSignifier;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Signifier extends BaseSignifier {

  private final ActionSpecification actionSpecification;
  private final Set<Ability> recommendedAbilities;
  private final Optional<String> label;
  private final Optional<String> comment;
  private final Set<Context> recommendedContexts;

  protected Signifier(AbstractBuilder builder) {
    super(builder);
    this.actionSpecification = builder.actionSpecification;
    this.recommendedAbilities = builder.recommendedAbilities;
    this.recommendedContexts = builder.recommendedContexts;
    this.label = Optional.ofNullable(builder.label);
    this.comment = Optional.ofNullable(builder.comment);
  }

  public ActionSpecification getActionSpecification() {
    return this.actionSpecification;
  }

  public Set<Ability> getRecommendedAbilities() {
    return this.recommendedAbilities;
  }

  public Set<Context> getRecommendedContexts() {
    return this.recommendedContexts;
  }

  public Optional<String> getLabel() {
    return this.label;
  }

  public Optional<String> getComment() {
    return this.comment;
  }

  public static class Builder extends AbstractBuilder<Builder, Signifier> {

    public Builder(ActionSpecification actionSpecification) {
      super(actionSpecification);
    }

    public Signifier build() {
      return new Signifier(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Signifier>
          extends BaseSignifier.AbstractBuilder<S, T> {

    protected ActionSpecification actionSpecification;
    protected Set<Ability> recommendedAbilities;
    protected String label;
    protected Set<Context> recommendedContexts;
    protected String comment;

    public AbstractBuilder(ActionSpecification actionSpecification) {
      this.actionSpecification = actionSpecification;
      this.recommendedAbilities = new HashSet<>();
      this.recommendedContexts = new HashSet<>();
      this.label = null;
      this.comment = null;
    }

    public S setLabel(String label) {
      this.label = label;
      return (S) this;
    }

    public S setComment(String comment) {
      this.comment = comment;
      return (S) this;
    }

    public S addRecommendedAbility(Ability ability) {
      this.recommendedAbilities.add(ability);
      return (S) this;
    }

    public S addRecommendedContext(Context context) {
      this.recommendedContexts.add(context);
      return (S) this;
    }

    public abstract T build();
  }
}
