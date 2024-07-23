package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.io.BaseResourceProfileGraphReader;
import ch.unisg.ics.interactions.hmas.core.io.InvalidResourceProfileException;
import ch.unisg.ics.interactions.hmas.interaction.shapes.*;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.*;
import org.apache.hc.client5.http.fluent.Request;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.*;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL.*;

public class ResourceProfileGraphReader extends BaseResourceProfileGraphReader {

  protected ResourceProfileGraphReader(RDFFormat format, String representation) {
    super(format, representation);
  }

  public static ResourceProfile readFromURL(String url) throws IOException {
    String representation = Request.get(url).execute().returnContent().asString();
    return readFromString(representation);
  }

  public static ResourceProfile readFromFile(String path) throws IOException {
    String content = new String(Files.readAllBytes(Paths.get(path)));
    return readFromString(content);
  }

  public static ResourceProfile readFromString(String representation) {
    ResourceProfileGraphReader reader = new ResourceProfileGraphReader(RDFFormat.TURTLE, representation);

    ResourceProfile.Builder artifactBuilder =
            new ResourceProfile.Builder(reader.readOwnerResource())
                    .addHMASPlatforms(reader.readHomeHMASPlatforms())
                    .addSemanticTypes(reader.readSemanticTypes())
                    .exposeSignifiers(reader.readSignifiers());

    Optional<IRI> profileIRI = reader.readProfileIRI();
    profileIRI.ifPresent(artifactBuilder::setIRI);

    return artifactBuilder.build();
  }

  public static Model getModelFromString(String representation) {
    ResourceProfileGraphReader reader = new ResourceProfileGraphReader(RDFFormat.TURTLE, representation);
    return reader.getModel();
  }

  protected Set<Signifier> readSignifiers() {
    Set<Signifier> signifiers = new HashSet<>();
    Set<Resource> signifierNodes = Models.objectResources(model.filter(profileIRI, EXPOSES_SIGNIFIER,
            null));
    for (Resource signifierNode : signifierNodes) {
      Optional<Resource> bSpecNode = Models.objectResource(model.filter(signifierNode, SIGNIFIES, null));

      // TODO Read also behavior specs
      if (bSpecNode.isPresent()) {
        ActionSpecification acSpec = readActionSpecification(bSpecNode.get());
        Signifier.Builder builder = new Signifier.Builder(acSpec);
        if (signifierNode.isIRI()) {
          builder.setIRIAsString(signifierNode.stringValue());
        }

        Set<Resource> abilities = Models.objectResources(model.filter(signifierNode, RECOMMENDS_ABILITY,
                null));

        for (Resource ability : abilities) {
          Ability.Builder abilityBuilder = new Ability.Builder();
          Set<IRI> abilityTypes = Models.objectIRIs(model.filter(ability, RDF.TYPE,
                  null));

          for (IRI abilityType : abilityTypes) {
            abilityBuilder.addSemanticType(abilityType.stringValue());
          }
          builder.addRecommendedAbility(abilityBuilder.build());
        }

        Models.objectLiteral(model.filter(signifierNode, RDFS.LABEL, null))
                .ifPresent(l -> builder.setLabel(l.getLabel()));
        Models.objectLiteral(model.filter(signifierNode, RDFS.COMMENT, null))
                .ifPresent(l -> builder.setComment(l.getLabel()));

        Set<Resource> contexts = Models.objectResources(model.filter(signifierNode, RECOMMENDS_CONTEXT, null));
        contexts.forEach(c -> {
          Context context = readRecommendedContext(c);
          builder.addRecommendedContext(context);
        });

        if (signifierNode.isIRI()) {
          builder.setIRI(SimpleValueFactory.getInstance().createIRI(signifierNode.stringValue()));
        }
        signifiers.add((Signifier) readResource(builder, signifierNode));
      } else {
        throw new InvalidResourceProfileException("Signifiers with behavioral specifications were expected. ");
      }
    }
    return signifiers;
  }

