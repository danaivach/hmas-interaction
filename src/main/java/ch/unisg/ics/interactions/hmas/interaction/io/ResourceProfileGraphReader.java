package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.io.BaseResourceProfileGraphReader;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Ability;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.CapableAgent;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.ResourceProfile;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Signifier;
import org.apache.hc.client5.http.fluent.Request;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.HAS_ABILITY;

public class ResourceProfileGraphReader extends BaseResourceProfileGraphReader {

  protected ResourceProfileGraphReader(RDFFormat format, String representation) {
    super(format, representation);
  }

  public static ResourceProfile readFromURL(String url) throws IOException {
    String representation = Request.get(url).execute().returnContent().asString();
    return readFromString(representation);
  }

  public static ResourceProfile readFromFile(String path) throws IOException {
    String content = new String(Files.readAllBytes(Paths.get(path)));
    return readFromString(content);
  }

  public static ResourceProfile readFromString(String representation) {
    ResourceProfileGraphReader reader = new ResourceProfileGraphReader(RDFFormat.TURTLE, representation);

    ResourceProfile.Builder artifactBuilder =
            new ResourceProfile.Builder(reader.readOwnerResource())
                    .addHMASPlatforms(reader.readHomeHMASPlatforms())
                    .addSemanticTypes(reader.readSemanticTypes())
                    .exposeSignifiers(reader.readSignifiers())
                    .addGraph(reader.getModel());

    Optional<IRI> profileIRI = reader.readProfileIRI();
    profileIRI.ifPresent(artifactBuilder::setIRI);

    return artifactBuilder.build();
  }

  public static Model getModelFromString(String representation) {
    ResourceProfileGraphReader reader = new ResourceProfileGraphReader(RDFFormat.TURTLE, representation);
    return reader.getModel();
  }

  @Override
  protected CapableAgent readAgent(Resource node) {
    CapableAgent.Builder builder = new CapableAgent.Builder()
            .addAbilities(readAgentAbilities(node));
    return (CapableAgent) readHostable(builder, node);
  }

  protected Set<Ability> readAgentAbilities(Resource agentNode) {
    Set<Ability> abilities = new HashSet<>();
    Set<Resource> abilityNodes = Models.objectResources(model.filter(agentNode, HAS_ABILITY,
            null));

    for (Resource abilityNode : abilityNodes) {
      Ability.Builder builder = new Ability.Builder();
      abilities.add((Ability) readResource(builder, abilityNode));
    }
    return abilities;
  }

  protected Set<Signifier> readSignifiers() {
    Set<Signifier> signifiers = new HashSet<>();
    Set<Resource> signifierNodes = Models.objectResources(model.filter(profileIRI, EXPOSES_SIGNIFIER,
            null));
    for (Resource signifierNode : signifierNodes) {
      try {
        Signifier signifier = SignifierGraphReader.readFromModel(model, signifierNode);
        signifiers.add(signifier);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return signifiers;
  }
}
