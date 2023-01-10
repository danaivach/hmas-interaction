package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArtifactProfileGraphWriterTest {

  private final static Logger LOGGER = Logger.getLogger(ArtifactProfileGraphWriterTest.class.getCanonicalName());

  private static final String CORE_PREFIX = "@prefix hmas: <" + CORE.NAMESPACE + ">";
  private static final String INTERACTION_PREFIX = "@prefix hmas-int: <" + INTERACTION.NAMESPACE + ">";

  private static final String PREFIXES =
          CORE_PREFIX + ".\n" + INTERACTION_PREFIX + ".\n" +
                  "@prefix prs: <http://example.org/prs#> .\n" +
                  "@prefix hctl: <" + HCTL.NAMESPACE + "> \n";


  private static final String BASE_URI = "http://example.org/";
  private static final String TARGET = "https://example.org/resource";
  private static final Form BASIC_FORM = new Form.Builder(TARGET).build();

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
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ;\n" +
            "     hmas-int:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ]\n" +
            " ]. ";

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
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ;\n" +
            "     hmas-int:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ]. \n" +
            "<urn:signifier-2> a hmas:Signifier ;\n" +
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ;\n" +
            "     hmas-int:hasForm [ a hctl:Form ;\n" +
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
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ;\n" +
            "     hmas-int:hasForm [ a hctl:Form ;\n" +
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
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ;\n" +
            "     hmas-int:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ];\n" +
            "   hmas-int:recommendsAbility [ a hmas-int:Ability, prs:PRSAbility ]\n" +
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
