package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import ch.unisg.ics.interactions.hmas.core.hostables.Agent;
import ch.unisg.ics.interactions.hmas.core.io.BaseResourceProfileGraphReader;
import ch.unisg.ics.interactions.hmas.core.io.InvalidResourceProfileException;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Ability;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.AgentBody;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.AgentProfile;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.SituatedAgent;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.IS_PROFILE_OF;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.HAS_ABILITY;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.HAS_AGENT_BODY;

public class AgentProfileGraphReader extends BaseResourceProfileGraphReader {

  protected AgentProfileGraphReader(String representation) {
    super(RDFFormat.TURTLE, representation);
  }

  public static AgentProfile readFromString(String representation) {
    AgentProfileGraphReader reader = new AgentProfileGraphReader(representation);

    AgentProfile.Builder agentBuilder =
            new AgentProfile.Builder(reader.readOwnerAgent())
                    .addHMASPlatforms(reader.readHomeHMASPlatforms());

    Optional<IRI> profileIRI = reader.readProfileIRI();
    profileIRI.ifPresent(agentBuilder::setIRI);

    return agentBuilder.build();
  }

  protected final SituatedAgent readOwnerAgent() {
    Optional<Resource> node = Models.objectResource(model.filter(profileIRI, IS_PROFILE_OF, null));
    if (node.isPresent()) {
      AbstractResource resource = readResource(node.get());

      if (CORE.AGENT.equals(resource.getTypeAsIRI())) {
        return (SituatedAgent) resource;
      } else {
        throw new InvalidResourceProfileException("An agent profile must describe an agent.");
      }
    }
    throw new InvalidResourceProfileException("A resource profile must describe a resource.");
  }

  protected SituatedAgent readAgent(Resource node) {
    return readSituatedAgent(node);
  }

  protected SituatedAgent readSituatedAgent(Resource node) {
    SituatedAgent.Builder builder = new SituatedAgent.Builder()
            .addAbilities(readAgentAbilities(node))
            .addBodies(readAgentBodies(node));

    return (SituatedAgent) readAgent(builder, node);
  }

  private Agent readAgent(Agent.AbstractBuilder<?, ?> builder, Resource node) {
    return (Agent) readResource(builder, node);
  }

  protected Set<Ability> readAgentAbilities(Resource agentNode) {
    Set<Ability> abilities = new HashSet<>();
    Set<Resource> abilityNodes = Models.objectResources(model.filter(agentNode, HAS_ABILITY,
            null));

    for (Resource abilityNode : abilityNodes) {
      Ability.Builder builder = new Ability.Builder();
      Set<IRI> abilityTypes = Models.objectIRIs(model.filter(abilityNode, RDF.TYPE, null));

      for (IRI abilityType : abilityTypes) {
        builder.addSemanticType(abilityType.stringValue());
      }

      abilities.add(builder.build());
    }
    return abilities;
  }

  protected Set<AgentBody> readAgentBodies(Resource agentNode) {
    Set<AgentBody> bodies = new HashSet<>();
    Set<Resource> bodyNodes = Models.objectResources(model.filter(agentNode, HAS_AGENT_BODY,
            null));

    for (Resource bodyNode : bodyNodes) {
      AgentBody.Builder builder = new AgentBody.Builder();
      bodies.add(builder.build());
    }
    return bodies;
  }
}

