package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
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

    FORM = rdf.createIRI(NAMESPACE + "Form");
    HAS_TARGET = rdf.createIRI(NAMESPACE + "hasTarget");
    HAS_OPERATION_TYPE = rdf.createIRI(NAMESPACE + "hasOperationType");
    FOR_CONTENT_TYPE = rdf.createIRI(NAMESPACE + "forContentType");
    FOR_SUB_PROTOCOL = rdf.createIRI(NAMESPACE + "forSubProtocol");
  }

  public enum TERM implements HMAS {
    FORM(HCTL.FORM),
    HAS_TARGET(HCTL.HAS_TARGET),
    HAS_OPERATION_TYPE(HCTL.HAS_OPERATION_TYPE),
    FOR_CONTENT_TYPE(HCTL.FOR_CONTENT_TYPE),
    FOR_SUB_PROTOCOL(HCTL.FOR_SUB_PROTOCOL);

    private final IRI iri;

    TERM(IRI iri) {
      this.iri = iri;
    }

    @Override
    public String toString() {
      return iri.toString();
    }

    @Override
    public IRI toIRI() {
      return this.iri;
    }
  }
}
