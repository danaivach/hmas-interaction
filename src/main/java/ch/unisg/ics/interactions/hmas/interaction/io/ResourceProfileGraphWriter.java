package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Agent;
import ch.unisg.ics.interactions.hmas.core.io.BaseResourceProfileGraphWriter;
import ch.unisg.ics.interactions.hmas.core.io.InvalidResourceProfileException;
import ch.unisg.ics.interactions.hmas.interaction.shapes.*;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.*;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;

import java.util.*;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.*;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL.*;
import static org.eclipse.rdf4j.model.util.Values.*;

public class ResourceProfileGraphWriter extends BaseResourceProfileGraphWriter<ResourceProfile> {
  private final Map<Resource, Set<AbstractIOSpecification>> addedContexts = new HashMap<>();
  private final Map<Resource, AbstractIOSpecification> addedIOShapes = new HashMap<>();

  public ResourceProfileGraphWriter(ResourceProfile profile) {
    super(profile);
  }

  @Override
  public String write() {
    this.setNamespace(INTERACTION.PREFIX, INTERACTION.NAMESPACE)
            .setNamespace(HCTL.PREFIX, HCTL.NAMESPACE)
            .setNamespace(HTV.PREFIX, HTV.NAMESPACE)
            .setNamespace(SHACL.PREFIX, SHACL.NAMESPACE)
            .setNamespace(PROV.PREFIX, PROV.NAMESPACE)
            .setNamespace(RDFS.PREFIX, RDFS.NAMESPACE)
            .setNamespace("xs", "http://www.w3.org/2001/XMLSchema#")
            .addSignifiers();

    return super.write();
  }

  @Override
  public ResourceProfileGraphWriter setNamespace(String prefix, String namespace) {
    this.graphBuilder.setNamespace(prefix, namespace);
    return this;
  }

  @Override
  protected BaseResourceProfileGraphWriter addAgent(Agent agent, Resource node) {
    addCapableAgent((CapableAgent) agent, node);
    return this;
  }

  protected BaseResourceProfileGraphWriter addCapableAgent(CapableAgent agent, Resource node) {
    Set<Ability> abilities = agent.getAbilities();

    for (Ability ability : abilities) {
      Resource abilityNode = resolveHostableLocation(ability);
      graphBuilder.add(node, HAS_ABILITY, abilityNode);
      addResource(ability, abilityNode);
    }
    addHostable(agent, node);
    return this;
  }

  private void addSignifiers() {

    Set<Signifier> signifiers = profile.getExposedSignifiers();

    for (Signifier signifier : signifiers) {
      Resource locatedSignifier = resolveHostableLocation(signifier);

      graphBuilder.add(profileIRI, EXPOSES_SIGNIFIER, locatedSignifier);
      graphBuilder.add(locatedSignifier, RDF.TYPE, SIGNIFIER);
      addResource(signifier, locatedSignifier);

      signifier.getLabel().ifPresent(label -> graphBuilder.add(locatedSignifier, RDFS.LABEL, label));
      signifier.getComment().ifPresent(comment -> graphBuilder.add(locatedSignifier, RDFS.COMMENT, comment));

      Set<Ability> abilities = signifier.getRecommendedAbilities();
      addRecommendedAbilities(locatedSignifier, abilities);

      addRecommendedContexts(locatedSignifier, signifier.getRecommendedContexts());

      ActionSpecification bSpec = signifier.getActionSpecification();
      addActionSpecification(locatedSignifier, bSpec);
    }
  }

  private void addRecommendedAbilities(Resource signifier, Set<Ability> abilities) {
    for (Ability ability : abilities) {
      Resource abilityId = rdf.createBNode();

      graphBuilder.add(signifier, RECOMMENDS_ABILITY, abilityId);
      // graphBuilder.add(abilityId, RDF.TYPE, ABILITY);

      for (String type : ability.getSemanticTypes()) {
        graphBuilder.add(abilityId, RDF.TYPE, rdf.createIRI(type));
      }
    }
  }

  private void addRecommendedContexts(Resource signifier, Set<Context> contexts) {
    contexts.forEach(context -> {
      Resource locatedContext = resolveHostableLocation(context);
      if (!addedContexts.containsKey(locatedContext)) {
        addedContexts.put(locatedContext, new HashSet<>());
      }
      graphBuilder.add(signifier, RECOMMENDS_CONTEXT, locatedContext);
      Model contextModel = context.getModel();
      for (Statement st : contextModel) {
        graphBuilder.add(st.getSubject(), st.getPredicate(), st.getObject());
      }
    });
  }

