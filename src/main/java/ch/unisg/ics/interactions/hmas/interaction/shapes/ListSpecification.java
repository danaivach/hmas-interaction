package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSpecification extends ValueSpecification {

  private final List<IOSpecification> memberSpecifications;

  protected ListSpecification(Builder builder) {
    super(builder);
    this.memberSpecifications = builder.memberSpecifications;
  }

  public static class Builder extends ValueSpecification.AbstractBuilder<Builder, ListSpecification> {

    protected final List<IOSpecification> memberSpecifications;

    public Builder() {
      super(RDF.LIST.stringValue());
      this.memberSpecifications = new ArrayList<>();
    }

    @Override
    protected Builder getBuilder() { return this; }

    @Override
    public ListSpecification build() { return new ListSpecification(this); }

  }

}
