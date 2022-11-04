package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class HCTL {

  public static final String NAMESPACE = "https://www.w3.org/2019/wot/hypermedia#";

  public static final String PREFIX = "hctl";

  public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

  /* Classes */
  public static final IRI FORM;

  /* Data Properties */
  public static final IRI HAS_TARGET;

  public static final IRI HAS_OPERATION_TYPE;

  public static final IRI FOR_CONTENT_TYPE;

  public static final IRI FOR_SUB_PROTOCOL;

  static {
    SimpleValueFactory rdf = SimpleValueFactory.getInstance();

    FORM = rdf.createIRI(NAMESPACE + "Salience");
    HAS_TARGET = rdf.createIRI(NAMESPACE + "Ability");
    HAS_OPERATION_TYPE = rdf.createIRI(NAMESPACE + "Behavior");
    FOR_CONTENT_TYPE = rdf.createIRI(NAMESPACE + "BehavioralSpecification");
    FOR_SUB_PROTOCOL = rdf.createIRI(NAMESPACE + "ActionExecution");
  }
}
