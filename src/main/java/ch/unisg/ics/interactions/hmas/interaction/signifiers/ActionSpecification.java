package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ActionSpecification extends AbstractResource {
  private Set<Form> forms;
  private Optional<InputSpecification> input;
  private Set<String> requiredSemanticTypes;

  protected ActionSpecification(ActionSpecification.Builder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
    this.input = builder.input;
    this.forms = ImmutableSet.copyOf(builder.forms);
    this.requiredSemanticTypes = ImmutableSet.copyOf(builder.requiredSemanticTypes);
  }

  public Set<Form> getForms() {
    return this.forms;
  }

  public Set<String> getRequiredSemanticTypes() {
    return this.requiredSemanticTypes.stream()
        .filter(type -> !type.equals(INTERACTION.TERM.ACTION_EXECUTION.toString()))
        .collect(ImmutableSet.toImmutableSet());
  }

  public Optional<InputSpecification> getInputSpecification() {
    return this.input;
  }

  public static class Builder extends AbstractResource.AbstractBuilder<Builder, ActionSpecification> {
    private Set<Form> forms;
    private Optional<InputSpecification> input;
    private Set<String> requiredSemanticTypes;

    public Builder(Form form) {
      super(INTERACTION.TERM.ACTION_SPECIFICATION);
      this.forms = Set.of(form);
      input = Optional.empty();
      requiredSemanticTypes = new HashSet<>();
    }

    public Builder(Set<Form> forms) {
      super(INTERACTION.TERM.ACTION_SPECIFICATION);
      this.forms = forms;
      input = Optional.empty();
      requiredSemanticTypes = new HashSet<>();
    }

    public Builder setRequiredInput(InputSpecification input) {
      this.input = Optional.of(input);
      return this;
    }

    public Builder setRequiredSemanticTypes(Set<String> requiredSemanticTypes) {
      this.requiredSemanticTypes = requiredSemanticTypes;
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