  private void addActionSpecification(Resource signifier, ActionSpecification specification) {
    Resource locatedAcSpec = resolveHostableLocation(specification);

    graphBuilder.add(signifier, SIGNIFIES, locatedAcSpec);
    graphBuilder.add(locatedAcSpec, RDF.TYPE, SHACL.NODE_SHAPE);
    graphBuilder.add(locatedAcSpec, SHACL.CLASS, ACTION_EXECUTION);
    specification.getRequiredSemanticTypes()
            .forEach(type -> graphBuilder.add(locatedAcSpec, SHACL.CLASS, rdf.createIRI(type)));

    specification.getSemanticTypes().forEach(type -> {
      graphBuilder.add(locatedAcSpec, RDF.TYPE, rdf.createIRI(type));
    });

    Resource propertyId = addPropertyNode(locatedAcSpec);

    Set<Form> forms = specification.getForms();

    List<Resource> formNodes = new ArrayList<>();
    for (Form form : forms) {
      Resource formNode = resolveHostableLocation(form);
      createFormNode(form, formNode);
      formNodes.add(formNode);
    }
    if (forms.size() == 1) {
      graphBuilder.add(propertyId, HAS_VALUE, formNodes.get(0));
    } else {
      addFormsList(propertyId, formNodes);
    }

    specification.getInputSpecification().ifPresent(input -> {
      Resource inputNode = addAbstractIOSpecification((AbstractIOSpecification) input);
      graphBuilder.add(inputNode, SHACL.PATH, INTERACTION.HAS_INPUT);
      graphBuilder.add(locatedAcSpec, SHACL.PROPERTY, inputNode);
    });

    specification.getOutputSpecification().ifPresent(output -> {
      Resource outputNode = addAbstractIOSpecification((AbstractIOSpecification) output);
      graphBuilder.add(outputNode, SHACL.PATH, INTERACTION.HAS_OUTPUT);
      graphBuilder.add(locatedAcSpec, SHACL.PROPERTY, outputNode);
    });
  }

  protected Resource addAbstractIOSpecification(AbstractIOSpecification specification) {
    Resource node;

    if (specification instanceof AbstractValueSpecification) {
      if (specification instanceof ListSpecification && ((ListSpecification) specification)
              .getMemberSpecifications().size() > 0) {
        node = rdf.createBNode();
        addQualifiedValueSpecification(getListSpecification((ListSpecification) specification), node);
      } else {
        node = resolveHostableLocation(specification);
        addAbstractValueSpecification((AbstractValueSpecification) specification, node);
      }
    } else if (specification instanceof QualifiedValueSpecification) {
      node = rdf.createBNode();
      addQualifiedValueSpecification((QualifiedValueSpecification) specification, node);
    } else throw new InvalidResourceProfileException("Unrecognized shape of input/output specification.");
    return node;
  }

  private void addAbstractValueSpecification(AbstractValueSpecification specification, Resource node) {
    if (specification.getRequiredSemanticTypes().contains(XSD.BOOLEAN.stringValue())) {
      addBooleanSpecification((BooleanSpecification) specification, node);
    } else if (specification.getRequiredSemanticTypes().contains(XSD.DOUBLE.stringValue())) {
      addDoubleSpecification((DoubleSpecification) specification, node);
    } else if (specification.getRequiredSemanticTypes().contains(XSD.FLOAT.stringValue())) {
      addFloatSpecification((FloatSpecification) specification, node);
    } else if (specification.getRequiredSemanticTypes().contains(XSD.INT.stringValue()) ||
            specification.getRequiredSemanticTypes().contains(XSD.INTEGER.stringValue())) {
      addIntegerSpecification((IntegerSpecification) specification, node);
    } else if (specification.getRequiredSemanticTypes().contains(XSD.STRING.stringValue())) {
      addStringSpecification((StringSpecification) specification, node);
    } else if (specification.getRequiredSemanticTypes().contains(RDF.LIST.stringValue())) {
      addSimpleListSpecification((ListSpecification) specification, node);
    } else if (specification instanceof ValueSpecification) {
      addValueSpecification((ValueSpecification) specification, node);
    }

    specification.getRequiredSemanticTypes().forEach(type -> {
      if (!(specification.getRequiredSemanticTypes().size() > 1 && XSD.ANYURI.stringValue().equals(type))) {
        if (type.startsWith(XSD.NAMESPACE)) {
          this.graphBuilder.add(node, DATATYPE, iri(type));
        } else {
          this.graphBuilder.add(node, CLASS, iri(type));
        }
      }
    });
    specification.getName().ifPresent(present -> this.graphBuilder.add(node, NAME, present));
    specification.getDescription().ifPresent(present -> this.graphBuilder.add(node, DESCRIPTION, present));
    specification.getOrder().ifPresent(present -> this.graphBuilder.add(node, ORDER, present));

    if (specification.isRequired()) {
      this.graphBuilder.add(node, MIN_COUNT, 1);
    }
    this.graphBuilder.add(node, MAX_COUNT, 1);
  }

