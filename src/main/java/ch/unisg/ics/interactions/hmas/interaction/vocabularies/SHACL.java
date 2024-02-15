package ch.unisg.ics.interactions.hmas.interaction.vocabularies;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import ch.unisg.ics.interactions.hmas.core.vocabularies.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class SHACL {
  public static final String NAMESPACE = "http://www.w3.org/ns/shacl#";
  public static final String PREFIX = "sh";
  public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

  public static final IRI NODE_SHAPE;
  public static final IRI PROPERTY_GROUP;

  public static final IRI CLASS;
  public static final IRI PROPERTY;
  public static final IRI MIN_COUNT;
  public static final IRI MAX_COUNT;
  public static final IRI OR;
  public static final IRI HAS_VALUE;
  public static final IRI PATH;
  public static final IRI QUALIFIED_VALUE_SHAPE;
  public static final IRI QUALIFIED_MIN_COUNT;
  public static final IRI QUALIFIED_MAX_COUNT;
  public static final IRI DATATYPE;
  public static final IRI NAME;
  public static final IRI DESCRIPTION;
  public static final IRI ORDER;
  public static final IRI TARGET_CLASS;
  public static final IRI DEFAULT_VALUE;
  public static final IRI MIN_INCLUSIVE;
  public static final IRI MAX_INCLUSIVE;
  public static final IRI NODE;
  public static final IRI GROUP;

  static {
    SimpleValueFactory rdf = SimpleValueFactory.getInstance();

    NODE_SHAPE = rdf.createIRI(NAMESPACE + "NodeShape");
    PROPERTY_GROUP = rdf.createIRI(NAMESPACE + "PropertyGroup");

    CLASS = rdf.createIRI(NAMESPACE + "class");
    PROPERTY = rdf.createIRI(NAMESPACE + "property");
    MIN_COUNT = rdf.createIRI(NAMESPACE + "minCount");
    MAX_COUNT = rdf.createIRI(NAMESPACE + "maxCount");
    OR = rdf.createIRI(NAMESPACE + "or");
    HAS_VALUE = rdf.createIRI(NAMESPACE + "hasValue");
    PATH = rdf.createIRI(NAMESPACE + "path");
    QUALIFIED_VALUE_SHAPE = rdf.createIRI(NAMESPACE + "qualifiedValueShape");
    QUALIFIED_MIN_COUNT = rdf.createIRI(NAMESPACE + "qualifiedMinCount");
    QUALIFIED_MAX_COUNT = rdf.createIRI(NAMESPACE + "qualifiedMaxCount");
    DATATYPE = rdf.createIRI(NAMESPACE + "datatype");
    NAME = rdf.createIRI(NAMESPACE + "name");
    DESCRIPTION = rdf.createIRI(NAMESPACE + "description");
    ORDER = rdf.createIRI(NAMESPACE + "order");
    TARGET_CLASS = rdf.createIRI(NAMESPACE + "targetClass");
    DEFAULT_VALUE = rdf.createIRI(NAMESPACE + "defaultValue");
    MIN_INCLUSIVE = rdf.createIRI(NAMESPACE + "minInclusive");
    MAX_INCLUSIVE = rdf.createIRI(NAMESPACE + "maxInclusive");
    NODE = rdf.createIRI(NAMESPACE + "node");
    GROUP = rdf.createIRI(NAMESPACE + "group");
  }

  public enum TERM implements HMAS {
    NODE_SHAPE(SHACL.NODE_SHAPE),
    PROPERTY_GROUP(SHACL.PROPERTY_GROUP),

    CLASS(SHACL.CLASS),
    PROPERTY(SHACL.PROPERTY),
    MIN_COUNT(SHACL.MIN_COUNT),
    MAX_COUNT(SHACL.MAX_COUNT),
    OR(SHACL.OR),
    HAS_VALUE(SHACL.HAS_VALUE),
    PATH(SHACL.PATH),
    QUALIFIED_VALUE_SHAPE(SHACL.QUALIFIED_VALUE_SHAPE),
    QUALIFIED_MIN_COUNT(SHACL.QUALIFIED_MIN_COUNT),
    QUALIFIED_MAX_COUNT(SHACL.QUALIFIED_MAX_COUNT),
    DATATYPE(SHACL.DATATYPE),
    NAME(SHACL.NAME),
    DESCRIPTION(SHACL.DESCRIPTION),
    ORDER(SHACL.ORDER),
    TARGET_CLASS(SHACL.TARGET_CLASS),
    DEFAULT_VALUE(SHACL.DEFAULT_VALUE),
    MIN_INCLUSIVE(SHACL.MIN_INCLUSIVE),
    MAX_INCLUSIVE(SHACL.MAX_INCLUSIVE),
    NODE(SHACL.NODE),
    GROUP(SHACL.GROUP);

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