  protected Context readRecommendedContext(Resource contextNode) {
    Context.Builder contextBuilder = new Context.Builder();
    if (contextNode.isIRI()) {
      contextBuilder.setIRIAsString(contextNode.stringValue());
    }

    Set<Statement> processedStatements = new HashSet<>();

    // Create a queue for BFS traversal
    Queue<Resource> queue = new LinkedList<>();

    // Add the initial subject (contextNode) to the queue
    queue.add(contextNode);

    // Continue BFS traversal
    while (!queue.isEmpty()) {
      Resource currentResource = queue.poll();

      // Iterate through triples with currentResource as subject
      model.filter(currentResource, null, null).forEach(statement -> {
        if (!processedStatements.contains(statement)) {
          contextBuilder.addStatement(statement);
          processedStatements.add(statement);

          // Add the object of the statement to the queue for BFS traversal
          if (statement.getObject() instanceof Resource) {
            queue.add((Resource) statement.getObject());
          }
        }
      });
    }
    return contextBuilder.build();
  }

  protected ActionSpecification readActionSpecification(Resource specNode) {
    Set<Resource> propNodes = Models.objectResources(model.filter(specNode, PROPERTY, null));
    Resource propNodeForm = propNodes.stream().filter(this::isForm).findFirst()
            .orElseThrow(() -> new InvalidResourceProfileException(
                    "An action specification was found with no forms. " + specNode.toString()));

    Optional<Resource> propNodeInput = propNodes.stream().filter(this::isInput).findFirst();
    Optional<Resource> propNodeOutput = propNodes.stream().filter(this::isOutput).findFirst();

    ActionSpecification.Builder acSpecBuilder =
            new ActionSpecification.Builder(readForms(readFormResources(propNodeForm)));

    propNodeInput.ifPresent(input -> acSpecBuilder.setInputSpecification(readIOSpecification(input)));
    propNodeOutput.ifPresent(output -> acSpecBuilder.setOutputSpecification(readIOSpecification(output)));

    acSpecBuilder.addSemanticTypes(
            Models.objectIRIs(model.filter(specNode, RDF.TYPE, null)).stream()
                    .map(IRI::stringValue)
                    .collect(Collectors.toSet())
    );

    acSpecBuilder.addRequiredSemanticTypes(
            Models.objectIRIs(model.filter(specNode, CLASS, null)).stream()
                    .map(IRI::stringValue)
                    .collect(Collectors.toSet())
    );
    if (specNode.isIRI()) {
      acSpecBuilder.setIRIAsString(specNode.stringValue());
    }
    return acSpecBuilder.build();
  }

  protected Set<Resource> readFormResources(Resource propNode) {
    if (model.contains(propNode, SHACL.HAS_VALUE, null)) {
      return Models.objectResources(model.filter(propNode, SHACL.HAS_VALUE, null));
    }
    if (model.contains(propNode, OR, null)) {
      return Models.objectResource(model.filter(propNode, OR, null))
              .map(this::extractForms)
              .orElse(Set.of());
    }
    throw new InvalidResourceProfileException("An action specification was found with no forms. " +
            "An action specification should have at least one form. ");
  }

  protected AbstractIOSpecification readIOSpecification(Resource node) {
    if (model.contains(node, DATATYPE, null) || model.contains(node, CLASS, null)) {
      return readAbstractValueSpecification(node);
    }

    Optional<Resource> qualifiedValueShapeOpt = Models.objectResource(model.filter(node, QUALIFIED_VALUE_SHAPE, null));
    if (qualifiedValueShapeOpt.isPresent()) {
      Set<IRI> ioTypeSet = Models.objectIRIs(model.filter(qualifiedValueShapeOpt.get(), CLASS, null));

      if (ioTypeSet.contains(RDF.LIST)) {
        return readListSpecification(node);
      } else {
        return readQualifiedValueSpecification(node);
      }
    }

    if (Models.objectIRI(model.filter(node, HAS_VALUE, null)).isPresent()
            || Models.objectIRI(model.filter(node, DEFAULT_VALUE, null)).isPresent()) {
      return readValueSpecification(new ValueSpecification.Builder(), node);
    }

    throw new InvalidResourceProfileException("The shape of an input cannot be recognized; no datatype, " +
            "qualified value shape, value node, or default value node was found.");
  }

