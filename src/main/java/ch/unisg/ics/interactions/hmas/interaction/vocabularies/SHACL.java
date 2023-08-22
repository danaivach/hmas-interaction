package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class SHACL {
    public static final String NAMESPACE = "http://www.w3.dorg/ns/shacl/";
    public static final String PREFIX = "sh";
    public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

    public static final IRI NODE_SHAPE;

    public static final IRI CLASS;
    public static final IRI PROPERTY;
    public static final IRI MIN_COUNT;
    public static final IRI MAX_COUNT;
    public static final IRI OR;
    public static final IRI HAS_VALUE;
    public static final IRI PATH;

    static {
        SimpleValueFactory rdf = SimpleValueFactory.getInstance();

        NODE_SHAPE = rdf.createIRI(NAMESPACE + "NodeShape");

        CLASS = rdf.createIRI(NAMESPACE + "class");
        PROPERTY = rdf.createIRI(NAMESPACE + "property");
        MIN_COUNT = rdf.createIRI(NAMESPACE + "minCount");
        MAX_COUNT = rdf.createIRI(NAMESPACE + "maxCount");
        OR = rdf.createIRI(NAMESPACE + "or");
        HAS_VALUE = rdf.createIRI(NAMESPACE + "hasValue");
        PATH = rdf.createIRI(NAMESPACE + "path");
    }

    public enum TERM implements HMAS {
        NODE_SHAPE(SHACL.NODE_SHAPE),

        CLASS(SHACL.CLASS),
        PROPERTY(SHACL.PROPERTY),
        MIN_COUNT(SHACL.MIN_COUNT),
        MAX_COUNT(SHACL.MAX_COUNT),
        OR(SHACL.OR),
        HAS_VALUE(SHACL.HAS_VALUE),
        PATH(SHACL.PATH);

        private final IRI type;

        TERM(IRI type) {
            this.type = type;
        }

        @Override
        public IRI toIRI() {
            return this.type;
        }

        @Override
        public String toString() {
            return this.type.toString();
        }
    }
}
