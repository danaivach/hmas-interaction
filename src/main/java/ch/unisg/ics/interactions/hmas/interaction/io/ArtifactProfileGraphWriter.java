package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphWriter;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.*;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.*;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.*;
import static org.eclipse.rdf4j.model.util.Values.*;

public class ArtifactProfileGraphWriter extends ResourceProfileGraphWriter<ArtifactProfile> {
  private final Map<Resource, Set<InputSpecification>> addedContexts = new HashMap<>();

  public ArtifactProfileGraphWriter(ArtifactProfile profile) {
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
            .setNamespace("urn", "http://example.org/urn#")
            .setNamespace("ex", "http://example.org/")
            .setNamespace("xs", "http://www.w3.org/2001/XMLSchema#")
            .addSignifiers();

    return super.write();
  }

  @Override
  public ArtifactProfileGraphWriter setNamespace(String prefix, String namespace) {
    this.graphBuilder.setNamespace(prefix, namespace);
    return this;
  }

  private void addSignifiers() {

    Set<Signifier> signifiers = profile.getExposedSignifiers();

    for (Signifier signifier : signifiers) {
      Resource locatedSignifier = resolveHostableLocation(signifier);

      graphBuilder.add(profileIRI, EXPOSES_SIGNIFIER, locatedSignifier);
      graphBuilder.add(locatedSignifier, RDF.TYPE, SIGNIFIER);

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
        .forEach(type -> graphBuilder.add(locatedAcSpec, SHACL.CLASS, ACTION_EXECUTION));

    Resource propertyId = addPropertyNode(locatedAcSpec);

    Set<Form> forms = specification.getForms();

    if (forms.size() == 1) {
      Form form = forms.iterator().next();
      createFormNode(form);
      graphBuilder.add(propertyId, SHACL.HAS_VALUE, form.getIRI().get());
    }
    if (forms.size() > 1) {
      addFormsList(propertyId, forms.stream().map(f -> f.getIRI().get()).toList());
      forms.forEach(this::createFormNode);
    }

    specification.getInputSpecification().ifPresent(input -> {
      Resource inputNode = addInput(input);
      graphBuilder.add(inputNode, SHACL.PATH, INTERACTION.HAS_INPUT);
      graphBuilder.add(locatedAcSpec, SHACL.PROPERTY, inputNode);
    });
  }

  private Resource addInput(InputSpecification input) {
    Resource inputId = rdf.createBNode();
    input.getQualifiedValueShape().ifPresent(shape -> graphBuilder.add(inputId, SHACL.QUALIFIED_VALUE_SHAPE, iri(shape)));
    input.getName().ifPresent(name -> graphBuilder.add(inputId, SHACL.NAME, literal(name)));
    input.getDescription().ifPresent(desc -> graphBuilder.add(inputId, SHACL.DESCRIPTION, literal(desc)));
    input.getMinCount().ifPresent(c -> graphBuilder.add(inputId, SHACL.MIN_COUNT, literal(c)));
    input.getMaxCount().ifPresent(c -> graphBuilder.add(inputId, SHACL.MAX_COUNT, literal(c)));
    input.getQualifiedMinCount().ifPresent(c -> graphBuilder.add(inputId, SHACL.QUALIFIED_MIN_COUNT, literal(c)));
    input.getQualifiedMaxCount().ifPresent(c -> graphBuilder.add(inputId, SHACL.QUALIFIED_MAX_COUNT, literal(c)));
    input.getRequiredDataType().ifPresent(c -> graphBuilder.add(inputId, SHACL.DATATYPE, iri(c)));
    input.getRequiredProperties().ifPresent(path -> graphBuilder.add(inputId, SHACL.PATH, iri(path)));
    input.getOrder().ifPresent(order -> graphBuilder.add(inputId, SHACL.ORDER, literal(order)));
    input.getGroup().ifPresent(g -> addGroup(g, inputId));
    input.getMinInclusive().ifPresent(m -> graphBuilder.add(inputId, SHACL.MIN_INCLUSIVE, literal(m)));
    input.getMaxInclusive().ifPresent(m -> graphBuilder.add(inputId, SHACL.MAX_INCLUSIVE, literal(m)));
    input.getDefaultValue().ifPresent(dv -> {
      if (dv.isRight()) {
        Resource bNode = rdf.createBNode();
        graphBuilder.add(bNode, SHACL.NODE, iri(dv.get()));
        graphBuilder.add(inputId, SHACL.DEFAULT_VALUE, bNode);
      } else {
        graphBuilder.add(inputId, SHACL.DEFAULT_VALUE, literal(dv.getLeft()));
      }
    });

    input.getQualifiedValueShape().ifPresent(shape -> {
      graphBuilder.add(iri(shape), RDF.TYPE, SHACL.NODE_SHAPE);
      input.getRequiredSemanticTypes().forEach(type -> graphBuilder.add(iri(shape), SHACL.CLASS, iri(type)));
      input.getInputs().forEach(i -> graphBuilder.add(iri(shape), SHACL.PROPERTY, addInput(i)));
    });
    return inputId;
  }

  private void addGroup(Group group, Resource propertyId) {
    Resource groupNode = resolveHostableLocation(group);
    graphBuilder.add(propertyId, SHACL.GROUP, groupNode);
    graphBuilder.add(groupNode, RDF.TYPE, group.getType().toIRI());
    group.getOrder().ifPresent(order -> graphBuilder.add(groupNode, SHACL.ORDER, literal(order)));
    group.getLabel().ifPresent(label -> graphBuilder.add(groupNode, RDFS.LABEL, literal(label)));
    group.getComment().ifPresent(comment -> graphBuilder.add(groupNode, RDFS.COMMENT, literal(comment)));
  }

  private void addFormsList(Resource propertyId, List<IRI> formNodes) {
    Resource collectionNode = createFormsCollection(formNodes);
    graphBuilder.add(propertyId, SHACL.OR, collectionNode);
  }

  private Resource createFormsCollection(List<IRI> formNodes) {
    if (formNodes.isEmpty()) {
      return RDF.NIL;
    }

    BNode node = bnode();
    BNode valueNode = bnode();

    graphBuilder.add(node, RDF.FIRST, valueNode);
    graphBuilder.add(valueNode, SHACL.HAS_VALUE, formNodes.get(0));
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

  private void createFormNode(Form form) {
    graphBuilder.add(form.getIRI().get(), RDF.TYPE, HCTL.FORM);
    graphBuilder.add(form.getIRI().get(), HCTL.HAS_TARGET, iri(form.getTarget()));
    form.getMethodName().ifPresent(m -> graphBuilder.add(form.getIRI().get(), HTV.METHOD_NAME, literal(m)));
    graphBuilder.add(form.getIRI().get(), HCTL.FOR_CONTENT_TYPE, literal(form.getContentType()));
  }
}
