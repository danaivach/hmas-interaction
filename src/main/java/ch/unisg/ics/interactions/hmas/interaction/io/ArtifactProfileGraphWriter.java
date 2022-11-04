package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphWriter;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Ability;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.ArtifactProfile;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.BehavioralSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Signifier;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.*;

public class ArtifactProfileGraphWriter extends ResourceProfileGraphWriter {
  public ArtifactProfileGraphWriter(ArtifactProfile profile) {
    super(profile);
  }

  @Override
  public String write() {
    this.addSignifiers();
    return super.write();
  }

  protected ResourceProfileGraphWriter addSignifiers() {
    ArtifactProfile profile = (ArtifactProfile) this.profile;
    Set<Signifier> signifiers = profile.getExposedSignifiers();
    for (Signifier signifier : signifiers) {
      Resource locatedSignifier = resolveHostableLocation(signifier);
      graphBuilder.add(profileIRI, EXPOSES_SIGNIFIER, locatedSignifier);
      graphBuilder.add(locatedSignifier, RDF.TYPE, SIGNIFIER);

      Set<Ability> abilities = signifier.getRecommendedAbilities();
      addRecommendedAbilities(locatedSignifier, abilities);

      BehavioralSpecification bSpec = signifier.getBehavioralSpecification();
      addBehavioralSpecification(locatedSignifier, bSpec);
    }
    return this;
  }

  protected void addRecommendedAbilities(Resource signifier, Set<Ability> abilities) {
    for (Ability ability : abilities) {
      Resource abilityId = rdf.createBNode();
      graphBuilder.add(signifier, RECOMMENDS_ABILITY, abilityId);
      graphBuilder.add(abilityId, RDF.TYPE, ABILITY);
      for (String type : ability.getSemanticTypes()) {
        graphBuilder.add(abilityId, RDF.TYPE, rdf.createIRI(type));
      }
    }
  }

  protected void addBehavioralSpecification(Resource signifier, BehavioralSpecification specification) {
    Resource specId = rdf.createBNode();
    graphBuilder.add(signifier, SIGNIFIES, specId);
    graphBuilder.add(specId, RDF.TYPE, ACTION_SPECIFICATION);
  }
}
