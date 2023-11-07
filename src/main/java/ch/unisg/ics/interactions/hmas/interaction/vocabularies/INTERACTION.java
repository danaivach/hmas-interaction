package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;


public class INTERACTION {

  public static final String NAMESPACE = "https://purl.org/hmas/";

  public static final String PREFIX = "hmas";

  public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

  /* Classes */
  public static final IRI SALIENCE;

  public static final IRI ABILITY;

  public static final IRI BEHAVIOR;

  public static final IRI BEHAVIORAL_SPECIFICATION;

  public static final IRI ACTION_EXECUTION;

  public static final IRI ACTION_SPECIFICATION;

  public static final IRI AGENT_BODY;

  /* Object Properties */
  public static final IRI SIGNIFIES;

  public static final IRI RECOMMENDS_ABILITY;

  public static final IRI RECOMMENDS_CONTEXT;

  public static final IRI HAS_ABILITY;

  public static final IRI HAS_ACTION;

  public static final IRI HAS_AGENT_BODY;

  public static final IRI HAS_INPUT;

  static {
    SimpleValueFactory rdf = SimpleValueFactory.getInstance();

    SALIENCE = rdf.createIRI(NAMESPACE + "Salience");
    ABILITY = rdf.createIRI(NAMESPACE + "Ability");
    BEHAVIOR = rdf.createIRI(NAMESPACE + "Behavior");
    BEHAVIORAL_SPECIFICATION = rdf.createIRI(NAMESPACE + "BehavioralSpecification");
    ACTION_EXECUTION = rdf.createIRI(NAMESPACE + "ActionExecution");
    ACTION_SPECIFICATION = rdf.createIRI(NAMESPACE + "ActionSpecification");
    AGENT_BODY = rdf.createIRI(NAMESPACE + "AgentBody");

    SIGNIFIES = rdf.createIRI(NAMESPACE + "signifies");
    RECOMMENDS_ABILITY = rdf.createIRI(NAMESPACE + "recommendsAbility");
    RECOMMENDS_CONTEXT = rdf.createIRI(NAMESPACE + "recommendsContext");
    HAS_ABILITY = rdf.createIRI(NAMESPACE + "hasAbility");
    HAS_ACTION = rdf.createIRI(NAMESPACE + "hasAction");
    HAS_AGENT_BODY = rdf.createIRI(NAMESPACE + "hasAgentBody");
    HAS_INPUT = rdf.createIRI(NAMESPACE + "hasInput");
  }

  public enum TERM implements HMAS {

    /* Classes */
    SALIENCE(INTERACTION.SALIENCE),
    ABILITY(INTERACTION.ABILITY),
    BEHAVIOR(INTERACTION.BEHAVIOR),
    BEHAVIORAL_SPECIFICATION(INTERACTION.BEHAVIORAL_SPECIFICATION),
    ACTION_EXECUTION(INTERACTION.ACTION_EXECUTION),
    ACTION_SPECIFICATION(INTERACTION.ACTION_SPECIFICATION),
    AGENT_BODY(INTERACTION.AGENT_BODY),

    /* Object Properties */
    SIGNIFIES(INTERACTION.SIGNIFIES),
    RECOMMENDS_ABILITY(INTERACTION.RECOMMENDS_ABILITY),
    RECOMMENDS_CONTEXT(INTERACTION.RECOMMENDS_CONTEXT),
    HAS_ABILITY(INTERACTION.HAS_ABILITY),
    HAS_ACTION(INTERACTION.HAS_ACTION),
    HAS_AGENT_BODY(INTERACTION.HAS_AGENT_BODY),
    HAS_INPUT(INTERACTION.HAS_INPUT);

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

