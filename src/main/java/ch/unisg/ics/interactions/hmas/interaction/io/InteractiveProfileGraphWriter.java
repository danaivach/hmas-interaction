package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.BaseSignifier;
import ch.unisg.ics.interactions.hmas.core.hostables.ResourceProfile;
import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphWriter;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Ability;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Signifier;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.vocabularies.INTERACTION;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS.SIGNIFIER;

public class InteractiveProfileGraphWriter extends ResourceProfileGraphWriter {
  public InteractiveProfileGraphWriter(ResourceProfile profile) {
    super(profile);
  }

  @Override
  protected ResourceProfileGraphWriter addSignifiers() {
    Set<BaseSignifier> signifiers = profile.getExposedSignifiers();
    if (!signifiers.isEmpty()) {
      for (BaseSignifier signifier : signifiers) {

        Resource locatedSignifier = resolveHostableLocation(signifier);
        graphBuilder.add(profileIRI, EXPOSES_SIGNIFIER.toIRI(), locatedSignifier);
        graphBuilder.add(locatedSignifier, RDF.TYPE, SIGNIFIER.toIRI());
        Signifier sg = (Signifier) signifier;
        Set<Ability> abilities = sg.getRecommendedAbilities();

        for (Ability ability : abilities) {
          Resource abilityId = rdf.createBNode();
          graphBuilder.add(locatedSignifier, INTERACTION.RECOMMENDS_ABILITY.toIRI(), abilityId);
          graphBuilder.add(abilityId, RDF.TYPE, INTERACTION.ABILITY.toIRI());
          for (String type : ability.getSemanticTypes()) {
            graphBuilder.add(abilityId, RDF.TYPE, type);
          }
        }
      }
    }
    return this;
  }
}
