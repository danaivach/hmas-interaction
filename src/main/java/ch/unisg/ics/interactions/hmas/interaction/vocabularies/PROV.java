package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class PROV {
    public static final String NAMESPACE = "http://www.w3.org/ns/prov/";
    public static final String PREFIX = "prov";
    public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

    public static final IRI USED;

    static {
        SimpleValueFactory rdf = SimpleValueFactory.getInstance();

        USED = rdf.createIRI(NAMESPACE + "used");
    }
}