  private AbstractValueSpecification readAbstractValueSpecification(Resource node) {

    Set<IRI> datatypeSet = Models.objectIRIs(model.filter(node, DATATYPE, null));
    Set<IRI> classSet = Models.objectIRIs(model.filter(node, CLASS, null));

    // Check for specific datatypes
    for (IRI ioDatatype : datatypeSet) {
      if (XSD.BOOLEAN.equals(ioDatatype)) {
        return readBooleanSpecification(node);
      } else if (XSD.DOUBLE.equals(ioDatatype)) {
        return readDoubleSpecification(node);
      } else if (XSD.FLOAT.equals(ioDatatype)) {
        return readFloatSpecification(node);
      } else if (XSD.INT.equals(ioDatatype) || XSD.INTEGER.equals(ioDatatype)) {
        return readIntegerSpecification(node);
      } else if (XSD.STRING.equals(ioDatatype)) {
        return readStringSpecification(node);
      }
    }

    // Check for specific classes
    for (IRI ioClass : classSet) {
      if (RDF.LIST.equals(ioClass)) {
        return readListValueSpecification(node);
      }
    }

    // If any datatype or class is present, but not matched above, return generic value specification
    if (!datatypeSet.isEmpty() || !classSet.isEmpty()) {
      return readValueSpecification(new ValueSpecification.Builder(), node);
    }

    // If neither datatype nor class is recognized, throw an exception
    throw new InvalidResourceProfileException("The datatype of an input cannot be recognized");
  }

  private AbstractValueSpecification readBooleanSpecification(Resource node) {
    BooleanSpecification.Builder builder = new BooleanSpecification.Builder();

    Models.objectLiteral(model.filter(node, HAS_VALUE, null))
            .ifPresent(literal -> builder.setValue(literal.booleanValue()));

    Models.objectLiteral(model.filter(node, DEFAULT_VALUE, null))
            .ifPresent(literal -> builder.setDefaultValue(literal.booleanValue()));

    return readAbstractValueSpecification(builder, node);
  }

  private AbstractValueSpecification readDoubleSpecification(Resource node) {
    DoubleSpecification.Builder builder = new DoubleSpecification.Builder();

    Models.objectLiteral(model.filter(node, HAS_VALUE, null))
            .ifPresent(literal -> builder.setValue(literal.doubleValue()));

    Models.objectLiteral(model.filter(node, DEFAULT_VALUE, null))
            .ifPresent(literal -> builder.setDefaultValue(literal.doubleValue()));

    return readAbstractValueSpecification(builder, node);
  }

  private AbstractValueSpecification readFloatSpecification(Resource node) {
    FloatSpecification.Builder builder = new FloatSpecification.Builder();

    Models.objectLiteral(model.filter(node, HAS_VALUE, null))
            .ifPresent(literal -> builder.setValue(literal.floatValue()));

    Models.objectLiteral(model.filter(node, DEFAULT_VALUE, null))
            .ifPresent(literal -> builder.setDefaultValue(literal.floatValue()));

    return readAbstractValueSpecification(builder, node);
  }

  private AbstractValueSpecification readIntegerSpecification(Resource node) {
    IntegerSpecification.Builder builder = new IntegerSpecification.Builder();

    Models.objectLiteral(model.filter(node, HAS_VALUE, null))
            .ifPresent(literal -> builder.setValue(literal.intValue()));

    Models.objectLiteral(model.filter(node, DEFAULT_VALUE, null))
            .ifPresent(literal -> builder.setDefaultValue(literal.intValue()));

    return readAbstractValueSpecification(builder, node);
  }

  private AbstractValueSpecification readStringSpecification(Resource node) {
    StringSpecification.Builder builder = new StringSpecification.Builder();

    Models.objectLiteral(model.filter(node, HAS_VALUE, null))
            .ifPresent(literal -> builder.setValue(literal.stringValue()));

    Models.objectLiteral(model.filter(node, DEFAULT_VALUE, null))
            .ifPresent(literal -> builder.setDefaultValue(literal.stringValue()));

    return readAbstractValueSpecification(builder, node);
  }

