package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import com.google.common.collect.ImmutableSet;
import org.eclipse.rdf4j.model.IRI;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.ACTION_EXECUTION;

public class ActionSpecification extends AbstractResource {
  private Set<Form> forms;
  private Optional<Input> input = Optional.empty();

  protected ActionSpecification(ActionSpecification.Builder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
    this.input = builder.input;
    this.forms = ImmutableSet.copyOf(builder.forms);
  }

  public Set<Form> getForms() {
    return this.forms;
  }

  public Optional<Input> getInput() {
    return this.input;
  }

  public static class Builder extends AbstractResource.AbstractBuilder<Builder, ActionSpecification> {
    private Set<Form> forms;
    private Optional<Input> input;

    public Builder(Form form) {
      super(INTERACTION.TERM.ACTION_SPECIFICATION);
      this.forms = Set.of(form);
      input = Optional.empty();
    }

    public Builder(Set<Form> forms) {
      super(INTERACTION.TERM.ACTION_SPECIFICATION);
      this.forms = forms;
      input = Optional.empty();
    }

    public Builder withInput(Input input) {
      this.input = Optional.of(input);
      return this;
    }

    public Builder setIRIAsString(String IRI) {
      this.IRI = Optional.of(IRI);
      return this;
    }

    public ActionSpecification build() {
      return new ActionSpecification(this);
    }
  }

}
