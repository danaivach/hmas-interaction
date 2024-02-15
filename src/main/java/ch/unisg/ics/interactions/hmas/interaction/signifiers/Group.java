package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;

import java.util.Optional;

public class Group extends AbstractResource {
  private Optional<String> label;
  private Optional<String> comment;
  private Optional<Integer> order;

  protected Group(HMAS type, Group.Builder builder) {
    super(type, builder);
    this.label = builder.label;
    this.comment = builder.comment;
    this.order = builder.order;
  }

  public Optional<String> getLabel() {
    return label;
  }

  public Optional<String> getComment() {
    return comment;
  }

  public Optional<Integer> getOrder() {
    return order;
  }

  public static class Builder extends AbstractBuilder {
    private Optional<String> label;
    private Optional<String> comment;
    private Optional<Integer> order;

    public Builder() {
      super(SHACL.TERM.PROPERTY_GROUP);
      this.label = Optional.empty();
      this.comment = Optional.empty();
      this.order = Optional.empty();
    }

    public Builder setLabel(String label) {
      this.label = Optional.of(label);
      return this;
    }

    public Builder setComment(String comment) {
      this.comment = Optional.of(comment);
      return this;
    }

    public Builder setOrder(int order) {
      this.order = Optional.of(order);
      return this;
    }

    @Override
    public Group build() {
      return new Group(this.TYPE, this);
    }
  }
}
