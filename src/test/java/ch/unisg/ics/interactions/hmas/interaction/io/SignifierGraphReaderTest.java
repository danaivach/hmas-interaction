package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignifierGraphReaderTest {

  @Test
  public void testReadFromFile() throws IOException {
    Signifier signifier = SignifierGraphReader.readFromFile("src/test/resources/signifier.ttl");

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper", form.getTarget());
    assertEquals("http://example.org/httpForm", form.getIRIAsString().get());

    Set<Context> contexts = signifier.getRecommendedContexts();
    assertEquals(1, contexts.size());
    Context context = signifier.getRecommendedContexts().stream().findFirst().get();

    Model contextModel = context.getModel();
    SimpleValueFactory valueFactory = SimpleValueFactory.getInstance();
    IRI hasBeliefResource = valueFactory.createIRI("http://example.org/hasBelief");

    List<Resource> actualBeliefs = contextModel.filter(null, SHACL.PATH, hasBeliefResource)
            .stream()
            .map(Statement::getSubject)
            .collect(Collectors.toList());

    assertEquals(1, actualBeliefs.size());

    List<Value> actualBeliefContents = contextModel.filter(actualBeliefs.get(0), SHACL.HAS_VALUE, null)
            .stream()
            .map(Statement::getObject)
            .collect(Collectors.toList());

    assertEquals(1, actualBeliefContents.size());
    assertEquals("room(empty)", actualBeliefContents.get(0).stringValue());
  }

  @Test
  public void testReadFromUrl() throws IOException {
    Signifier signifier = SignifierGraphReader.readFromURL("http://172.27.52.55:8080/workspaces/61/artifacts/component5/#5C");
    Set<Ability> abilities = signifier.getRecommendedAbilities();
    assertEquals(1, abilities.size());
    Set<Context> contexts = signifier.getRecommendedContexts();
    assertEquals(1, contexts.size());
    for (Context context : contexts) {
      Model model = context.getModel();
      System.out.println(model);
    }
  }
}
