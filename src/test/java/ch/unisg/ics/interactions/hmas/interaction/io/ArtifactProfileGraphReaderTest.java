package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.ARTIFACT;
import static org.junit.jupiter.api.Assertions.*;

public class ArtifactProfileGraphReaderTest {

  private static final String PREFIXES =
      "@prefix hmas: <" + CORE.NAMESPACE + "> .\n" +
          "@prefix hctl: <" + HCTL.NAMESPACE + "> .\n" +
          "@prefix prs: <http://example.org/prs#> .\n" +
          "@prefix sh: <" + SHACL.NAMESPACE + ">";

  @Test
  public void testReadSignifierWithAbilities() {
    String expectedProfile = PREFIXES +
        ".\n" +
        "<urn:profile> a hmas:ResourceProfile ;\n" +
        " hmas:isProfileOf [ a hmas:Artifact ];\n" +
        " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
        "    hmas:signifies [ a sh:NodeShape ;\n" +
        "       sh:class hmas:ActionExecution ;\n" +
        "       sh:property [\n" +
        "          sh:path prov:used ;\n" +
        "          sh:minCount 1 ;\n" +
        "          sh:maxCount 1;\n" +
        "          sh:hasValue [ a hctl:Form ;\n" +
        "             hctl:hasTarget <https://example.org/resource> \n" +
        "          ]\n" +
        "      ]\n" +
        "   ];\n" +
        "   hmas:recommendsAbility [ a hmas:Ability, prs:PRSAbility ] \n" +
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
        "    hmas:signifies [ a sh:NodeShape ;\n" +
        "       sh:class hmas:ActionExecution ;\n" +
        "       sh:property [\n" +
        "          sh:path prov:used ;\n" +
        "          sh:minCount 1 ;\n" +
        "          sh:maxCount 1;\n" +
        "          sh:hasValue <urn:3g6lpq9v> ;\n" +
        "      ]\n" +
        "   ]\n" +
        "] .\n" +
        "<urn:3g6lpq9v> a hctl:Form ;\n" + //here the URN is randomly generated
        "   hctl:hasTarget <https://example.org/resource> .";

    ArtifactProfile profile = ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://example.org/resource", form.getTarget());
    assertEquals("urn:3g6lpq9v", form.getIRIAsString().get());
  }

  @Test
  public void testReadArtifactProfileMultipleForms() {
    String expectedProfile = PREFIXES +
        ".\n" +
        "<urn:profile> a hmas:ResourceProfile ;\n" +
        " hmas:isProfileOf [ a hmas:Artifact ];\n" +
        " hmas:exposesSignifier [ a hmas:Signifier ;\n" +
        "    hmas:signifies [ a sh:NodeShape ;\n" +
        "       sh:class hmas:ActionExecution ;\n" +
        "       sh:property [\n" +
        "          sh:path prov:used ;\n" +
        "          sh:minCount 1 ;\n" +
        "          sh:maxCount 1;\n" +
        "          sh:or (\n" +
        "             [ sh:hasValue <urn:3g6lpq9v> ]\n" +
        "             [ sh:hasValue <urn:x7aym3hn> ]\n" +
        "         ) ;\n" +
        "      ]\n" +
        "   ]\n" +
        "] .\n" +
        "<urn:3g6lpq9v> a hctl:Form ;\n" + //here the URN is randomly generated
        "   hctl:hasTarget <https://example.org/resource> .\n" +
        "<urn:x7aym3hn> a hctl:Form ;\n" + //here the URN is randomly generated
        "   hctl:hasTarget <coaps://example.org/resource> .";

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

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(2, forms.size());

    Form firstForm = actionSpec.getFirstForm();
    assertTrue(forms.contains(firstForm));

    assertTrue(forms.stream().anyMatch(form ->
        "https://example.org/resource".equals(form.getTarget()) && "urn:3g6lpq9v".equals(form.getIRIAsString().get())));

    assertTrue(forms.stream().anyMatch(form ->
        "coaps://example.org/resource".equals(form.getTarget()) && "urn:x7aym3hn".equals(form.getIRIAsString().get())));
  }

