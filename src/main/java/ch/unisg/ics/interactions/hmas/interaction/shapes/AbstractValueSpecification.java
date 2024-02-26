package ch.unisg.ics.interactions.hmas.interaction.shapes;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.Optional;
import java.util.Set;


public class AbstractValueSpecification extends AbstractIOSpecification {

  private final Boolean isRequired;
  private final Optional<String> name;
  private final Optional<String> description;
  private final Optional<Group> group;
  private final Optional<Integer> order;

  protected AbstractValueSpecification(AbstractBuilder builder) {
    super(builder);
    this.isRequired = builder.isRequired;
    this.name = builder.name;
    this.description = builder.description;
    this.group = builder.group;
    this.order = builder.order;
  }

  public Boolean isRequired() {
    return isRequired;
  }

  public Optional<String> getName() {
    return this.name;
  }

  public Optional<String> getDescription() {
    return this.description;
  }

  public Optional<Group> getGroup() {
    return this.group;
  }

  public Optional<Integer> getOrder() {
    return this.order;
  }


  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends AbstractValueSpecification>
          extends AbstractIOSpecification.AbstractBuilder<S, T> {

    protected Boolean isRequired;
    protected Optional<String> name;
    protected Optional<String> description;
    protected Optional<Group> group;
    protected Optional<Integer> order;

    public AbstractBuilder(String dataType) {
      this(Set.of(dataType));
    }

    public AbstractBuilder(Set<String> dataTypes) {
      super();
      for (String dataType : dataTypes) {
        validateIRI(dataType);
      }
      addRequiredSemanticTypes(dataTypes);
      this.isRequired = false;
      this.name = Optional.empty();
      this.description = Optional.empty();
      this.group = Optional.empty();
      this.order = Optional.empty();
    }

    protected static boolean validateIRI(String IRI) {
      try {
        SimpleValueFactory.getInstance().createIRI(IRI);
        return true;
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("IRIs of DataTypes and node values must be valid.");
      }
    }

    protected static boolean validateNewValue(Object value, Object defaultValue) {
      if (!defaultValue.equals(value)) {
        String valueStr = value.toString();
        String defaultValueStr = defaultValue.toString();
        throw new ValueConflictException(valueStr, defaultValueStr);
      }
      return true;
    }

    protected static boolean validateNewDefaultValue(Object value, Object defaultValue) {
      if (!value.equals(defaultValue)) {
        String valueStr = value.toString();
        String defaultValueStr = defaultValue.toString();
        throw new ValueConflictException(valueStr, defaultValueStr);
      }
      return true;
    }

    public S setRequired(Boolean required) {
      this.isRequired = required;
      return (S) this;
    }

    public S setName(String name) {
      this.name = Optional.of(name);
      return (S) this;
    }

    public S setDescription(String description) {
      this.description = Optional.of(description);
      return (S) this;
    }

    public S setGroup(Group group) {
      this.group = Optional.of(group);
      return (S) this;
    }

    public S setOrder(Integer order) {
      this.order = Optional.of(order);
      return (S) this;
    }

    @Override
    public abstract T build();

  }
}
