package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.BaseSignifier;
import ch.unisg.ics.interactions.hmas.core.io.InvalidResourceProfileException;
import ch.unisg.ics.interactions.hmas.core.io.ResourceProfileGraphReader;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Ability;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.ActionSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Signifier;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.HashSet;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS.EXPOSES_SIGNIFIER;
import static ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS.PREFIX;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.RECOMMENDS_ABILITY;
import static ch.unisg.ics.interactions.hmas.interaction.vocabularies.INTERACTION.SIGNIFIES;

public class InteractiveProfileGraphReader extends ResourceProfileGraphReader {

  @Override
  protected Set<BaseSignifier> readSignifiers() {
    Set<BaseSignifier> signifiers = new HashSet<>();
    Set<Resource> signifierNodes = Models.objectResources(model.filter(profileIRI, EXPOSES_SIGNIFIER.toIRI(),
            null));
    for (Resource signifierNode : signifierNodes) {
      Set<Resource> bSpecNodes = Models.objectResources(model.filter(signifierNode, SIGNIFIES.toIRI(), null));
      if (!bSpecNodes.isEmpty()) {
        ActionSpecification acSpec = new ActionSpecification.Builder(new Form()).build();
        Signifier.Builder builder = new Signifier.Builder(acSpec);

        Set<Resource> abilities = Models.objectResources(model.filter(signifierNode, RECOMMENDS_ABILITY.toIRI(),
                null));

        for (Resource ability : abilities) {
          Ability.Builder abilityBuilder = new Ability.Builder();
          Set<IRI> abilityTypes = Models.objectIRIs(model.filter(signifierNode, RDF.TYPE,
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
}
