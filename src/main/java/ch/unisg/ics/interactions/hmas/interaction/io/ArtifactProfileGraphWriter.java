package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphWriter;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.*;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.*;
import static org.eclipse.rdf4j.model.util.Values.*;

public class ArtifactProfileGraphWriter extends ResourceProfileGraphWriter<ArtifactProfile> {

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
            .setNamespace("urn", "http://example.org/urn#")
            .addSignifiers();

    return super.write();
  }

  @Override
  public ArtifactProfileGraphWriter setNamespace(String prefix, String namespace) {
    this.graphBuilder.setNamespace(prefix, namespace);
    return this;
  }

  private ArtifactProfileGraphWriter addSignifiers() {

    Set<Signifier> signifiers = profile.getExposedSignifiers();

    for (Signifier signifier : signifiers) {
      Resource locatedSignifier = resolveHostableLocation(signifier);

      graphBuilder.add(profileIRI, EXPOSES_SIGNIFIER, locatedSignifier);
      graphBuilder.add(locatedSignifier, RDF.TYPE, SIGNIFIER);

      Set<Ability> abilities = signifier.getRecommendedAbilities();
      addRecommendedAbilities(locatedSignifier, abilities);

      AbstractResource bSpec = signifier.getResource();
      addAbstractResource(locatedSignifier, bSpec);
    }
    return this;
  }

  private void addRecommendedAbilities(Resource signifier, Set<Ability> abilities) {

    for (Ability ability : abilities) {
      Resource abilityId = rdf.createBNode();

      graphBuilder.add(signifier, RECOMMENDS_ABILITY, abilityId);
      graphBuilder.add(abilityId, RDF.TYPE, ABILITY);

      for (String type : ability.getSemanticTypes()) {
        graphBuilder.add(abilityId, RDF.TYPE, rdf.createIRI(type));
      }
    }
  }

  private void addAbstractResource(Resource signifier, AbstractResource specification) {

    Resource nodeShapeId = rdf.createBNode();
    graphBuilder.add(signifier, SIGNIFIES, nodeShapeId);
    graphBuilder.add(nodeShapeId, RDF.TYPE, SHACL.NODE_SHAPE);
    graphBuilder.add(nodeShapeId, SHACL.CLASS, ACTION_EXECUTION);

    Resource propertyId = rdf.createBNode();
    graphBuilder.add(nodeShapeId, SHACL.PROPERTY, propertyId);
    graphBuilder.add(propertyId, SHACL.PATH, PROV.USED);
    graphBuilder.add(propertyId, SHACL.MIN_COUNT, literal("1"));
    graphBuilder.add(propertyId, SHACL.MAX_COUNT, literal("1"));

    ActionSpecification actionSpecification = (ActionSpecification) specification;
    Set<Form> forms = actionSpecification.getForms();

    if (forms.size() == 1) {
      Form form = forms.iterator().next();
      createFormNode(form);
      graphBuilder.add(propertyId, SHACL.HAS_VALUE, form.getIRI().get());
    }
    if (forms.size() > 1) {
      List<IRI> formNodes = forms.stream().map(f -> f.getIRI().get()).toList();

      BNode collectionNode = bnode();

      BNode node = bnode();
      graphBuilder.add(node, SHACL.HAS_VALUE, formNodes.get(0));
      graphBuilder.add(collectionNode, RDF.FIRST, node);

      // Create the rest of the collection
      BNode previousNode = collectionNode;
      for (int i = 1; i < formNodes.size(); i++) {
        BNode nextNode = bnode();
        graphBuilder.add(previousNode, RDF.REST, nextNode);
        BNode n = bnode();
        graphBuilder.add(n, SHACL.HAS_VALUE, formNodes.get(i));
        graphBuilder.add(nextNode, RDF.FIRST, n);
        previousNode = nextNode;
      }

      // Add rdf:nil to terminate the collection
      graphBuilder.add(previousNode, RDF.REST, RDF.NIL);
      graphBuilder.add(propertyId, SHACL.OR, collectionNode);

      forms.forEach(this::createFormNode);
    }
  }

  private void createFormNode(Form form) {
    graphBuilder.add(form.getIRI().get(), RDF.TYPE, HCTL.FORM);
    graphBuilder.add(form.getIRI().get(), HCTL.HAS_TARGET, iri(form.getTarget()));
  }
}
