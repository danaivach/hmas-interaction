package ch.unisg.ics.interactions.hmas.interaction.signifiers.vocabularies;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public enum INTERACTION {
  PREFIX("https://purl.org/hmas/interaction#"),

  /* Classes */
  SALIENCE(PREFIX + "Salience"),
  ABILITY(PREFIX + "Ability"),
  BEHAVIOR(PREFIX + "Behavior"),
  BEHAVIORAL_SPECIFICATION(PREFIX + "BehavioralSpecification"),
  ACTION_EXECUTION(PREFIX + "ActionExecution"),
  ACTION_SPECIFICATION(PREFIX + "ActionSpecification"),
  EXPECTED_INPUT(PREFIX + "Input"),
  INPUT_SCHEMA(PREFIX + "InputSchema"),
  AGENT_BODY(PREFIX + "AgentBody"),

  /* Object Properties */
  SIGNIFIES(PREFIX + "signifier"),
  RECOMMENDS_ABILITY(PREFIX + "recommendsAbility"),
  EXPECTS(PREFIX + "expects"),
  HAS_INPUT_SCHEMA(PREFIX + "hasInputSchema"),
  HAS_FORM(PREFIX + "hasForm"),
  HAS_ACTION(PREFIX + "hasAction"),
  HAS_AGENT_BODY(PREFIX + "hasAgentBody");

  private final String type;

  private INTERACTION(String type) {
    this.type = type;
  }

  public String toString() {
    return this.type;
  }

  public IRI toIRI() {
    return SimpleValueFactory.getInstance().createIRI(this.type);
  }
}
