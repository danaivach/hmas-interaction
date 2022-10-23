package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class ActionSpecification extends BehavioralSpecification {

  private final Set<Form> forms;

  protected ActionSpecification(AbstractBuilder builder) {
    super(builder);
    this.forms = ImmutableSet.copyOf(builder.forms);
  }

  public Set<Form> getForms() { return this.forms; }

  public static class Builder extends AbstractBuilder<Builder, ActionSpecification> {

    protected Builder(Form form) {
      super(form);
    }

    protected Builder(Set<Form> forms) {
      super(forms);
    }

    public ActionSpecification build() {
      return new ActionSpecification(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends ActionSpecification>
          extends BehavioralSpecification.AbstractBuilder<S, T> {

    private final Set<Form> forms;

    protected AbstractBuilder(Form form) {
      this(new HashSet<>() {{ add(form);}});
    }

    protected AbstractBuilder(Set<Form> forms) {
      this.forms = forms;
    }

    public S addForm(Form form) {
      forms.add(form);
      return (S) this;
    }

    public S addForms(Set<Form> forms) {
      this.forms.addAll(forms);
      return (S) this;
    }

    public abstract T build();
  }
}