  private void addBooleanSpecification(BooleanSpecification specification, Resource node) {
    specification.getValue().ifPresent(present -> this.graphBuilder.add(node, HAS_VALUE, literal(present)));
    specification.getDefaultValue().ifPresent(present -> this.graphBuilder.add(node, DEFAULT_VALUE, literal(present)));
    addAbstractIOSpecification(specification, node);
  }

  private void addDoubleSpecification(DoubleSpecification specification, Resource node) {
    specification.getValue().ifPresent(present -> this.graphBuilder.add(node, HAS_VALUE, literal(present)));
    specification.getDefaultValue().ifPresent(present -> this.graphBuilder.add(node, DEFAULT_VALUE, literal(present)));
    addAbstractIOSpecification(specification, node);
  }

  private void addFloatSpecification(FloatSpecification specification, Resource node) {
    specification.getValue().ifPresent(present -> this.graphBuilder.add(node, HAS_VALUE, literal(present)));
    specification.getDefaultValue().ifPresent(present -> this.graphBuilder.add(node, DEFAULT_VALUE, literal(present)));
    addAbstractIOSpecification(specification, node);
  }

  private void addIntegerSpecification(IntegerSpecification specification, Resource node) {
    specification.getValue().ifPresent(present -> this.graphBuilder.add(node, HAS_VALUE, literal(present)));
    specification.getDefaultValue().ifPresent(present -> this.graphBuilder.add(node, DEFAULT_VALUE, literal(present)));
    addAbstractIOSpecification(specification, node);
  }

  private void addStringSpecification(StringSpecification specification, Resource node) {
    specification.getValue().ifPresent(present -> this.graphBuilder.add(node, HAS_VALUE, literal(present)));
    specification.getDefaultValue().ifPresent(present -> this.graphBuilder.add(node, DEFAULT_VALUE, literal(present)));
    addAbstractIOSpecification(specification, node);
  }

  private void addSimpleListSpecification(ListSpecification specification, Resource node) {
    specification.getValue().ifPresent(present -> this.graphBuilder.add(node, HAS_VALUE, present));
    specification.getDefaultValue().ifPresent(present -> this.graphBuilder.add(node, DEFAULT_VALUE, present));
    addAbstractIOSpecification(specification, node);
  }

  private QualifiedValueSpecification getListSpecification(ListSpecification specification) {
    List<IOSpecification> memberSpecifications = specification.getMemberSpecifications();

    // Create the specification of the RDF nil value as a Value Specification
    ValueSpecification nilSpecification = new ValueSpecification.Builder()
            .setValue(RDF.NIL)
            .setRequired(true)
            .build();

    // Create the last sublist specification
    QualifiedValueSpecification subListSpecification =
            prepareSublistSpecificationBuilder(specification,
                    (AbstractIOSpecification) memberSpecifications.get(memberSpecifications.size() - 1),
                    nilSpecification)
                    .build();

    // Recursively create the specification of sublists as Qualified Value Specifications
    for (int i = memberSpecifications.size() - 2; i >= 0; i--) {
      QualifiedValueSpecification.Builder subListSpecificationBuilder =
              prepareSublistSpecificationBuilder(specification, (AbstractIOSpecification) memberSpecifications.get(i),
                      subListSpecification);

      if (i == 0 && specification.getIRI().isPresent()) {
        subListSpecificationBuilder.setIRI(specification.getIRI().get());

      }

      subListSpecification = subListSpecificationBuilder.build();
    }

    return subListSpecification;
  }

  private void addValueSpecification(ValueSpecification specification, Resource node) {
    specification.getValue().ifPresent(present -> this.graphBuilder.add(node, HAS_VALUE, present));
    specification.getDefaultValue().ifPresent(present -> this.graphBuilder.add(node, DEFAULT_VALUE, present));
    addAbstractIOSpecification(specification, node);
  }

