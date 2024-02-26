package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.shapes.IOSpecification;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ActionSpecification extends AbstractResource {
  private final Set<Form> forms;
  private final Optional<IOSpecification> input;
  private final Optional<IOSpecification> output;
  private final Set<String> requiredSemanticTypes;

  protected ActionSpecification(ActionSpecification.Builder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
    this.input = builder.input;
    this.output = builder.output;
    this.forms = ImmutableSet.copyOf(builder.forms);
    this.requiredSemanticTypes = ImmutableSet.copyOf(builder.requiredSemanticTypes);
  }

  public Set<Form> getForms() {
    return this.forms;
  }

  public Form getFirstForm() {
    return this.forms.iterator().next();
  }

  public Set<String> getRequiredSemanticTypes() {
    return this.requiredSemanticTypes.stream()
            .filter(type -> !type.equals(INTERACTION.TERM.ACTION_EXECUTION.toString()))
            .collect(ImmutableSet.toImmutableSet());
  }

  public Optional<IOSpecification> getInputSpecification() {
    return this.input;
  }

  public Optional<IOSpecification> getOutputSpecification() {
    return this.output;
  }

  public static class Builder extends AbstractResource.AbstractBuilder<Builder, ActionSpecification> {
    private final Set<Form> forms;
    private Optional<IOSpecification> input;
    private Optional<IOSpecification> output;
    private Set<String> requiredSemanticTypes;

    public Builder(Form form) {
      this(Set.of(form));
    }

    public Builder(Set<Form> forms) {
      super(INTERACTION.TERM.ACTION_SPECIFICATION);
      this.forms = forms;
      this.input = Optional.empty();
      this.output = Optional.empty();
      this.requiredSemanticTypes = new HashSet<>();
    }

    public Builder setInputSpecification(IOSpecification input) {
      this.input = Optional.of(input);
      return this;
    }

    public Builder setOutputSpecification(IOSpecification output) {
      this.output = Optional.of(output);
      return this;
    }

    public Builder setRequiredSemanticTypes(Set<String> requiredSemanticTypes) {
      this.requiredSemanticTypes = requiredSemanticTypes;
      return this;
    }

    public ActionSpecification build() {
      return new ActionSpecification(this);
    }
  }

}
