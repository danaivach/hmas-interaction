package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.ARTIFACT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ArtifactProfileGraphReaderTest {

  private static final String PREFIXES =
          "@prefix hmas: <" + CORE.NAMESPACE + "> .\n" +
                  "@prefix hmas-int: <" + INTERACTION.NAMESPACE + "> .\n" +
                  "@prefix hctl: <" + HCTL.NAMESPACE + "> .\n" +
                  "@prefix prs: <http://example.org/prs#> \n";


  @Test
  public void testReadSignifierWithAbilities() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ;\n" +
            "     hmas-int:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ] ;\n" +
            "   hmas-int:recommendsAbility [ a hmas-int:Ability, prs:PRSAbility ] \n" +
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
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ;\n" +
            "     hmas-int:hasForm [ a hctl:Form ;\n" +
            "       hctl:hasTarget <https://example.org/resource> \n" +
            "     ]\n" +
            "   ] \n" +
            "].";

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

    ActionSpecification actionSpec = (ActionSpecification) signifier.getBehavioralSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://example.org/resource", form.getTarget());
  }
}
