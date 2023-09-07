package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class RDFS {
  public static final String NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
  public static final String PREFIX = "rdfs";
  public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

  public static final IRI LABEL;
  public static final IRI COMMENT;

  static {
    SimpleValueFactory rdf = SimpleValueFactory.getInstance();

    LABEL = rdf.createIRI(NAMESPACE + "label");
    COMMENT = rdf.createIRI(NAMESPACE + "comment");
  }

  public enum TERM implements HMAS {
    LABEL(RDFS.LABEL),
    COMMENT(RDFS.COMMENT);

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
      return iri;
    }
  }
}
