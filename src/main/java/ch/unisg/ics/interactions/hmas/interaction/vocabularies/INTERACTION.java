package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;


public class INTERACTION {

  public static final String NAMESPACE = "https://purl.org/hmas/interaction#";

  public static final String PREFIX = "hmas-int";

  public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

  /* Classes */
  public static final IRI SALIENCE;

  public static final IRI ABILITY;

  public static final IRI BEHAVIOR;

  public static final IRI BEHAVIORAL_SPECIFICATION;

  public static final IRI ACTION_EXECUTION;

  public static final IRI ACTION_SPECIFICATION;

  public static final IRI EXPECTED_INPUT;

  public static final IRI INPUT_SCHEMA;

  public static final IRI AGENT_BODY;

  /* Object Properties */
  public static final IRI SIGNIFIES;

  public static final IRI RECOMMENDS_ABILITY;

  public static final IRI HAS_ABILITY;

  public static final IRI EXPECTS;

  public static final IRI HAS_INPUT_SCHEMA;

  public static final IRI HAS_FORM;

  public static final IRI HAS_ACTION;

  public static final IRI HAS_AGENT_BODY;

  static {
    SimpleValueFactory rdf = SimpleValueFactory.getInstance();

    SALIENCE = rdf.createIRI(NAMESPACE + "Salience");
    ABILITY = rdf.createIRI(NAMESPACE + "Ability");
    BEHAVIOR = rdf.createIRI(NAMESPACE + "Behavior");
    BEHAVIORAL_SPECIFICATION = rdf.createIRI(NAMESPACE + "BehavioralSpecification");
    ACTION_EXECUTION = rdf.createIRI(NAMESPACE + "ActionExecution");
    ACTION_SPECIFICATION = rdf.createIRI(NAMESPACE + "ActionSpecification");
    EXPECTED_INPUT = rdf.createIRI(NAMESPACE + "Input");
    INPUT_SCHEMA = rdf.createIRI(NAMESPACE + "Signfiier");
    AGENT_BODY = rdf.createIRI(NAMESPACE + "InputSchema");

    SIGNIFIES = rdf.createIRI(NAMESPACE + "signifies");
    RECOMMENDS_ABILITY = rdf.createIRI(NAMESPACE + "recommendsAbility");
    HAS_ABILITY = rdf.createIRI(NAMESPACE + "hasAbility");
    EXPECTS = rdf.createIRI(NAMESPACE + "expects");
    HAS_INPUT_SCHEMA = rdf.createIRI(NAMESPACE + "hasInputSchema");
    HAS_FORM = rdf.createIRI(NAMESPACE + "hasForm");
    HAS_ACTION = rdf.createIRI(NAMESPACE + "hasAction");
    HAS_AGENT_BODY = rdf.createIRI(NAMESPACE + "hasAgentBody");
  }

  public enum TERM implements HMAS {

    /* Classes */
    SALIENCE(INTERACTION.SALIENCE),
    ABILITY(INTERACTION.ABILITY),
    BEHAVIOR(INTERACTION.BEHAVIOR),
    BEHAVIORAL_SPECIFICATION(INTERACTION.BEHAVIORAL_SPECIFICATION),
    ACTION_EXECUTION(INTERACTION.ACTION_EXECUTION),
    ACTION_SPECIFICATION(INTERACTION.ACTION_SPECIFICATION),
    EXPECTED_INPUT(INTERACTION.EXPECTED_INPUT),
    INPUT_SCHEMA(INTERACTION.INPUT_SCHEMA),
    AGENT_BODY(INTERACTION.AGENT_BODY),

    /* Object Properties */
    SIGNIFIES(INTERACTION.SIGNIFIES),
    RECOMMENDS_ABILITY(INTERACTION.RECOMMENDS_ABILITY),
    HAS_ABILITY(INTERACTION.HAS_ABILITY),
    EXPECTS(INTERACTION.EXPECTS),
    HAS_INPUT_SCHEMA(INTERACTION.HAS_INPUT_SCHEMA),
    HAS_FORM(INTERACTION.HAS_FORM),
    HAS_ACTION(INTERACTION.HAS_ACTION),
    HAS_AGENT_BODY(INTERACTION.HAS_AGENT_BODY);

    private final IRI type;

    TERM(IRI type) {
      this.type = type;
    }

    public String toString() {
      return this.type.toString();
    }

    public IRI toIRI() {
      return this.type;
    }
  }
}

