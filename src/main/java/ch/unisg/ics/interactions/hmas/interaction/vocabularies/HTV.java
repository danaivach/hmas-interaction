package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class HTV {
  public static final String NAMESPACE = "http://www.w3.org/2011/http#";

  public static final String PREFIX = "htv";

  public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

  /* Data Properties */
  public static final IRI METHOD_NAME;

  static {
    SimpleValueFactory rdf = SimpleValueFactory.getInstance();

    METHOD_NAME = rdf.createIRI(NAMESPACE + "methodName");
  }
}
