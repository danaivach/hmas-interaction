package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class InputSpecification extends AbstractIOSpecification {

  private final Set<InputSpecification> inputs;

  protected InputSpecification(Builder builder) {
    super(builder);
    this.inputs = builder.inputs;
  }

  public Set<InputSpecification> getInputs() {
    return this.inputs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InputSpecification that = (InputSpecification) o;
    return super.equals(o) && Objects.equals(inputs, that.inputs);
  }

  public static class Builder extends AbstractIOSpecification.AbstractBuilder<Builder, InputSpecification> {

    protected final Set<InputSpecification> inputs;

    public Builder() {
      super();
      this.inputs = new HashSet<>();
    }

    public Builder setInput(InputSpecification shape) {
      this.inputs.add(shape);
      return this;
    }

    @Override
    public InputSpecification build() {
      return new InputSpecification(this);
    }

  }
}
