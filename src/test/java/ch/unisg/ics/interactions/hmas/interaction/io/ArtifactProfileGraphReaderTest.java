package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.io.ArtifactProfileGraphReader;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Ability;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.ArtifactProfile;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Signifier;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.ARTIFACT;
import static org.junit.jupiter.api.Assertions.*;

public class ArtifactProfileGraphReaderTest {

  private static final String PREFIXES =
          "@prefix hmas: <" + CORE.PREFIX + "> .\n" +
          "@prefix hmas-int: <" + INTERACTION.PREFIX + "> .\n" +
          "@prefix prs: <http://example.org/prs#> \n" ;


  @Test
  public void testReadSignifierWithAbilities() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ];\n" +
            " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
            "   hmas-int:signifies [ a hmas-int:ActionSpecification ] ;\n" +
            "   hmas-int:recommendsAbility [ a hmas-int:Ability, prs:PRSAbility ] \n" +
            "].";

    ArtifactProfile profile =
            ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getType());
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

}
