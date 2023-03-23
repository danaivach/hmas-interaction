package ch.unisg.ics.interactions.hmas.interaction.examples;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.io.ArtifactProfileGraphWriter;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.ActionSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.ArtifactProfile;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Signifier;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;

public class LightbulbProfileExample {

  public static void main(String[] args) {
    ActionSpecification toggleSpec =
            new ActionSpecification.Builder(new Form.Builder("https://lightbulb1.example.org/light")
                    .setMethodName("POST")
                    .build())
                    .addSemanticType("https://saref.etsi.org/core/ToggleCommand")
                    .build();

    ArtifactProfile profile =
            new ArtifactProfile.Builder(new Artifact.Builder()
                    .setIRIAsString("http://example.org/workspaces/meetingroom/artifacts/lightbulb1#artifact")
                    .build())
                    .setIRIAsString("http://example.org/workspaces/meetingroom/artifacts/lightbulb1")
                    .exposeSignifier(new Signifier.Builder(toggleSpec)
                            .build())
                    .build();

    String profileStr = new ArtifactProfileGraphWriter(profile)
            .setNamespace(CORE.PREFIX, CORE.NAMESPACE)
            .setNamespace(INTERACTION.PREFIX, INTERACTION.NAMESPACE)
            .write();

    System.out.println(profileStr);

  }

}
