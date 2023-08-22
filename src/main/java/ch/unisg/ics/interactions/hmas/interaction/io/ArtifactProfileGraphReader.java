package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.io.InvalidResourceProfileException;
import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphReader;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HTV;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleBNode;
import org.eclipse.rdf4j.model.impl.SimpleIRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.spin.function.spif.For;
import org.eclipse.rdf4j.spin.function.spif.Mod;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.*;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.*;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL.*;

public class ArtifactProfileGraphReader extends ResourceProfileGraphReader {

  protected ArtifactProfileGraphReader(RDFFormat format, String representation) {
    super(format, representation);
  }

  public static ArtifactProfile readFromString(String representation) {
    ArtifactProfileGraphReader reader = new ArtifactProfileGraphReader(RDFFormat.TURTLE, representation);

    ArtifactProfile.Builder artifactBuilder =
            new ArtifactProfile.Builder(reader.readOwnerResource())
                    .addHMASPlatforms(reader.readHomeHMASPlatforms())
                    .addSemanticTypes(reader.readSemanticTypes())
                    .exposeSignifiers(reader.readSignifiers());

    Optional<IRI> profileIRI = reader.readProfileIRI();
    if (profileIRI.isPresent()) {
      artifactBuilder.setIRI(profileIRI.get());
    }

    return artifactBuilder.build();
  }

  protected final Artifact readOwnerResource() {
    Optional<Resource> node = Models.objectResource(model.filter(profileIRI, IS_PROFILE_OF, null));
    if (node.isPresent()) {
      return readResource(node.get());
    }
    throw new InvalidResourceProfileException("An artifact profile must describe an artifact.");
  }

  protected final Artifact readResource(Resource node) {

    Set<IRI> types = Models.objectIRIs(model.filter(node, RDF.TYPE, null));

    if (types.contains(ARTIFACT) || types.contains(AGENT_BODY)) {
      return readArtifact(node);
    }
    throw new InvalidResourceProfileException("Unknown type of profiled resource. " +
            "Supported resource types: Artifact, AgentBody.");
  }

  protected Set<Signifier> readSignifiers() {
    Set<Signifier> signifiers = new HashSet<>();
    Set<Resource> signifierNodes = Models.objectResources(model.filter(profileIRI, EXPOSES_SIGNIFIER,
            null));
    for (Resource signifierNode : signifierNodes) {
      Optional<Resource> bSpecNode = Models.objectResource(model.filter(signifierNode, SIGNIFIES, null));

      // TODO Read also behavior specs
      if (bSpecNode.isPresent()) {
        ActionSpecification acSpec = readActionSpecification(bSpecNode.get());
        Signifier.Builder builder = new Signifier.Builder(acSpec);

        Set<Resource> abilities = Models.objectResources(model.filter(signifierNode, RECOMMENDS_ABILITY,
                null));

        for (Resource ability : abilities) {
          Ability.Builder abilityBuilder = new Ability.Builder();
          Set<IRI> abilityTypes = Models.objectIRIs(model.filter(ability, RDF.TYPE,
                  null));

          for (IRI abilityType : abilityTypes) {
            abilityBuilder.addSemanticType(abilityType.stringValue());
          }
          builder.addRecommendedAbility(abilityBuilder.build());
        }

        if (signifierNode.isIRI()) {
          builder.setIRI(SimpleValueFactory.getInstance().createIRI(signifierNode.stringValue()));
        }
        signifiers.add(builder.build());
      } else {
        throw new InvalidResourceProfileException("Signifiers with behavioral specifications were expected. ");
      }
    }
    return signifiers;
  }

  protected ActionSpecification readActionSpecification(Resource specNode) {
    Resource propNode = Models.objectResource(model.filter(specNode, PROPERTY, null))
            .orElseThrow(() -> new InvalidResourceProfileException(
                    "An action specification was found with no property."));
    return new ActionSpecification.Builder(readForms(readFormResources(propNode))).build();
  }

  protected Set<Resource> readFormResources(Resource propNode) {
    if (model.contains(propNode, HAS_VALUE, null)) {
      return Models.objectResources(model.filter(propNode, HAS_VALUE, null));
    }
    if (model.contains(propNode, OR, null)) {
      return Models.objectResource(model.filter(propNode, OR, null))
              .map(orNode -> Models.objectResources(model.filter(orNode, null, null)))
              .map(x -> x.stream().flatMap(l -> extractForms(l).stream()).collect(Collectors.toSet()))
              .orElse(Set.of());
    }
    throw new InvalidResourceProfileException("An action specification was found with no forms. " +
            "An action specification should have at least one form. ");
  }

  private Set<Resource> extractForms(Resource node) {
    Set<Resource> resources = new HashSet<>();
    Resource n = node;
    while (Models.objectResources(model.filter(n, null, null)).stream()
            .anyMatch(r -> r instanceof SimpleBNode)) {
      Models.objectResources(model.filter(n, null, null)).stream()
              .filter(r -> r instanceof SimpleIRI).findFirst().ifPresent(resources::add);
      n = Models.objectResources(model.filter(n, null, null)).stream()
              .filter(r -> r instanceof SimpleBNode).findFirst().get();
    }
    Models.objectResources(model.filter(n, null, null)).stream()
            .filter(r -> r instanceof SimpleIRI).findFirst().ifPresent(resources::add);
    return resources;
  }

  protected Set<Form> readForms(Set<Resource> formNodes) {
    Set<Form> forms = new HashSet<>();
    for (Resource formNode : formNodes) {
      Optional<IRI> target = Models.objectIRI(model.filter(formNode, HCTL.HAS_TARGET,
              null));

      if (target.isPresent()) {
        Form.Builder builder = new Form.Builder(target.get().stringValue());

        Optional<Literal> methodName = Models.objectLiteral(model.filter(formNode, HTV.METHOD_NAME,
                null));
        if (methodName.isPresent()) {
          builder.setMethodName(methodName.get().stringValue());
        }

        builder.addProperty("IRI", formNode.stringValue());

        forms.add(builder.build());

      } else {
        throw new InvalidResourceProfileException("A form was found but its submission target is missing. ");
      }
    }

    return forms;
  }

}
