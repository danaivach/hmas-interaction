package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.shapes.*;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.PROV;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArtifactProfileGraphWriterTest {

  private final static Logger LOGGER = Logger.getLogger(ArtifactProfileGraphWriterTest.class.getCanonicalName());

  private static final String PREFIXES =
          "@prefix hmas: <" + CORE.NAMESPACE + "> .\n" +
                  "@prefix hctl: <" + HCTL.NAMESPACE + "> .\n" +
                  "@prefix prs: <http://example.org/prs#> .\n" +
                  "@prefix prov: <" + PROV.NAMESPACE + "> .\n" +
                  "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
                  "@prefix sh: <" + SHACL.NAMESPACE + ">";

  private static final String FORM_IRI = "<urn:3g6lpq9v>";
  private static final String SECOND_FORM_IRI = "<urn:x7aym3hn>";
  private static final String TARGET = "https://example.org/resource";
  private static final Form BASIC_FORM = new Form.Builder(TARGET)
          .setIRIAsString("urn:3g6lpq9v").build();
  private static final String BASE_URI = "http://example.org/";

  private static Model readModelFromString(String profile, String baseURI)
          throws RDFParseException, RDFHandlerException, IOException {
    StringReader stringReader = new StringReader(profile);

    RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
    Model model = new LinkedHashModel();
    rdfParser.setRDFHandler(new StatementCollector(model));

    rdfParser.parse(stringReader, baseURI);

    return model;
  }

  // Compare two RDF models
  private static void compareModels(Model modelA, Model modelB) {
    // Iterate through the triples in modelA
    for (Statement stmtA : modelA) {
      // Check if the same triple exists in modelB
      if (!modelB.contains(stmtA)) {
        // Triple in modelA does not exist in modelB
        System.out.println("Triple in modelA but not in modelB: " + stmtA);
      }
    }

    // Iterate through the triples in modelB
    for (Statement stmtB : modelB) {
      // Check if the same triple exists in modelA
      if (!modelA.contains(stmtB)) {
        // Triple in modelB does not exist in modelA
        System.out.println("Triple in modelB but not in modelA: " + stmtB);
      }
    }
  }

  @Test
  public void testWriteArtifactProfileWithSignifier() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier, <http://example.org/signifier-type-1> ,\n" +
            "    <http://example.org/signifier-type-2> , <http://example.org/signifier-type-3> ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount \"1\"^^xs:int;\n" +
            "          sh:maxCount \"1\"^^xs:int;\n" +
            "          sh:hasValue " + FORM_IRI + " ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "  hctl:forContentType \"application/json\" ;\n" +
            "  hctl:hasTarget <https://example.org/resource> .";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    Signifier signifier = new Signifier.Builder(actionSpec)
            .addSemanticType("http://example.org/signifier-type-1")
            .addSemanticTypes(Set.of("http://example.org/signifier-type-2",
                    "http://example.org/signifier-type-3"))
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(signifier)
                    .build();

    assertEquals(4, signifier.getSemanticTypes().size());

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithFormBNode() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount \"1\"^^xs:int;\n" +
            "          sh:maxCount \"1\"^^xs:int;\n" +
            "          sh:hasValue [ a hctl:Form ; \n" +
            "             hctl:forContentType \"application/json\" ;\n" +
            "             hctl:hasTarget <https://example.org/resource> ] ] ] ].";

    Form form = new Form.Builder(TARGET).build();
    ActionSpecification actionSpec = new ActionSpecification.Builder(form).build();

    Signifier signifier = new Signifier.Builder(actionSpec)
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(signifier)
                    .build();

    assertEquals(1, signifier.getSemanticTypes().size());

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithSignifiersWithIRI() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier <urn:signifier-1>, <urn:signifier-2>.\n" +
            "<urn:signifier-1> a hmas:Signifier ;\n" +
            "   hmas:signifies [ a sh:NodeShape; ;\n" +
            "     sh:class hmas:ActionExecution ;\n" +
            "     sh:property [\n" +
            "       sh:path prov:used ;\n" +
            "       sh:minCount \"1\"^^xs:int;\n" +
            "       sh:maxCount \"1\"^^xs:int;\n" +
            "       sh:hasValue " + FORM_IRI + " ;\n" +
            "     ]\n" +
            "   ]. \n" +
            "<urn:signifier-2> a hmas:Signifier ;\n" +
            "   hmas:signifies [ a sh:NodeShape ;\n" +
            "     sh:class hmas:ActionExecution ;\n" +
            "     sh:property [\n" +
            "       sh:path prov:used ;\n" +
            "       sh:minCount \"1\"^^xs:int;\n" +
            "       sh:maxCount \"1\"^^xs:int;\n" +
            "       sh:hasValue " + FORM_IRI + " ;\n" +
            "     ]\n" +
            "   ]. \n" +
            FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "  hctl:forContentType \"application/json\" ;\n" +
            "  hctl:hasTarget <https://example.org/resource> .";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    Signifier signifier1 =
            new Signifier.Builder(actionSpec)
                    .setIRI(SimpleValueFactory.getInstance().createIRI("urn:signifier-1"))
                    .build();

    Signifier signifier2 =
            new Signifier.Builder(actionSpec)
                    .setIRIAsString("urn:signifier-2")
                    .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(signifier1)
                    .exposeSignifier(signifier2)
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteSignifierWithAbilities() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount \"1\"^^xs:int;\n" +
            "          sh:maxCount \"1\"^^xs:int;\n" +
            "          sh:hasValue " + FORM_IRI + " ;\n" +
            "      ]\n" +
            "   ];\n" +
            "   hmas:recommendsAbility [ a hmas:Ability, prs:PRSAbility ]\n" +
            "]. \n" +
            FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "  hctl:forContentType \"application/json\" ;\n" +
            "   hctl:hasTarget <https://example.org/resource> .";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec)
                            .addRecommendedAbility(new Ability.Builder()
                                    .addSemanticType("http://example.org/prs#PRSAbility")
                                    .addSemanticType(INTERACTION.ABILITY.stringValue())
                                    .build())
                            .build())
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteAgentBodyProfile() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<http://example.org/profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <http://example.org/profile#agent-body>.\n" +
            "<http://example.org/profile#agent-body> a hmas:Artifact.";

    AgentBody body = new AgentBody.Builder().setIRIAsString("http://example.org/profile#agent-body").build();

    ResourceProfile profile =
            new ResourceProfile.Builder(body)
                    .setIRIAsString("http://example.org/profile")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileMultipleForms() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount \"1\"^^xs:int;\n" +
            "          sh:maxCount \"1\"^^xs:int;\n" +
            "          sh:or (\n" +
            "             [ sh:hasValue " + FORM_IRI + " ]\n" +
            "             [ sh:hasValue " + SECOND_FORM_IRI + " ]\n" +
            "         ) ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "  hctl:forContentType \"application/json\" ;\n" +
            "   hctl:hasTarget <https://example.org/resource> .\n" +
            SECOND_FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "  hctl:forContentType \"application/json\" ;\n" +
            "   hctl:hasTarget <coaps://example.org/resource> .";

    Form coapForm = new Form.Builder("coaps://example.org/resource")
            .setIRIAsString(SECOND_FORM_IRI).build();

    Set<Form> forms = new HashSet<>(Arrays.asList(coapForm, BASIC_FORM));

    ActionSpecification actionSpec = new ActionSpecification.Builder(forms).build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec).build())
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileMultipleFormsBNode() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount \"1\"^^xs:int;\n" +
            "          sh:maxCount \"1\"^^xs:int;\n" +
            "          sh:or (\n" +
            "             [ sh:hasValue " + FORM_IRI + " ]\n" +
            "             [ sh:hasValue [ a hctl:Form ; \n" +
            "                 hctl:forContentType \"application/json\" ;\n" +
            "                 hctl:hasTarget <coaps://example.org/resource> ] ]\n" +
            "         ) \n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            FORM_IRI + " a hctl:Form ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "   hctl:hasTarget <https://example.org/resource> .";

    Form coapForm = new Form.Builder("coaps://example.org/resource").build();

    Set<Form> forms = new HashSet<>(Arrays.asList(coapForm, BASIC_FORM));

    ActionSpecification actionSpec = new ActionSpecification.Builder(forms).build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec).build())
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithBooleanInput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "              hmas:isProfileOf [ a hmas:Artifact ] ;\n" +
            "              hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier ;\n" +
            "             hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape ;\n" +
            "         sh:class hmas:ActionExecution ;\n" +
            "         sh:property [\n" +
            "             sh:path prov:used ;\n" +
            "             sh:minCount \"1\"^^xs:int;\n" +
            "             sh:maxCount \"1\"^^xs:int ;\n" +
            "             sh:hasValue ex:httpForm\n" +
            "         ] ;\n" +
            "         sh:property [ a sh:Shape, ex:ExampleIOSpecification ; \n" +
            "         sh:path hmas:hasInput;\n" +
            "         sh:datatype xs:boolean ;\n" +
            "         sh:name \"Label\" ;\n" +
            "         sh:description \"Description\" ;\n" +
            "         sh:order \"5\"^^xs:int;\n" +
            "         sh:minCount \"1\"^^xs:int;\n" +
            "         sh:maxCount \"1\"^^xs:int;\n" +
            "         sh:hasValue true ;\n" +
            "         sh:defaultValue true ;\n" +
            "         ] ." +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" ." +
            "\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("PUT")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();

    BooleanSpecification booleanSpec = new BooleanSpecification.Builder()
            .addSemanticType("http://example.org/ExampleIOSpecification")
            .setName("Label")
            .setDescription("Description")
            .setOrder(5)
            .setValue(true)
            .setDefaultValue(true)
            .setRequired(true)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .setInputSpecification(booleanSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithDoubleInput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "              hmas:isProfileOf [ a hmas:Artifact ] ;\n" +
            "              hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier ;\n" +
            "             hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape ;\n" +
            "         sh:class hmas:ActionExecution ;\n" +
            "         sh:property [\n" +
            "             sh:path prov:used ;\n" +
            "             sh:minCount \"1\"^^xs:int;\n" +
            "             sh:maxCount \"1\"^^xs:int ;\n" +
            "             sh:hasValue ex:httpForm\n" +
            "         ] ;\n" +
            "         sh:property [ a sh:Shape ; \n" +
            "         sh:path hmas:hasInput;\n" +
            "         sh:datatype xs:double ;\n" +
            "         sh:name \"Label\" ;\n" +
            "         sh:description \"Description\" ;\n" +
            "         sh:order \"5\"^^xs:int;\n" +
            "         sh:minCount \"1\"^^xs:int;\n" +
            "         sh:maxCount \"1\"^^xs:int;\n" +
            "         sh:hasValue 1.055E1 ;\n" +
            "         sh:defaultValue 1.055E1 ;\n" +
            "         ] ." +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" ." +
            "\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("PUT")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();

    DoubleSpecification doubleSpec = new DoubleSpecification.Builder()
            .setName("Label")
            .setDescription("Description")
            .setOrder(5)
            .setValue(10.55)
            .setDefaultValue(10.55)
            .setRequired(true)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .setInputSpecification(doubleSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithFloatInput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "              hmas:isProfileOf [ a hmas:Artifact ] ;\n" +
            "              hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier ;\n" +
            "             hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape ;\n" +
            "         sh:class hmas:ActionExecution ;\n" +
            "         sh:property [\n" +
            "             sh:path prov:used ;\n" +
            "             sh:minCount \"1\"^^xs:int;\n" +
            "             sh:maxCount \"1\"^^xs:int ;\n" +
            "             sh:hasValue ex:httpForm\n" +
            "         ] ;\n" +
            "         sh:property [ a sh:Shape ; \n" +
            "         sh:path hmas:hasInput;\n" +
            "         sh:datatype xs:float ;\n" +
            "         sh:name \"Label\" ;\n" +
            "         sh:description \"Description\" ;\n" +
            "         sh:order \"5\"^^xs:int;\n" +
            "         sh:minCount \"1\"^^xs:int;\n" +
            "         sh:maxCount \"1\"^^xs:int;\n" +
            "         sh:hasValue \"1.5\"^^xs:float ;\n" +
            "         sh:defaultValue \"1.5\"^^xs:float ;\n" +
            "         ] ." +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" ." +
            "\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("PUT")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();

    FloatSpecification floatSpec = new FloatSpecification.Builder()
            .setName("Label")
            .setDescription("Description")
            .setOrder(5)
            .setValue(1.5f)
            .setDefaultValue(1.5f)
            .setRequired(true)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .setInputSpecification(floatSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithIntegerOutput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "              hmas:isProfileOf [ a hmas:Artifact ] ;\n" +
            "              hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier ;\n" +
            "             hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape ;\n" +
            "         sh:class hmas:ActionExecution ;\n" +
            "         sh:property [\n" +
            "             sh:path prov:used ;\n" +
            "             sh:minCount \"1\"^^xs:int;\n" +
            "             sh:maxCount \"1\"^^xs:int ;\n" +
            "             sh:hasValue ex:httpForm\n" +
            "         ] ;\n" +
            "         sh:property [ a sh:Shape ; \n" +
            "         sh:path hmas:hasOutput;\n" +
            "         sh:datatype xs:int ;\n" +
            "         sh:name \"Label\" ;\n" +
            "         sh:description \"Description\" ;\n" +
            "         sh:order \"5\"^^xs:int;\n" +
            "         sh:minCount \"1\"^^xs:int;\n" +
            "         sh:maxCount \"1\"^^xs:int;\n" +
            "         sh:hasValue \"1\"^^xs:int ;\n" +
            "         sh:defaultValue \"1\"^^xs:int ;\n" +
            "         ] ." +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" ." +
            "\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("PUT")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();

    IntegerSpecification integerSpec = new IntegerSpecification.Builder()
            .setName("Label")
            .setDescription("Description")
            .setOrder(5)
            .setValue(1)
            .setDefaultValue(1)
            .setRequired(true)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .setOutputSpecification(integerSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithStringOutput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "              hmas:isProfileOf [ a hmas:Artifact ] ;\n" +
            "              hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier ;\n" +
            "             hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape ;\n" +
            "         sh:class hmas:ActionExecution ;\n" +
            "         sh:property [\n" +
            "             sh:path prov:used ;\n" +
            "             sh:minCount \"1\"^^xs:int;\n" +
            "             sh:maxCount \"1\"^^xs:int ;\n" +
            "             sh:hasValue ex:httpForm\n" +
            "         ] ;\n" +
            "         sh:property [ a sh:Shape ; \n" +
            "         sh:path hmas:hasOutput;\n" +
            "         sh:datatype xs:string ;\n" +
            "         sh:name \"Label\" ;\n" +
            "         sh:description \"Description\" ;\n" +
            "         sh:order \"5\"^^xs:int;\n" +
            "         sh:minCount \"1\"^^xs:int;\n" +
            "         sh:maxCount \"1\"^^xs:int;\n" +
            "         sh:hasValue \"string\"^^xs:string ;\n" +
            "         sh:defaultValue \"string\"^^xs:string ;\n" +
            "         ] ." +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" ." +
            "\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("PUT")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();

    StringSpecification stringSpec = new StringSpecification.Builder()
            .setName("Label")
            .setDescription("Description")
            .setOrder(5)
            .setValue("string")
            .setDefaultValue("string")
            .setRequired(true)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .setOutputSpecification(stringSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithValueOutput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "              hmas:isProfileOf [ a hmas:Artifact ] ;\n" +
            "              hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier ;\n" +
            "             hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape ;\n" +
            "         sh:class hmas:ActionExecution ;\n" +
            "         sh:property [\n" +
            "             sh:path prov:used ;\n" +
            "             sh:minCount \"1\"^^xs:int;\n" +
            "             sh:maxCount \"1\"^^xs:int ;\n" +
            "             sh:hasValue ex:httpForm\n" +
            "         ] ;\n" +
            "         sh:property [ a sh:Shape ; \n" +
            "         sh:path hmas:hasOutput;\n" +
            "         sh:datatype hmas:ResourceProfile, ex:ExampleDatatype ;\n" +
            "         sh:name \"Label\" ;\n" +
            "         sh:description \"Description\" ;\n" +
            "         sh:order \"5\"^^xs:int;\n" +
            "         sh:maxCount \"1\"^^xs:int;\n" +
            "         sh:hasValue ex:exampleNode ;\n" +
            "         sh:defaultValue ex:exampleNode ;\n" +
            "         ] ." +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" ." +
            "\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("PUT")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();

    ValueSpecification valueSpec = new ValueSpecification.Builder()
            .addRequiredSemanticType(CORE.TERM.RESOURCE_PROFILE.toString())
            .addRequiredSemanticType("http://example.org/ExampleDatatype")
            .setName("Label")
            .setDescription("Description")
            .setOrder(5)
            .setValueAsString("http://example.org/exampleNode")
            .setDefaultValueAsString("http://example.org/exampleNode")
            .setRequired(false)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .setOutputSpecification(valueSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithQualifiedValueInput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "@prefix saref: <https://saref.etsi.org/core/v3.1.1/> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "              hmas:isProfileOf [ a hmas:Artifact ] ;\n" +
            "              hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier, ex:ExampleSignifier ;\n" +
            "             hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape, ex:ExampleSpecification ;\n" +
            "         sh:class hmas:ActionExecution, ex:ExampleActionExecution ;\n" +
            "         sh:property [ \n" +
            "             sh:path prov:used ;\n" +
            "             sh:minCount \"1\"^^xs:int;\n" +
            "             sh:maxCount \"1\"^^xs:int ;\n" +
            "             sh:hasValue ex:httpForm\n" +
            "         ] ;\n" +
            "         sh:property [  \n" +
            "             sh:path hmas:hasInput;\n" +
            "             sh:qualifiedValueShape ex:gripperJointShape ;\n" +
            "             sh:qualifiedMinCount \"1\"^^xs:int ;\n" +
            "             sh:qualifiedMaxCount \"1\"^^xs:int\n" +
            "         ] ." +
            "\n" +
            "ex:httpForm a hctl:Form, ex:ExampleForm ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" ." +
            "\n" +
            "ex:gripperJointShape a sh:Shape, <http://example.org/ExampleQualifiedValueSpecification>   ;\n" +
            "  sh:class ex:GripperJoint, saref:State ;\n" +
            "  sh:property [ a sh:Shape, <http://example.org/ExampleValueSpecification> ; \n" +
            "    sh:path ex:hasGripperValue ;\n" +
            "    sh:minCount \"1\"^^xs:int;\n" +
            "    sh:maxCount \"1\"^^xs:int;\n" +
            "    sh:hasValue \"1\"^^xs:int;\n" +
            "    sh:datatype xs:int\n" +
            "  ] .\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .addSemanticType("http://example.org/ExampleForm")
            .setMethodName("PUT")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();


    QualifiedValueSpecification gripperJointInput = new QualifiedValueSpecification.Builder()
            .addSemanticType("http://example.org/ExampleQualifiedValueSpecification")
            .addRequiredSemanticType("http://example.org/GripperJoint")
            .addRequiredSemanticType("https://saref.etsi.org/core/v3.1.1/State")
            .setIRIAsString("http://example.org/gripperJointShape")
            .setRequired(true)
            .addPropertySpecification("http://example.org/hasGripperValue",
                    new IntegerSpecification.Builder()
                            .addSemanticType("http://example.org/ExampleValueSpecification")
                            .setRequired(true)
                            .setValue(1)
                            .build())
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .addSemanticType("http://example.org/ExampleSpecification")
            .addRequiredSemanticTypes(Set.of("http://example.org/ExampleActionExecution"))
            .setInputSpecification(gripperJointInput)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .addSemanticType("http://example.org/ExampleSignifier")
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithListQualifiedValueOutput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix saref: <https://saref.etsi.org/core/> .\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile;\n" +
            "  hmas:exposesSignifier ex:signifier;\n" +
            "  hmas:isProfileOf [ a hmas:Artifact\n" +
            "    ] .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier, ex:ExampleSignifier;\n" +
            "  hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape;\n" +
            "  sh:class hmas:ActionExecution, ex:ExampleActionExecution;\n" +
            "  sh:property [\n" +
            "      sh:path prov:used;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:hasValue ex:httpForm\n" +
            "    ], [\n" +
            "      sh:qualifiedValueShape ex:listShape;\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path hmas:hasOutput\n" +
            "    ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper>;\n" +
            "  htv:methodName \"GET\";\n" +
            "  hctl:forContentType \"application/json\" .\n" +
            "\n" +
            "ex:listShape a sh:Shape, ex:ExampleListSpecification;\n" +
            "  sh:class saref:State, rdf:List;\n" +
            "  sh:property [\n" +
            "      sh:qualifiedValueShape ex:restListShape;\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:rest\n" +
            "    ], [ a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "      sh:datatype xs:int;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:first\n" +
            "    ] .\n" +
            "\n" +
            "ex:restListShape a sh:Shape, ex:ExampleListSpecification;\n" +
            "  sh:class saref:State, rdf:List;\n" +
            "  sh:property [ a sh:Shape, ex:ExampleRestSpecification;\n" +
            "      sh:hasValue rdf:nil;\n" +
            "      sh:datatype xs:anyURI;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:rest\n" +
            "    ], [ a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "      sh:datatype xs:int;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:first\n" +
            "    ] .";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("GET")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();


    IntegerSpecification memberSpec = new IntegerSpecification.Builder()
            .addSemanticType("http://example.org/ExampleFirstSpecification")
            .setRequired(true)
            .build();

    ValueSpecification nilSpec = new ValueSpecification.Builder()
            .addSemanticType("http://example.org/ExampleRestSpecification")
            .setValueAsString("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil")
            .setRequired(true)
            .build();

    QualifiedValueSpecification listSpecLast = new QualifiedValueSpecification.Builder()
            .setIRIAsString("http://example.org/restListShape")
            .addSemanticType("http://example.org/ExampleListSpecification")
            .addRequiredSemanticType("http://www.w3.org/1999/02/22-rdf-syntax-ns#List")
            .addRequiredSemanticType("https://saref.etsi.org/core/State")
            .setRequired(true)
            .addPropertySpecification("http://www.w3.org/1999/02/22-rdf-syntax-ns#first", memberSpec)
            .addPropertySpecification("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest", nilSpec)
            .build();

    QualifiedValueSpecification listSpec = new QualifiedValueSpecification.Builder()
            .setIRIAsString("http://example.org/listShape")
            .addSemanticType("http://example.org/ExampleListSpecification")
            .addRequiredSemanticType("http://www.w3.org/1999/02/22-rdf-syntax-ns#List")
            .addRequiredSemanticType("https://saref.etsi.org/core/State")
            .setRequired(true)
            .addPropertySpecification("http://www.w3.org/1999/02/22-rdf-syntax-ns#first", memberSpec)
            .addPropertySpecification("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest", listSpecLast)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .addRequiredSemanticTypes(Set.of("http://example.org/ExampleActionExecution"))
            .setOutputSpecification(listSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .addSemanticType("http://example.org/ExampleSignifier")
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithListOutput() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix saref: <https://saref.etsi.org/core/> .\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile;\n" +
            "  hmas:exposesSignifier ex:signifier;\n" +
            "  hmas:isProfileOf [ a hmas:Artifact\n" +
            "    ] .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier, ex:ExampleSignifier;\n" +
            "  hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape;\n" +
            "  sh:class hmas:ActionExecution, ex:ExampleActionExecution;\n" +
            "  sh:property [\n" +
            "      sh:path prov:used;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:hasValue ex:httpForm\n" +
            "    ], [\n" +
            "      sh:qualifiedValueShape ex:listShape;\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path hmas:hasOutput\n" +
            "    ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper>;\n" +
            "  htv:methodName \"GET\";\n" +
            "  hctl:forContentType \"application/json\" .\n" +
            "\n" +
            "ex:listShape a sh:Shape, ex:ExampleListSpecification;\n" +
            "  sh:class saref:State, rdf:List;\n" +
            "  sh:property [\n" +
            "      sh:qualifiedValueShape [" +
            "         a sh:Shape, ex:ExampleListSpecification;\n" +
            "         sh:class saref:State, rdf:List;\n" +
            "         sh:property [ a sh:Shape ;\n" +
            "           sh:hasValue rdf:nil;\n" +
            "           sh:datatype xs:anyURI;\n" +
            "           sh:minCount \"1\"^^xs:int;\n" +
            "           sh:maxCount \"1\"^^xs:int;\n" +
            "           sh:path rdf:rest\n" +
            "         ], [ a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "           sh:datatype xs:int;\n" +
            "           sh:minCount \"1\"^^xs:int;\n" +
            "           sh:maxCount \"1\"^^xs:int;\n" +
            "           sh:path rdf:first\n" +
            "         ]\n" +
            "      ];\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:rest\n" +
            "    ], [ a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "      sh:datatype xs:int;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:first\n" +
            "    ] .\n";

    Form httpForm = new Form.Builder("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper")
            .setMethodName("GET")
            .setContentType("application/json")
            .setIRIAsString("http://example.org/httpForm")
            .build();

    IntegerSpecification memberSpec = new IntegerSpecification.Builder()
            .addSemanticType("http://example.org/ExampleFirstSpecification")
            .setRequired(true)
            .build();

    ListSpecification listSpec = new ListSpecification.Builder()
            .setIRIAsString("http://example.org/listShape")
            .addSemanticType("http://example.org/ExampleListSpecification")
            .addRequiredSemanticType("http://www.w3.org/1999/02/22-rdf-syntax-ns#List")
            .addRequiredSemanticType("https://saref.etsi.org/core/State")
            .setRequired(true)
            .addMemberSpecification(memberSpec)
            .addMemberSpecification(memberSpec)
            .build();

    ActionSpecification moveGripperSpec = new ActionSpecification.Builder(httpForm)
            .addRequiredSemanticTypes(Set.of("http://example.org/ExampleActionExecution"))
            .setOutputSpecification(listSpec)
            .setIRIAsString("http://example.org/moveGripperSpecification")
            .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(
                            new Signifier.Builder(moveGripperSpec)
                                    .addSemanticType("http://example.org/ExampleSignifier")
                                    .setIRIAsString("http://example.org/signifier")
                                    .build()
                    )
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteArtifactProfileWithContext() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:recommendsContext ex:situationShape ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount \"1\"^^xs:int;\n" +
            "          sh:maxCount \"1\"^^xs:int;\n" +
            "          sh:hasValue " + FORM_IRI + " ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "  hctl:forContentType \"application/json\" ;\n" +
            "  hctl:hasTarget <https://example.org/resource> . \n" +
            "\n" +
            "ex:situationShape a sh:NodeShape ;\n" +
            "  sh:class hmas:ResourceProfile ;\n" +
            "  sh:property [\n" +
            "    sh:path hmas:isProfileOf ;\n" +
            "    sh:qualifiedValueShape ex:agentShape ;\n" +
            "    sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "    sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "  ] .\n" +
            "\n" +
            "ex:agentShape a sh:NodeShape ;\n" +
            "  sh:class hmas:Agent ;\n" +
            "  sh:property [\n" +
            "    sh:path prs:hasBelief ;\n" +
            "    sh:minCount \"1\"^^xs:int;\n" +
            "    sh:maxCount \"1\"^^xs:int;\n" +
            "    sh:hasValue \"room(empty)\"\n" +
            "  ] .";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    SimpleValueFactory rdfFactory = SimpleValueFactory.getInstance();
    IRI situationShapeIRI = rdfFactory.createIRI("http://example.org/situationShape");
    IRI agentShapeIRI = rdfFactory.createIRI("http://example.org/agentShape");
    IRI beliefPropIRI = rdfFactory.createIRI("http://example.org/prs#hasBelief");
    BNode profileProperty = Values.bnode();
    BNode agentProperty = Values.bnode();

    ModelBuilder contextModelBuilder = new ModelBuilder()
            .subject(situationShapeIRI)
            .add(RDF.TYPE, SHACL.NODE_SHAPE)
            .add(SHACL.CLASS, CORE.RESOURCE_PROFILE)
            .add(SHACL.PROPERTY, profileProperty)
            .subject(profileProperty)
            .add(SHACL.PATH, CORE.IS_PROFILE_OF)
            .add(SHACL.QUALIFIED_MIN_COUNT, 1)
            .add(SHACL.QUALIFIED_MAX_COUNT, 1)
            .add(SHACL.QUALIFIED_VALUE_SHAPE, agentShapeIRI)
            .subject(agentShapeIRI)
            .add(RDF.TYPE, SHACL.NODE_SHAPE)
            .add(SHACL.CLASS, CORE.AGENT)
            .add(SHACL.PROPERTY, agentProperty)
            .subject(agentProperty)
            .add(SHACL.PATH, beliefPropIRI)
            .add(SHACL.MIN_COUNT, 1)
            .add(SHACL.MAX_COUNT, 1)
            .add(SHACL.HAS_VALUE, "room(empty)");

    Context context = new Context.Builder()
            .setIRIAsString("http://example.org/situationShape")
            .addModel(contextModelBuilder.build()).build();

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec)
                            .addRecommendedContext(context)
                            .build())
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  /* TODO pass
  @Test
  public void testWriteArtifactProfileReadFromFile() throws IOException, URISyntaxException {
    String baseUri = BASE_URI;
    BASE_URI = "http://example.org/ontology-example";
    URL fileResource = ArtifactProfileGraphReaderTest.class.getClassLoader()
            .getResource("artifact-profile.ttl");
    String profilePath = Paths.get(fileResource.toURI()).toFile().getPath();
    ResourceProfile profile = ResourceProfileGraphReader.readFromFile(profilePath);
    assertIsomorphicGraphs(Files.readString(Paths.get(profilePath)), profile);
    BASE_URI = baseUri;
  }
   */

  private void assertIsomorphicGraphs(String expectedProfile, ResourceProfile profile) throws RDFParseException,
          RDFHandlerException, IOException {

    Model expectedModel = readModelFromString(expectedProfile, BASE_URI);

    String actualProfile = new ResourceProfileGraphWriter(profile)
            .setNamespace(CORE.PREFIX, CORE.NAMESPACE)
            .setNamespace(INTERACTION.PREFIX, INTERACTION.NAMESPACE)
            .setNamespace("prs", "http://example.org/prs#")
            .setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
            .setNamespace("saref", "https://saref.etsi.org/core/")
            .write();

    LOGGER.info("Expected:\n" + expectedProfile);
    LOGGER.info("Actual:\n" + actualProfile);

    Model actualModel = readModelFromString(actualProfile, BASE_URI);
    compareModels(expectedModel, actualModel);

    assertTrue(Models.isomorphic(expectedModel, actualModel));
  }
}
