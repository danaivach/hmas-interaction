package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.io.InvalidResourceProfileException;
import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphReader;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.RECOMMENDS_ABILITY;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.SIGNIFIES;

public class ArtifactProfileGraphReader extends ResourceProfileGraphReader {

  protected ArtifactProfileGraphReader(RDFFormat format, String representation) {
    super(format, representation);
  }

  public static ArtifactProfile readFromString(String representation) {
    ArtifactProfileGraphReader reader = new ArtifactProfileGraphReader(RDFFormat.TURTLE, representation);

    ArtifactProfile.Builder artifactBuilder =
            new ArtifactProfile.Builder((Artifact) reader.readOwnerResource())
                    .addHMASPlatforms(reader.readHomeHMASPlatforms())
                    .exposeSignifiers(reader.readSignifiers());

    Optional<IRI> profileIRI = reader.readProfileIRI();
    if (profileIRI.isPresent()) {
      artifactBuilder.setIRI(profileIRI.get());
    }

    return artifactBuilder.build();
  }

  protected Set<Signifier> readSignifiers() {
    Set<Signifier> signifiers = new HashSet<>();
    Set<Resource> signifierNodes = Models.objectResources(model.filter(profileIRI, EXPOSES_SIGNIFIER,
            null));
    for (Resource signifierNode : signifierNodes) {
      Set<Resource> bSpecNodes = Models.objectResources(model.filter(signifierNode, SIGNIFIES, null));

      if (!bSpecNodes.isEmpty()) {
        Set<Form> forms = readForms(bSpecNodes);
        ActionSpecification acSpec = new ActionSpecification.Builder(forms).build();
        Signifier.Builder builder = new Signifier.Builder(acSpec);

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

  protected Set<Form> readForms(Set<Resource> formNodes) {
    Set<Form> forms = new HashSet<>();
    /*for (Resource formNode : formNodes) {
      Optional<IRI> target = Models.objectIRI(model.filter(formNode, HCTL.hasTarget),
              null));

      if (!targetOpt.isPresent()) {
        continue;
      Form.Builder builder = new Form.Builder():
    }


     */

    return forms;
  }
}
