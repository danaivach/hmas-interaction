package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OutputSpecification extends AbstractIOSpecification {

  private final Set<OutputSpecification> outputs;

  protected OutputSpecification(Builder builder) {
    super(builder);
    this.outputs = builder.outputs;
  }

  public Set<OutputSpecification> getOutputs() {
    return this.outputs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OutputSpecification that = (OutputSpecification) o;
    return super.equals(o) && Objects.equals(outputs, that.outputs);
  }

  public static class Builder extends AbstractIOSpecification.AbstractBuilder<Builder, OutputSpecification> {

    protected final Set<OutputSpecification> outputs;

    public Builder() {
      super();
      this.outputs = new HashSet<>();
    }

    public Builder setOutput(OutputSpecification shape) {
      this.outputs.add(shape);
      return this;
    }

    @Override
    public OutputSpecification build() {
      return new OutputSpecification(this);
    }

  }
}
