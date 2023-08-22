package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.ARTIFACT;
import static org.junit.jupiter.api.Assertions.*;

public class ArtifactProfileGraphReaderTest {

  private static final String PREFIXES =
          "@prefix hmas: <" + CORE.NAMESPACE + "> .\n" +
                  "@prefix hctl: <" + HCTL.NAMESPACE + "> .\n" +
                  "@prefix prs: <http://example.org/prs#> .\n" +
                  "@prefix sh: <" + SHACL.NAMESPACE + ">";

  @Test
  public void testReadSignifierWithAbilities() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "   hmas:signifies [ a hmas:ActionSpecification ;\n" +
            "     hmas:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ] ;\n" +
            "   hmas:recommendsAbility [ a hmas:Ability, prs:PRSAbility ] \n" +
            " ].";

    ArtifactProfile profile =
            ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(1, signifier.getRecommendedAbilities().size());
    Set<Ability> abilities = signifier.getRecommendedAbilities();

    List<Ability> abilitiesList = new ArrayList<>(abilities);
    Ability ability = abilitiesList.get(0);
    assertEquals(2, ability.getSemanticTypes().size());

  }

  @Test
  public void testReadActionSpecWithHTTPMethod() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount 1 ;\n" +
            "          sh:maxCount 1;\n" +
            "          sh:hasValue <urn:3g6lpq9v> ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            "<urn:3g6lpq9v> a hctl:Form ;\n" + //here the URN is randomly generated
            "   hctl:hasTarget <https://example.org/resource> .";

    ArtifactProfile profile = ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    ActionSpecification actionSpec = (ActionSpecification) signifier.getResource();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://example.org/resource", form.getTarget());
    assertEquals("urn:3g6lpq9v", form.getIRIAsString());
  }

  @Test
  public void testReadArtifactProfileMultipleForms() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "    hmas:signifies [ a sh:NodeShape ;\n" +
            "       sh:class hmas:ActionExecution ;\n" +
            "       sh:property [\n" +
            "          sh:path prov:used ;\n" +
            "          sh:minCount 1 ;\n" +
            "          sh:maxCount 1;\n" +
            "          sh:or (\n" +
            "             [ sh:hasValue <urn:3g6lpq9v> ]\n" +
            "             [ sh:hasValue <urn:x7aym3hn> ]\n" +
            "         ) ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .\n" +
            "<urn:3g6lpq9v> a hctl:Form ;\n" + //here the URN is randomly generated
            "   hctl:hasTarget <https://example.org/resource> .\n" +
            "<urn:x7aym3hn> a hctl:Form ;\n" + //here the URN is randomly generated
            "   hctl:hasTarget <coaps://example.org/resource> .";

    ArtifactProfile profile =
            ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    ActionSpecification actionSpec = (ActionSpecification) signifier.getResource();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(2, forms.size());

    assertTrue(forms.stream().anyMatch(form ->
            "https://example.org/resource".equals(form.getTarget()) && "urn:3g6lpq9v".equals(form.getIRIAsString()) ));

    assertTrue(forms.stream().anyMatch(form ->
            "coaps://example.org/resource".equals(form.getTarget()) && "urn:x7aym3hn".equals(form.getIRIAsString()) ));
  }
}
