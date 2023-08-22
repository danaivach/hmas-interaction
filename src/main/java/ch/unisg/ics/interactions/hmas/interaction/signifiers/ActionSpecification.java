package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import com.google.common.collect.ImmutableSet;
import org.eclipse.rdf4j.model.IRI;

import java.util.HashSet;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.ACTION_EXECUTION;

public class ActionSpecification extends AbstractResource {
  private Set<Form> forms;
  private final IRI clazz = ACTION_EXECUTION;

  protected ActionSpecification(ActionSpecification.AbstractBuilder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
    this.forms = ImmutableSet.copyOf(builder.forms);
  }

  public Set<Form> getForms() {
    return this.forms;
  }

  public static class Builder extends ActionSpecification.AbstractBuilder<Builder, ActionSpecification> {
    private final ActionSpecification actionSpecification;

    public Builder(Form form) {
      super(form);
      this.actionSpecification = new ActionSpecification(this);
    }

    public Builder(Set<Form> forms) {
      super(forms);
      this.actionSpecification = new ActionSpecification(this);
    }

    public ActionSpecification build() {
      return this.actionSpecification;
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends ActionSpecification>
          extends AbstractResource.AbstractBuilder<S, T> {

    private final Set<Form> forms;

    protected AbstractBuilder(Form form) {
      this(new HashSet<Form>() {{
        add(form);
      }});
    }

    protected AbstractBuilder(Set<Form> forms) {
      super(SHACL.TERM.NODE_SHAPE);
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