  @Test
  public void testReadArtifactProfileWithInput() {
    String expectedProfile = PREFIXES +
        ".\n" +
        "@prefix ex: <http://example.org/> .\n" +
        "@prefix xs: <https://www.w3.org/2001/XMLSchema#> .\n" +
        "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
        "<urn:profile> a hmas:ResourceProfile ;\n" +
        "  hmas:isProfileOf [ a hmas:Artifact ];\n" +
        "  hmas:exposesSignifier ex:signifier .\n" +
        "\n" +
        "ex:signifier a hmas:Signifier ;\n" +
        "  hmas:signifies ex:moveGripperSpecification .\n" +
        "\n" +
        "ex:moveGripperSpecification a sh:NodeShape;\n" +
        "  sh:class hmas:ActionExecution ;\n" +
        "  sh:property [\n" +
        "    sh:path prov:used ;\n" +
        "    sh:minCount 1;\n" +
        "    sh:maxCount 1 ;\n" +
        "    sh:hasValue ex:httpForm ;\n" +
        "  ] ;\n" +
        "  sh:property [\n" +
        "    sh:path hmas:hasInput;\n" +
        "    sh:qualifiedValueShape ex:gripperJointShape ;\n" +
        "    sh:qualifiedMinCount 1 ;\n" +
        "    sh:qualifiedMaxCount 1 \n" +
        "  ] .\n" +
        "\n" +
        "ex:gripperJointShape a sh:NodeShape ;\n" +
        "  sh:class ex:GripperJoint ;\n" +
        "  sh:property [\n" +
        "    sh:path ex:hasGripperValue ;\n" +
        "    sh:minCount 1;\n" +
        "    sh:maxCount 1 ;\n" +
        "    sh:datatype xs:integer\n" +
        "  ] .\n" +
        "\n" +
        "ex:httpForm a hctl:Form ;\n" +
        "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
        "  hctl:forContentType \"application/json\" ;\n" +
        "  htv:methodName \"PUT\" .";

    ArtifactProfile profile = ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper", form.getTarget());
    assertEquals("http://example.org/httpForm", form.getIRIAsString().get());

    Optional<InputSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());
    InputSpecification input = i.get();
    assertEquals(Set.of("http://example.org/GripperJoint"), input.getRequiredSemanticTypes());
    assertEquals("http://example.org/hasGripperValue",
        input.getInputs().stream().findFirst().get().getRequiredProperties().get());
    assertEquals("https://www.w3.org/2001/XMLSchema#integer",
        input.getInputs().stream().findFirst().get().getRequiredDataType().get());
  }

  @Test
  public void testReadArtifactProfileWithOutput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <https://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "  hmas:isProfileOf [ a hmas:Artifact ];\n" +
            "  hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier ;\n" +
            "  hmas:signifies ex:readGripperSpecification .\n" +
            "\n" +
            "ex:readGripperSpecification a sh:NodeShape;\n" +
            "  sh:class hmas:ActionExecution ;\n" +
            "  sh:property [\n" +
            "    sh:path prov:used ;\n" +
            "    sh:minCount 1;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:hasValue ex:httpForm ;\n" +
            "  ] ;\n" +
            "  sh:property [\n" +
            "    sh:path hmas:hasOutput;\n" +
            "    sh:qualifiedValueShape ex:gripperJointShape ;\n" +
            "    sh:qualifiedMinCount 1 ;\n" +
            "    sh:qualifiedMaxCount 1 \n" +
            "  ] .\n" +
            "\n" +
            "ex:gripperJointShape a sh:NodeShape ;\n" +
            "  sh:class ex:GripperJoint ;\n" +
            "  sh:property [\n" +
            "    sh:path ex:hasGripperValue ;\n" +
            "    sh:minCount 1;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:datatype xs:integer\n" +
            "  ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"GET\" .";

    ArtifactProfile profile = ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper", form.getTarget());
    assertEquals("http://example.org/httpForm", form.getIRIAsString().get());


    Optional<OutputSpecification> i = actionSpec.getOuputSpecification();
    assertTrue(i.isPresent());
    /*
    InputSpecification input = (InputSpecification) i.get();
    assertEquals(Set.of("http://example.org/GripperJoint"), input.getRequiredSemanticTypes());
    assertEquals("http://example.org/hasGripperValue",
            input.getInputs().stream().findFirst().get().getRequiredProperties().get());
    assertEquals("https://www.w3.org/2001/XMLSchema#integer",
            input.getInputs().stream().findFirst().get().getRequiredDataType().get());

     */
  }

  @Test
  public void testReadArtifactProfileWithContext() {
    String expectedProfile = PREFIXES +
        ".\n" +
        "@prefix ex: <http://example.org/> .\n" +
        "@prefix xs: <https://www.w3.org/2001/XMLSchema#> .\n" +
        "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
        "<urn:profile> a hmas:ResourceProfile ;\n" +
        "  hmas:isProfileOf [ a hmas:Artifact ];\n" +
        "  hmas:exposesSignifier ex:signifier .\n" +
        "\n" +
        "ex:signifier a hmas:Signifier ;\n" +
        "  hmas:recommendsContext ex:situationShape ;\n" +
        "  hmas:signifies ex:moveGripperSpecification .\n" +
        "\n" +
        "ex:moveGripperSpecification a sh:NodeShape;\n" +
        "  sh:class hmas:ActionExecution ;\n" +
        "  sh:property [\n" +
        "    sh:path prov:used ;\n" +
        "    sh:minCount 1;\n" +
        "    sh:maxCount 1 ;\n" +
        "    sh:hasValue ex:httpForm ;\n" +
        "  ] .\n" +
        "\n" +
        "ex:situationShape a sh:NodeShape ;\n" +
        "  sh:class hmas:ResourceProfile ;\n" +
        "  sh:property [\n" +
        "    sh:path hmas:isProfileOf ;\n" +
        "    sh:qualifiedValueShape ex:agentShape ;\n" +
        "    sh:qualifiedMinCount 1 ;\n" +
        "    sh:qualifiedMaxCount 1 \n" +
        "  ] .\n" +
        "\n" +
        "ex:agentShape a sh:NodeShape ;\n" +
        "  sh:class hmas:Agent ;\n" +
        "  sh:property [\n" +
        "    sh:path ex:hasBelief ;\n" +
        "    sh:minCount 1;\n" +
        "    sh:maxCount 1 ;\n" +
        "    sh:hasValue \"room(empty)\"\n" +
        "  ] .\n" +
        "ex:httpForm a hctl:Form ;\n" +
        "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
        "  hctl:forContentType \"application/json\" ;\n" +
        "  htv:methodName \"PUT\" .";

    ArtifactProfile profile = ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);

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
  public void testReadArtifactProfileSemanticTypes() {
    String expectedProfile = PREFIXES +
        ".\n" +
        "@prefix saref: <https://saref.etsi.org/core/> .\n" +
        "@prefix xs: <https://www.w3.org/2001/XMLSchema#> .\n" +
        "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
        "<http://example.org/workspaces/meetingroom/artifacts/lightbulb1> a hmas:ResourceProfile ;\n" +
        "  hmas:isProfileOf <http://example.org/workspaces/meetingroom/artifacts/lightbulb1#artifact>;\n" +
        "  hmas:exposesSignifier [  a hmas:Signifier ;\n" +
        "    hmas:signifies <http://example.org/workspaces/meetingroom/artifacts/lightbulb1#toggle> ; ] .\n" +
        "\n" +
        "<http://example.org/workspaces/meetingroom/artifacts/lightbulb1#artifact> a hmas:Artifact, saref:LightSwitch .\n" +
        "\n" +
        "<http://example.org/workspaces/meetingroom/artifacts/lightbulb1#toggle> a sh:NodeShape;\n" +
        "  sh:class hmas:ActionExecution, saref:ToggleCommand ;\n" +
        "  sh:property [\n" +
        "    sh:path prov:used ;\n" +
        "    sh:minCount 1;\n" +
        "    sh:maxCount 1 ;\n" +
        "    sh:hasValue <http://example.org/workspaces/meetingroom/artifacts/lightbulb1#form> ;\n" +
        "  ] ;\n" +
        "  sh:property [\n" +
        "    sh:path hmas:hasInput;\n" +
        "    sh:qualifiedValueShape <http://example.org/workspaces/meetingroom/artifacts/lightbulb1#input> ;\n" +
        "    sh:qualifiedMinCount 1 ;\n" +
        "    sh:qualifiedMaxCount 1 \n" +
        "  ] .\n" +
        "\n" +
        "<http://example.org/workspaces/meetingroom/artifacts/lightbulb1#input> a sh:NodeShape ;\n" +
        "  sh:class saref:OnOffState ;\n" +
        "  sh:property [\n" +
        "    sh:path saref:hasValue ;\n" +
        "    sh:minCount 1;\n" +
        "    sh:maxCount 1 ;\n" +
        "    sh:datatype xs:integer\n" +
        "  ] .\n" +
        "\n" +
        "<http://example.org/workspaces/meetingroom/artifacts/lightbulb1#form> a hctl:Form ;\n" +
        "  hctl:hasTarget <https://example.org/light> ;\n" +
        "  hctl:forContentType \"application/json\" ;\n" +
        "  htv:methodName \"PUT\" .";
    ArtifactProfile profile = ArtifactProfileGraphReader.readFromString(expectedProfile);

    Artifact artifact = profile.getArtifact();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertTrue(artifact.getIRI().isPresent());
    assertTrue(artifact.getSemanticTypes().contains("https://saref.etsi.org/core/LightSwitch"));

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());

    Set<String> actionTypes = actionSpec.getRequiredSemanticTypes();
    assertEquals(1, actionTypes.size());
    actionTypes.contains("https://saref.etsi.org/core/ToggleCommand");

    Optional<InputSpecification> inputSpec = actionSpec.getInputSpecification();
    assertTrue(inputSpec.isPresent());
    InputSpecification comInputSpec = (InputSpecification)  inputSpec.get();

    Set<String> inputTypes = comInputSpec.getRequiredSemanticTypes(); //defined as a `Set` since there can be more than one objects for the property `sh:class`
    assertEquals(1, inputTypes.size());
    actionTypes.contains("https://saref.etsi.org/core/OnOffState");

    assertEquals("https://saref.etsi.org/core/hasValue",
        comInputSpec.getInputs().stream().findFirst().get().getRequiredProperties().get());
    assertEquals("https://www.w3.org/2001/XMLSchema#integer",
        comInputSpec.getInputs().stream().findFirst().get().getRequiredDataType().get());

   /* To be discussed
   Set<Property> properties = comInputSpec.getRequiredProperties();
   assertEquals(1, inputTypes.size());
   actionTypes.contains("https://saref.etsi.org/core/OnOffState");
   assertTrue((comInputSpec.getRequiredProperties.stream().findFirst().get()).getSemanticTypes().contains(https://saref.etsi.org/core/hasValue));
   assertEquals("https://www.w3.org/2001/XMLSchema#integer", (comInputSpec.getRequiredProperties.stream().findFirst().get()).getRequiredDataType());
*/
  }
}