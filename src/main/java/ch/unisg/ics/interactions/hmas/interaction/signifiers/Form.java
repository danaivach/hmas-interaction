package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.*;

public class Form extends AbstractResource {

  private final String target;
  private final Set<String> operationTypes;
  private final Map<String, Object> additionalProperties = new HashMap<>();
  private final String contentType;
  private final Optional<String> subProtocol;
  private Optional<String> methodName;

  private Form(String href, Optional<String> methodName, String mediaType, Set<String> operationTypes,
               Optional<String> subProtocol, Builder builder) {
    super(HCTL.TERM.FORM, builder);
    this.methodName = methodName;
    this.target = href;
    this.contentType = mediaType;
    this.operationTypes = operationTypes;
    this.subProtocol = subProtocol;
  }

  private Form(String href, Optional<String> methodName, String mediaType, Set<String> operationTypes,
               Optional<String> subProtocol, Map<String, Object> additionalProperties, Builder builder) {
    this(href, methodName, mediaType, operationTypes, subProtocol, builder);
    this.additionalProperties.putAll(additionalProperties);
  }

  public Optional<String> getMethodName() {
    return methodName;
  }

  // Package-level access, used for setting affordance-specific default values after instantiation
  void setMethodName(String methodName) {
    this.methodName = Optional.of(methodName);
  }

  public Optional<String> getMethodName(String operationType) {

    return methodName;

  }

  public String getTarget() {
    return target;
  }

  public String getContentType() {
    return contentType;
  }

  public boolean hasOperationType(String type) {
    return operationTypes.contains(type);
  }

  public Set<String> getOperationTypes() {
    return operationTypes;
  }

  public Optional<String> getSubProtocol() {
    return subProtocol;
  }

  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  public boolean hasSubProtocol(String operationType, String subProtocol) {
    Optional<String> targetSubProtocol = getSubProtocol(operationType);
    return targetSubProtocol.isPresent() && subProtocol.equals(targetSubProtocol.get());
  }

  public Optional<String> getSubProtocol(String operationType) {
    if (!operationTypes.contains(operationType)) {
      throw new IllegalArgumentException("Unknown operation type: " + operationType);
    }

    return subProtocol;

  }

  // Package-level access, used for setting affordance-specific default values after instantiation
  void addOperationType(String operationType) {
    this.operationTypes.add(operationType);
  }

  public Optional<IRI> getIRI() {
    return getIRIAsString()
            .map(s -> s.replace("<", ""))
            .map(s -> s.replace(">", ""))
            .map(SimpleValueFactory.getInstance()::createIRI);
  }

  public static class Builder extends AbstractResource.AbstractBuilder<Builder, Form> {
    private final String target;
    private final Set<String> operationTypes;
    private final Map<String, Object> additionalProperties;
    private String contentType;
    private Optional<String> subProtocol;
    private Optional<String> methodName;

    public Builder(String target) {
      super(HCTL.TERM.FORM);
      this.target = target;
      this.contentType = "application/json";
      this.operationTypes = new HashSet<>();
      this.subProtocol = Optional.empty();
      this.additionalProperties = new HashMap<>();
      this.methodName = Optional.empty();
    }

    public Builder addOperationType(String operationType) {
      this.operationTypes.add(operationType);
      return this;
    }

    public Builder addOperationTypes(Set<String> operationTypes) {
      this.operationTypes.addAll(operationTypes);
      return this;
    }

    public Builder setMethodName(String methodName) {
      this.methodName = Optional.of(methodName);
      return this;
    }

    public Builder setContentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder addSubProtocol(String subProtocol) {
      this.subProtocol = Optional.of(subProtocol);
      return this;
    }

    public Builder addProperty(String key, Object value) {
      this.additionalProperties.put(key, value);
      return this;
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public Form build() {
      return new Form(this.target, this.methodName, this.contentType, this.operationTypes,
              this.subProtocol, this.additionalProperties, this);
    }

  }

}
