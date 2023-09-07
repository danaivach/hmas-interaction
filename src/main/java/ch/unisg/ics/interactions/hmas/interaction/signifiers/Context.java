package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;

import java.util.HashSet;
import java.util.Set;

public class Context extends AbstractResource {
  private final String targetClass;
  private final Set<Input> inputs;

  public String getTargetClass() {
    return this.targetClass;
  }

  public Set<Input> getInputs() {
    return this.inputs;
  }

  protected Context(Builder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
    this.targetClass = builder.targetClass;
    this.inputs = builder.inputs;
  }

  public static class Builder extends AbstractBuilder<Builder, Context> {
    protected String targetClass;
    protected Set<Input> inputs;

    public Builder() {
      super(SHACL.TERM.NODE_SHAPE);
      this.targetClass = "";
      this.inputs = new HashSet<>();
    }

    public Builder withInput(Input input) {
      this.inputs.add(input);
      return this;
    }

    public Builder withTargetClass(String targetClass) {
      this.targetClass = targetClass;
      return this;
    }

    public Context build() {
      return new Context(this);
    }
  }
}