  private void addQualifiedValueSpecification(QualifiedValueSpecification specification, Resource node) {
    Resource valueNode;

    if (specification.getIRI().isPresent() && addedIOShapes.containsKey(specification.getIRI().get())) {
      valueNode = specification.getIRI().get();
      prepareQualifiedValueSpecification(specification, node, valueNode);
      return;
    } else {
      valueNode = resolveHostableLocation(specification);
    }

    prepareQualifiedValueSpecification(specification, node, valueNode);

    Map<String, IOSpecification> properties = specification.getPropertySpecifications();
    for (String propertyType : specification.getPropertySpecifications().keySet()) {
      IOSpecification propertySpecification = properties.get(propertyType);
      Resource propertyNode = addAbstractIOSpecification((AbstractIOSpecification) propertySpecification);
      this.graphBuilder.add(propertyNode, SHACL.PATH, iri(propertyType));
      this.graphBuilder.add(valueNode, SHACL.PROPERTY, propertyNode);
    }

    addAbstractIOSpecification(specification, valueNode);
  }

  private void addAbstractIOSpecification(AbstractIOSpecification specification, Resource node) {
    addedIOShapes.put(node, specification);
    addResource(specification, node);
  }

  private void prepareQualifiedValueSpecification(AbstractIOSpecification specification, Resource node, Resource valueNode) {

    this.graphBuilder.add(node, QUALIFIED_VALUE_SHAPE, valueNode);
    this.graphBuilder.add(valueNode, RDF.TYPE, SHAPE);

    if (specification.isRequired()) {
      this.graphBuilder.add(node, QUALIFIED_MIN_COUNT, 1);
    }
    this.graphBuilder.add(node, QUALIFIED_MAX_COUNT, 1);

    specification.getRequiredSemanticTypes().forEach(type -> graphBuilder.add(valueNode, CLASS, iri(type)));
  }

  private QualifiedValueSpecification.Builder prepareSublistSpecificationBuilder(ListSpecification specification,
                                                                                 AbstractIOSpecification firstMember,
                                                                                 AbstractIOSpecification restMember) {
    return new QualifiedValueSpecification.Builder()
            .addSemanticTypes(specification.getSemanticTypes())
            .addRequiredSemanticTypes(specification.getRequiredSemanticTypes())
            .setRequired(specification.isRequired())
            .addPropertySpecification(RDF.FIRST.stringValue(), firstMember)
            .addPropertySpecification(RDF.REST.stringValue(), restMember);
  }

  private void addGroup(Group group, Resource propertyId) {
    Resource groupNode = resolveHostableLocation(group);
    graphBuilder.add(propertyId, SHACL.GROUP, groupNode);
    graphBuilder.add(groupNode, RDF.TYPE, group.getType().toIRI());
    group.getOrder().ifPresent(order -> graphBuilder.add(groupNode, SHACL.ORDER, literal(order)));
    group.getLabel().ifPresent(label -> graphBuilder.add(groupNode, RDFS.LABEL, literal(label)));
  }

  private void addFormsList(Resource propertyId, List<Resource> formNodes) {
    Resource collectionNode = createFormsCollection(formNodes);
    graphBuilder.add(propertyId, SHACL.OR, collectionNode);
  }

  private Resource createFormsCollection(List<Resource> formNodes) {
    if (formNodes.isEmpty()) {
      return RDF.NIL;
    }

    BNode node = bnode();
    BNode valueNode = bnode();

    graphBuilder.add(node, RDF.FIRST, valueNode);
    graphBuilder.add(valueNode, HAS_VALUE, formNodes.get(0));
    graphBuilder.add(node, RDF.REST, createFormsCollection(formNodes.subList(1, formNodes.size())));

    return node;
  }

  private Resource addPropertyNode(Resource acSpec) {
    Resource propertyId = rdf.createBNode();

    graphBuilder.add(acSpec, SHACL.PROPERTY, propertyId);
    graphBuilder.add(propertyId, SHACL.PATH, PROV.USED);
    graphBuilder.add(propertyId, SHACL.MIN_COUNT, literal(1));
    graphBuilder.add(propertyId, SHACL.MAX_COUNT, literal(1));

    return propertyId;
  }

  private void createFormNode(Form form, Resource formNode) {
    addResource(form, formNode);
    graphBuilder.add(formNode, HCTL.HAS_TARGET, iri(form.getTarget()));
    form.getMethodName().ifPresent(m -> graphBuilder.add(formNode, HTV.METHOD_NAME, literal(m)));
    graphBuilder.add(formNode, HCTL.FOR_CONTENT_TYPE, literal(form.getContentType()));
  }
}
