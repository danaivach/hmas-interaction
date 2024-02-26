package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.io.BaseResourceProfileGraphReader;
import ch.unisg.ics.interactions.hmas.core.io.InvalidResourceProfileException;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.*;
import io.vavr.control.Either;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.*;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.*;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL.*;

public class ResourceProfileGraphReader extends BaseResourceProfileGraphReader {

  protected ResourceProfileGraphReader(RDFFormat format, String representation) {
    super(format, representation);
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
    if (profileIRI.isPresent()) {
      artifactBuilder.setIRI(profileIRI.get());
    }

    return artifactBuilder.build();
  }

  public static Model getModelFromString(String representation) {
    ResourceProfileGraphReader reader = new ResourceProfileGraphReader(RDFFormat.TURTLE, representation);
    return reader.getModel();
  }

  protected final Artifact readOwnerResource() {
    Optional<Resource> node = Models.objectResource(model.filter(profileIRI, IS_PROFILE_OF, null));
    if (node.isPresent()) {
      return readResource(node.get());
    }
    throw new InvalidResourceProfileException("An artifact profile must describe an artifact.");
  }

  protected final Artifact readResource(Resource node) {

    Set<IRI> types = Models.objectIRIs(model.filter(node, RDF.TYPE, null));

    if (types.contains(ARTIFACT) || types.contains(AGENT_BODY)) {
      return readArtifact(node);
    }
    throw new InvalidResourceProfileException("Unknown type of profiled resource. " +
            "Supported resource types: Artifact, AgentBody.");
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
        signifiers.add(builder.build());
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
    propNodeInput.ifPresent(input -> acSpecBuilder.setRequiredInput(readInput(input)));
    propNodeOutput.ifPresent(output -> acSpecBuilder.setRequiredOutput(readOutput(output)));
    acSpecBuilder.setRequiredSemanticTypes(
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

  protected InputSpecification readInput(Resource node) {
    System.out.println("in input");
    InputSpecification.Builder builder = new InputSpecification.Builder();
    return (InputSpecification) readIOSpecification(builder, node, "in");
  }

  protected OutputSpecification readOutput(Resource node) {
    OutputSpecification.Builder builder = new OutputSpecification.Builder();
    return (OutputSpecification) readIOSpecification(builder, node, "out");
  }

  protected AbstractIOSpecification readIOSpecification(AbstractIOSpecification.AbstractBuilder<?, ?> builder, Resource propNode, String io) {
    if (model.contains(propNode, QUALIFIED_VALUE_SHAPE, null)) {
      System.out.println("there is a qualified value shape");
      return readNestedIOSpecification(builder,
              Models.objectResource(model.filter(propNode, QUALIFIED_VALUE_SHAPE, null)).orElseThrow(() ->
                      new InvalidResourceProfileException("Invalid action property (input/output) shape. " + propNode.toString())
              ),
              propNode,
              io
      );
    }
    return readNestedIOSpecification(builder, null, propNode, io);
  }

  protected AbstractIOSpecification readNestedIOSpecification(AbstractIOSpecification.AbstractBuilder<?, ?> builder, Resource baseNode, Resource propNode, String io) {

    builder.setRequiredSemanticTypes(
            Models.objectIRIs(model.filter(baseNode, CLASS, null)).stream()
                    .peek(iri -> System.out.println("IRI stringValue: " + iri.stringValue()))  // Print statement added
                    .map(IRI::stringValue)
                    .collect(Collectors.toSet())
    );

    Models.objectLiteral(model.filter(baseNode, NAME, null))
            .ifPresent(literal -> builder.setName(literal.stringValue()));
    Models.objectLiteral(model.filter(baseNode, DESCRIPTION, null))
            .ifPresent(literal -> builder.setDescription(literal.stringValue()));
    Models.objectIRI(model.filter(propNode, PATH, null))
            .ifPresent(path -> builder.setPath(path.stringValue()));
    Models.objectLiteral(model.filter(propNode, MIN_COUNT, null))
            .ifPresent(minCount -> builder.setMinCount(minCount.intValue()));
    Models.objectLiteral(model.filter(propNode, MAX_COUNT, null))
            .ifPresent(maxCount -> builder.setMaxCount(maxCount.intValue()));
    Models.objectLiteral(model.filter(propNode, QUALIFIED_MIN_COUNT, null))
            .ifPresent(count -> builder.setQualifiedMinCount(count.intValue()));
    Models.objectLiteral(model.filter(propNode, QUALIFIED_MAX_COUNT, null))
            .ifPresent(count -> builder.setQualifiedMaxCount(count.intValue()));
    Models.objectIRI(model.filter(propNode, QUALIFIED_VALUE_SHAPE, null))
            .ifPresent(qualifiedValueShape -> builder.setQualifiedValueShape(qualifiedValueShape.stringValue()));
    Models.objectResource(model.filter(propNode, GROUP, null))
            .ifPresent(groupResource -> builder.setGroup(readGroup(groupResource)));
    Models.objectLiteral(model.filter(propNode, ORDER, null))
            .ifPresent(order -> builder.setOrder(order.intValue()));
    Models.objectIRI(model.filter(propNode, DATATYPE, null))
            .ifPresent(dataType -> builder.setDataType(dataType.stringValue()));
    Models.objectLiteral(model.filter(baseNode, MIN_INCLUSIVE, null))
            .ifPresent(literal -> builder.setMinInclusive(literal.doubleValue()));
    Models.objectLiteral(model.filter(baseNode, MAX_INCLUSIVE, null))
            .ifPresent(literal -> builder.setMaxInclusive(literal.doubleValue()));
    Models.objectLiteral(model.filter(baseNode, DEFAULT_VALUE, null))
            .ifPresentOrElse(
                    literal -> builder.setDefaultValue(Either.left(literal.doubleValue())),
                    () -> Models.objectResource(model.filter(baseNode, DEFAULT_VALUE, null))
                            .flatMap(resource -> Models.objectIRI(model.filter(resource, NODE, null)))
                            .ifPresent(iri -> builder.setDefaultValue(Either.right(iri.stringValue())))
            );

    if ("in".equals(io)) {
      Models.objectResources(model.filter(Optional.ofNullable(baseNode).orElse(propNode), PROPERTY, null))
              .forEach(node -> ((InputSpecification.Builder) builder).setInput(readInput(node)));
    } else if ("out".equals(io)) {
      Models.objectResources(model.filter(Optional.ofNullable(baseNode).orElse(propNode), PROPERTY, null))
              .forEach(node -> ((OutputSpecification.Builder) builder).setOutput(readOutput(node)));
    }


    return builder.build();
  }

  private Group readGroup(Resource groupNode) {
    Group.Builder builder = new Group.Builder();
    if (groupNode.isIRI()) {
      builder.setIRIAsString(groupNode.stringValue());
    }
    Models.objectLiteral(model.filter(groupNode, RDFS.LABEL, null))
            .ifPresent(literal -> builder.setLabel(literal.stringValue()));
    Models.objectLiteral(model.filter(groupNode, RDFS.COMMENT, null))
            .ifPresent(literal -> builder.setComment(literal.stringValue()));
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
