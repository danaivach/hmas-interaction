package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.AGENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AgentProfileGraphReaderTest {

  private static final String PREFIXES =
          "@prefix hmas: <" + CORE.NAMESPACE + "> .\n" +
                  "@prefix prs: <http://example.org/prs#> \n";


  @Test
  public void testReadSignifierWithAbilities() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Agent ;" +
            "  hmas:hasAbility [ a hmas:Ability, prs:PRSAbility ]\n" +
            " ].";

    AgentProfile profile =
            AgentProfileGraphReader.readFromString(expectedProfile);

    CapableAgent agent = profile.getAgent();
    assertEquals(AGENT, agent.getTypeAsIRI());
    assertFalse(agent.getIRI().isPresent());

    Set<Ability> abilities = agent.getAbilities();
    assertEquals(1, abilities.size());

    List<Ability> abilitiesList = new ArrayList<>(abilities);
    Ability ability = abilitiesList.get(0);
    assertEquals(2, ability.getSemanticTypes().size());
  }

  @Test
  public void testReadSignifierWithBody() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Agent ;" +
            "  hmas:hasAgentBody <urn:body>\n" +
            " ]. \n" +
            "\n" +
            "<urn:body> a hmas:AgentBody .";

    AgentProfile profile =
            AgentProfileGraphReader.readFromString(expectedProfile);

    SituatedAgent agent = profile.getAgent();
    assertEquals(AGENT, agent.getTypeAsIRI());
    assertFalse(agent.getIRI().isPresent());

    Set<AgentBody> bodies = agent.getBodies();
    assertEquals(1, bodies.size());
  }

}
