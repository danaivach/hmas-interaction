package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SignifierTest {

  private static final String TARGET = "https://example.org/resource";
  private static final Form BASIC_FORM = new Form.Builder(TARGET).build();

  @Test
  public void testGetSignifiersByActionType() {

    ActionSpecification toggleCommandSpec1 = new ActionSpecification.Builder(BASIC_FORM)
            .addSemanticType("https://saref.etsi.org/core/ToggleCommand")
            .addSemanticType("https://saref.etsi.org/core/SetLevelCommand").build();
    ActionSpecification setLevelCommandSpec1 = new ActionSpecification.Builder(BASIC_FORM)
            .addSemanticType("https://saref.etsi.org/core/SetLevelCommand").build();
    ActionSpecification toggleCommandSpec2 = new ActionSpecification.Builder(BASIC_FORM)
            .addSemanticType("https://saref.etsi.org/core/ToggleCommand").build();

    Signifier sig1 = new Signifier.Builder(toggleCommandSpec1).build();
    Signifier sig2 = new Signifier.Builder(setLevelCommandSpec1).build();
    Signifier sig3 = new Signifier.Builder(toggleCommandSpec2).build();

    ArtifactProfile profile = new ArtifactProfile.Builder(new Artifact.Builder().build())
            .exposeSignifier(sig1)
            .exposeSignifier(sig2)
            .exposeSignifier(sig3).build();

    Set<Signifier> allSignifiers = profile.getExposedSignifiers();
    assertEquals(3, allSignifiers.size());
    assertTrue(allSignifiers.contains(sig1));
    assertTrue(allSignifiers.contains(sig2));
    assertTrue(allSignifiers.contains(sig3));

    Set<Signifier> toggleCommandSignifiers = profile
            .getExposedSignifiers("https://saref.etsi.org/core/ToggleCommand");
    assertEquals(2, toggleCommandSignifiers.size());
    assertTrue(toggleCommandSignifiers.contains(sig1));
    assertFalse(toggleCommandSignifiers.contains(sig2));
    assertTrue(toggleCommandSignifiers.contains(sig3));

    Set<Signifier> setLevelCommandSignifiers = profile
            .getExposedSignifiers("https://saref.etsi.org/core/SetLevelCommand");
    assertEquals(2, setLevelCommandSignifiers.size());
    assertTrue(setLevelCommandSignifiers.contains(sig1));
    assertTrue(setLevelCommandSignifiers.contains(sig2));
    assertFalse(setLevelCommandSignifiers.contains(sig3));

    Optional<Signifier> firstToggleCommandSignifier = profile
            .getFirstExposedSignifier("https://saref.etsi.org/core/ToggleCommand");
    assertTrue(firstToggleCommandSignifier.isPresent());

    Optional<Signifier> firstSetLevelCommandSignifier = profile
            .getFirstExposedSignifier("https://saref.etsi.org/core/SetLevelCommand");
    assertTrue(firstSetLevelCommandSignifier.isPresent());

    Optional<Signifier> firstStopCommandSignifier = profile
            .getFirstExposedSignifier("https://saref.etsi.org/core/StopCommand");
    assertFalse(firstStopCommandSignifier.isPresent());

  }

}
