package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.PROV;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArtifactProfileGraphWriterTest {

  private final static Logger LOGGER = Logger.getLogger(ArtifactProfileGraphWriterTest.class.getCanonicalName());

  private static final String HMAS_PREFIX = "@prefix hmas: <" + CORE.NAMESPACE + ">";

  private static final String PREFIXES =
          "@prefix hmas: <" + CORE.NAMESPACE + "> .\n" +
                  "@prefix hctl: <" + HCTL.NAMESPACE + "> .\n" +
                  "@prefix prs: <http://example.org/prs#> .\n" +
                  "@prefix prov: <" + PROV.NAMESPACE + "> .\n" +
                  "@prefix urn: <http://example.org/urn#> .\n" +
                  "@prefix sh: <" + SHACL.NAMESPACE + ">";

  private static final String FORM_IRI = "<urn:3g6lpq9v>";
  private static final String SECOND_FORM_IRI = "<urn:x7aym3hn>";
  private static final String BASE_URI = "http://example.org/";
  private static final String TARGET = "https://example.org/resource";
  private static final Form BASIC_FORM = new Form.Builder(TARGET)
          .addProperty("IRI", FORM_IRI).build();

  private static Model readModelFromString(String profile, String baseURI)
          throws RDFParseException, RDFHandlerException, IOException {
    StringReader stringReader = new StringReader(profile);

    RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
    Model model = new LinkedHashModel();
    rdfParser.setRDFHandler(new StatementCollector(model));

    rdfParser.parse(stringReader, baseURI);

    return model;
  }

  @Test
  public void testWriteArtifactProfileWithSignifier() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount 1;\n" +
            "          sh:maxCount 1;\n" +
            "          sh:hasValue " + FORM_IRI + " ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            "<urn:3g6lpq9v> a hctl:Form ;\n" + //here the URN is randomly generated
            "   hctl:hasTarget <https://example.org/resource> .";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    ArtifactProfile profile =
            new ArtifactProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec).build())
                    .build();

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
            "   hmas:signifies [ a hmas:ActionSpecification ;\n" +
            "     hmas:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ]. \n" +
            "<urn:signifier-2> a hmas:Signifier ;\n" +
            "   hmas:signifies [ a hmas:ActionSpecification ;\n" +
            "     hmas:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ]. ";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    Signifier signifier1 =
            new Signifier.Builder(actionSpec)
                    .setIRI(SimpleValueFactory.getInstance().createIRI("urn:signifier-1"))
                    .build();

    Signifier signifier2 =
            new Signifier.Builder(actionSpec)
                    .setIRIAsString("urn:signifier-2")
                    .build();

    ArtifactProfile profile =
            new ArtifactProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(signifier1)
                    .exposeSignifier(signifier2)
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteSignifierWithActionSpec() throws IOException {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "   hmas:signifies [ a hmas:ActionSpecification ;\n" +
            "     hmas:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ] \n" +
            "].";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    ArtifactProfile profile =
            new ArtifactProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec)
                            .build())
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
            "   hmas:signifies [ a hmas:ActionSpecification ;\n" +
            "     hmas:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ];\n" +
            "   hmas:recommendsAbility [ a hmas:Ability, prs:PRSAbility ]\n" +
            "].";

    ActionSpecification actionSpec = new ActionSpecification.Builder(BASIC_FORM).build();

    ArtifactProfile profile =
            new ArtifactProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec)
                            .addRecommendedAbility(new Ability.Builder()
                                    .addSemanticType("http://example.org/prs#PRSAbility")
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
            "<http://example.org/profile#agent-body> a hmas:AgentBody.";

    AgentBody body = new AgentBody.Builder().setIRIAsString("http://example.org/profile#agent-body").build();

    ArtifactProfile profile =
            new ArtifactProfile.Builder(body)
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
            "          sh:minCount \"1\" ;\n" +
            "          sh:maxCount \"1\";\n" +
            "          sh:or (\n" +
            "             [ sh:hasValue " + FORM_IRI + " ]\n" +
            "             [ sh:hasValue " + SECOND_FORM_IRI + " ]\n" +
            "         ) ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "   hctl:hasTarget <https://example.org/resource> .\n" +
            SECOND_FORM_IRI + " a hctl:Form ;\n" + //here the URN is randomly generated
            "   hctl:hasTarget <coaps://example.org/resource> .";

    Form coapForm = new Form.Builder("coaps://example.org/resource")
            .addProperty("IRI", SECOND_FORM_IRI).build();

    Set<Form> forms = new HashSet<>(Arrays.asList(coapForm, BASIC_FORM));

    ActionSpecification actionSpec = new ActionSpecification.Builder(forms).build();

    ArtifactProfile profile =
            new ArtifactProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .exposeSignifier(new Signifier.Builder(actionSpec).build())
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  private void assertIsomorphicGraphs(String expectedProfile, ArtifactProfile profile) throws RDFParseException,
          RDFHandlerException, IOException {

    Model expectedModel = readModelFromString(expectedProfile, BASE_URI);

    String actualProfile = new ArtifactProfileGraphWriter(profile)
            .setNamespace(CORE.PREFIX, CORE.NAMESPACE)
            .setNamespace(INTERACTION.PREFIX, INTERACTION.NAMESPACE)
            .setNamespace("prs", "http://example.org/prs#")
            .write();

    LOGGER.info("Expected:\n" + expectedProfile);
    LOGGER.info("Actual:\n" + actualProfile);

    Model actualModel = readModelFromString(actualProfile, BASE_URI);

    assertTrue(Models.isomorphic(expectedModel, actualModel));
  }
}
