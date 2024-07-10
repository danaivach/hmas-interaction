package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.*;

public class ListSpecification extends ValueSpecification {

  private final List<IOSpecification> memberSpecifications;

  protected ListSpecification(Builder builder) {
    super(builder);
    this.memberSpecifications = Collections.unmodifiableList(builder.memberSpecifications);
  }

  public List<IOSpecification> getMemberSpecifications() {
    return this.memberSpecifications;
  }

  public static class Builder extends ValueSpecification.AbstractBuilder<Builder, ListSpecification> {

    protected final List<IOSpecification> memberSpecifications;

    public Builder() {
      super(RDF.LIST.stringValue());
      this.memberSpecifications = new ArrayList<>();
    }

    public Builder addMemberSpecification(IOSpecification specification) {
      this.memberSpecifications.add(specification);
      return this;
    }

    public Builder addMemberSpecifications(List<IOSpecification> specifications) {
      this.memberSpecifications.addAll(specifications);
      return this;
    }

    @Override
    protected Builder getBuilder() { return this; }

    @Override
    public ListSpecification build() { return new ListSpecification(this); }

  }

}
