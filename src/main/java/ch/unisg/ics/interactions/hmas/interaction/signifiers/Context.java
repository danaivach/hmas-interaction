package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;

import java.util.Set;

public class Context extends AbstractResource {
  private final Model model;

  protected Context(Builder builder) {
    super(SHACL.TERM.NODE_SHAPE, builder);
    this.model = builder.model;
  }

  public Model getModel() {
    return this.model;
  }

  public static class Builder extends AbstractBuilder<Builder, Context> {
    protected Model model;
    private final ModelBuilder modelBuilder;

    public Builder() {
      super(SHACL.TERM.NODE_SHAPE);
      this.modelBuilder = new ModelBuilder();
      this.model = modelBuilder.build();
    }

    public Builder addStatement(Statement statement) {
      this.modelBuilder.add(statement.getSubject(), statement.getPredicate(), statement.getObject());
      this.model = this.modelBuilder.build();
      return this;
    }

    public Builder addModel(Model model) {
      for (Statement statement : model) {
        addStatement(statement);
      }

      return this;
    }

    public Context build() {
      return new Context(this);
    }
  }
}