  private AbstractValueSpecification readListValueSpecification(Resource node) {
    ListSpecification.Builder builder = new ListSpecification.Builder();

    return readValueSpecification(builder, node);
  }

  private AbstractIOSpecification readListSpecification(Resource node) {
    ListSpecification.Builder builder = new ListSpecification.Builder();

    Optional<Literal> minCount = Models.objectLiteral(model.filter(node, QUALIFIED_MIN_COUNT, null));
    if (minCount.isPresent() && minCount.get().intValue() >= 1) {
      builder.setRequired(true);
    }

    Resource baseNode;
    Optional<Resource> qualifiedOp = Models.objectResource(model.filter(node, QUALIFIED_VALUE_SHAPE, null));

    if (qualifiedOp.isPresent()) {
      Resource qualifiedNode = qualifiedOp.get();
      baseNode = qualifiedOp.get();

      // Add required semantic types only for the initial qualified node
      Set<IRI> requiredSemanticTypes = Models.objectIRIs(model.filter(qualifiedNode, CLASS, null));
      for (IRI semanticType : requiredSemanticTypes) {
        builder.addRequiredSemanticType(semanticType.toString());
      }

      while (qualifiedOp.isPresent()) {
        qualifiedNode = qualifiedOp.get();

        // Process property nodes
        Set<Resource> propertyNodes = Models.objectResources(model.filter(qualifiedNode, PROPERTY, null));
        for (Resource propertyNode : propertyNodes) {
          Optional<IRI> pathIRI = Models.objectIRI(model.filter(propertyNode, PATH, null));
          if (pathIRI.isPresent() && RDF.FIRST.equals(pathIRI.get())) {
            AbstractIOSpecification memberSpec = readIOSpecification(propertyNode);
            builder.addMemberSpecification(memberSpec);
          }

          if (pathIRI.isPresent() && RDF.REST.equals(pathIRI.get())) {
            Optional<IRI> valueIRI = Models.objectIRI(model.filter(propertyNode, HAS_VALUE, null));
            if (valueIRI.isPresent() && RDF.NIL.equals(valueIRI.get())) {
              qualifiedOp = Optional.empty(); // Exit loop if RDF.NIL found
            } else {
              qualifiedOp = Models.objectResource(model.filter(propertyNode, QUALIFIED_VALUE_SHAPE, null));
            }
          }
        }
      }
    } else throw new InvalidResourceProfileException("Unrecognized qualified value shape.");

    return (AbstractIOSpecification) readResource(builder, baseNode);
  }


  private AbstractValueSpecification readValueSpecification(ValueSpecification.AbstractBuilder<?, ?> builder, Resource node) {

    Set<IRI> datatypes = Models.objectIRIs(model.filter(node, CLASS, null));

    if (datatypes.size() > 0) {
      datatypes.forEach(type -> builder.addRequiredSemanticType(type.stringValue()));
    }

    Models.objectIRI(model.filter(node, HAS_VALUE, null))
            .ifPresent(builder::setValue);

    Models.objectIRI(model.filter(node, DEFAULT_VALUE, null))
            .ifPresent(builder::setDefaultValue);

    return readAbstractValueSpecification(builder, node);
  }

