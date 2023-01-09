package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphWriter;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HTV;
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

    ActionSpecification actionSpecification = (ActionSpecification) specification;
    Set<Form> forms = actionSpecification.getForms();

    for (Form form : forms) {
      Resource formId = rdf.createBNode();

      graphBuilder.add(specId, HAS_FORM, formId);
      graphBuilder.add(formId, RDF.TYPE, HCTL.FORM);
      graphBuilder.add(formId, HCTL.HAS_TARGET, rdf.createIRI(form.getTarget()));
      //graphBuilder.add(formId, HCTL.FOR_CONTENT_TYPE, form.getContentType());

      if (form.getMethodName().isPresent()) {
        graphBuilder.add(formId, HTV.METHOD_NAME, form.getMethodName().get());
      }
    }
  }
}