  private AbstractIOSpecification readQualifiedValueSpecification(Resource node) {

    QualifiedValueSpecification.Builder builder = new QualifiedValueSpecification.Builder();

    Optional<Literal> minCount = Models.objectLiteral(model.filter(node, QUALIFIED_MIN_COUNT, null));
    if (minCount.isPresent() && minCount.get().intValue() >= 1) {
      builder.setRequired(true);
    }

    Optional<Resource> qualifiedOp = Models.objectResource(model.filter(node, QUALIFIED_VALUE_SHAPE, null));

    if (qualifiedOp.isPresent()) {
      Resource qualifiedNode = qualifiedOp.get();

      Set<Resource> propertyNodes = Models.objectResources(model.filter(qualifiedNode, PROPERTY,
              null));
      for (Resource propertyNode : propertyNodes) {

        Optional<IRI> pathIRI = Models.objectIRI(model.filter(propertyNode, PATH, null));
        if (pathIRI.isEmpty()) {
          throw new InvalidResourceProfileException("Found a property shape without specified path.");
        }
        AbstractIOSpecification propertySpec = readIOSpecification(propertyNode);
        builder.addPropertySpecification(pathIRI.get().stringValue(), propertySpec);
      }

      Set<IRI> requiredSemanticTypes = Models.objectIRIs(model.filter(qualifiedNode, CLASS,
              null));
      for (IRI semanticType : requiredSemanticTypes) {
        builder.addRequiredSemanticType(semanticType.toString());
      }
    } else throw new InvalidResourceProfileException("Unrecognized qualified value shape.");

    return (AbstractIOSpecification) readResource(builder, qualifiedOp.get());
  }

  private AbstractValueSpecification readAbstractValueSpecification(AbstractValueSpecification.AbstractBuilder<?, ?> builder, Resource node) {

    Models.objectLiteral(model.filter(node, NAME, null))
            .ifPresent(literal -> builder.setName(literal.stringValue()));

    Models.objectLiteral(model.filter(node, DESCRIPTION, null))
            .ifPresent(literal -> builder.setDescription(literal.stringValue()));

    Models.objectLiteral(model.filter(node, ORDER, null))
            .ifPresent(order -> builder.setOrder(order.intValue()));

    Optional<Literal> minCount = Models.objectLiteral(model.filter(node, MIN_COUNT, null));
    if (minCount.isPresent() && minCount.get().intValue() >= 1) {
      builder.setRequired(true);
    }

    return (AbstractValueSpecification) readResource(builder, node);
  }

  private Group readGroup(Resource groupNode) {
    Group.Builder builder = new Group.Builder();
    if (groupNode.isIRI()) {
      builder.setIRIAsString(groupNode.stringValue());
    }
    Models.objectLiteral(model.filter(groupNode, RDFS.LABEL, null))
            .ifPresent(literal -> builder.setLabel(literal.stringValue()));
    Models.objectLiteral(model.filter(groupNode, ORDER, null))
            .ifPresent(literal -> builder.setOrder(literal.intValue()));
    return builder.build();
  }

  private Set<Resource> extractForms(Resource node) {
    List<Value> values = RDFCollections.asValues(model, node, new ArrayList<>());
    return values.stream()
            .map(r -> Models.objectResource(model.filter((Resource) r, SHACL.HAS_VALUE, null)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
  }

  private Boolean isForm(Resource node) {
    return model.contains(node, PATH, PROV.USED);
  }

  private Boolean isInput(Resource node) {
    return model.contains(node, PATH, HAS_INPUT);
  }

  private Boolean isOutput(Resource node) {
    return model.contains(node, PATH, HAS_OUTPUT);
  }

  protected Set<Form> readForms(Set<Resource> formNodes) {
    Set<Form> forms = new HashSet<>();
    for (Resource formNode : formNodes) {
      Optional<IRI> target = Models.objectIRI(model.filter(formNode, HCTL.HAS_TARGET, null));

      if (target.isPresent()) {
        Form.Builder builder = new Form.Builder(target.get().stringValue());

        Models.objectLiteral(model.filter(formNode, HTV.METHOD_NAME, null))
                .ifPresent(literal -> builder.setMethodName(literal.stringValue()));
        Models.objectLiteral(model.filter(formNode, HCTL.FOR_CONTENT_TYPE, null))
                .ifPresent(literal -> builder.setContentType(literal.stringValue()));
        if (formNode.isIRI()) {
          builder.setIRIAsString(formNode.stringValue());
        }

        forms.add(builder.build());

      } else {
        throw new InvalidResourceProfileException("A form was found but its submission target is missing. ");
      }
    }

    return forms;
  }
}
