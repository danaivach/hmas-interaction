package ch.unisg.ics.interactions.hmas.interaction.io;

import ch.unisg.ics.interactions.hmas.core.hostables.ProfiledResource;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.shapes.*;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.*;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.HCTL;
import ch.unisg.ics.interactions.hmas.interaction.vocabularies.SHACL;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.junit.jupiter.api.Test;

import java.util.*;
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

    ResourceProfile profile =
            ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
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
            " hmas:exposesSignifier [ a hmas:Signifier , <http://example.org/signifier-type-1> ,\n" +
            "    <http://example.org/signifier-type-2> , <http://example.org/signifier-type-3> ;\n" +
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

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    assertEquals(4, signifier.getSemanticTypes().size());
    assertTrue(signifier.getSemanticTypes().contains("http://example.org/signifier-type-1"));
    assertTrue(signifier.getSemanticTypes().contains("http://example.org/signifier-type-2"));
    assertTrue(signifier.getSemanticTypes().contains("http://example.org/signifier-type-3"));
    assertTrue(signifier.getSemanticTypes().contains(CORE.TERM.SIGNIFIER.toString()));

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://example.org/resource", form.getTarget());
    assertEquals("urn:3g6lpq9v", form.getIRIAsString().get());
  }

  @Test
  public void testReadActionSpecWithFromBNode() {
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
            "          sh:hasValue [ a hctl:Form ; \n" +
            "            hctl:hasTarget <https://example.org/resource> ] ] ] ] .";


    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertEquals(0, signifier.getRecommendedAbilities().size());

    assertEquals(1, signifier.getSemanticTypes().size());
    assertTrue(signifier.getSemanticTypes().contains(CORE.TERM.SIGNIFIER.toString()));

    ActionSpecification actionSpec = signifier.getActionSpecification();
    Set<Form> forms = actionSpec.getForms();
    assertEquals(1, forms.size());
    Form form = new ArrayList<>(forms).get(0);
    assertEquals("https://example.org/resource", form.getTarget());
    assertFalse(form.getIRI().isPresent());
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

    ResourceProfile profile =
            ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
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
  public void testReadActionSpecWithHTTPMethodBlankNode() {
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
            "          sh:hasValue [ a hctl:Form ; hctl:hasTarget <https://example.org/resource> ] ;\n" +
            "      ]\n" +
            "   ]\n" +
            "] .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
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
    assertFalse(form.getIRIAsString().isPresent());
  }

  @Test
  public void testReadArtifactProfileWithBooleanInput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "    sh:datatype xs:boolean ;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:order 5 ;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:hasValue true ;\n" +
            "    sh:defaultValue true ;\n" +
            "  ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());

    BooleanSpecification iSpec = (BooleanSpecification) i.get();

    assertTrue(iSpec.getRequiredSemanticTypes().contains(XSD.BOOLEAN.stringValue()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertFalse(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getValue().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());

    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals(true, iSpec.getValue().get());
    assertEquals(true, iSpec.getDefaultValue().get());
  }

  @Test
  public void testReadArtifactProfileWithDoubleInput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "    sh:datatype xs:double ;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:order 5 ;\n" +
            "    sh:hasValue 10.5 ;\n" +
            "    sh:defaultValue 10.5 ;\n" +
            "  ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());

    DoubleSpecification iSpec = (DoubleSpecification) i.get();
    assertFalse(iSpec.getIRI().isPresent());

    assertTrue(iSpec.getRequiredSemanticTypes().contains(XSD.DOUBLE.stringValue()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertFalse(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getValue().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());

    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals(10.5, iSpec.getValue().get());
    assertEquals(10.5, iSpec.getDefaultValue().get());
  }

  @Test
  public void testReadArtifactProfileWithFloatOutput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "    sh:path hmas:hasOutput;\n" +
            "    sh:datatype xs:float ;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:minCount 1 ;\n" +
            "    sh:order 5 ;\n" +
            "    sh:hasValue 10.5 ;\n" +
            "    sh:defaultValue 10.5 ;\n" +
            "  ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getOutputSpecification();
    assertTrue(i.isPresent());

    FloatSpecification iSpec = (FloatSpecification) i.get();
    assertFalse(iSpec.getIRI().isPresent());

    assertTrue(iSpec.getRequiredSemanticTypes().contains(XSD.FLOAT.stringValue()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertTrue(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getValue().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());

    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals(10.5f, iSpec.getValue().get());
    assertEquals(10.5f, iSpec.getDefaultValue().get());
  }

  @Test
  public void testReadArtifactProfileWithIntegerInput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "    sh:datatype xs:int ;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:minCount 1 ;\n" +
            "    sh:order 5 ;\n" +
            "    sh:hasValue 10 ;\n" +
            "    sh:defaultValue 10 ;\n" +
            "  ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());

    IntegerSpecification iSpec = (IntegerSpecification) i.get();
    assertFalse(iSpec.getIRI().isPresent());

    assertTrue(iSpec.getRequiredSemanticTypes().contains(XSD.INT.stringValue()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertTrue(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getValue().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());

    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals(10, iSpec.getValue().get());
    assertEquals(10, iSpec.getDefaultValue().get());
  }

  @Test
  public void testReadArtifactProfileWithStringOutput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "  sh:property <urn:input-spec> .\n" +
            " <urn:input-spec> \n" +
            "    a ex:ExampleIOSpecification ;\n" +
            "    sh:path hmas:hasOutput;\n" +
            "    sh:datatype xs:string ;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:minCount 1 ;\n" +
            "    sh:order 5 ;\n" +
            "    sh:hasValue \"10\" ;\n" +
            "    sh:defaultValue \"10\" ;\n" +
            " .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getOutputSpecification();
    assertTrue(i.isPresent());

    StringSpecification iSpec = (StringSpecification) i.get();
    assertTrue(iSpec.getIRI().isPresent());
    assertEquals("urn:input-spec", iSpec.getIRIAsString().get());
    assertTrue(iSpec.getSemanticTypes().contains("http://example.org/ExampleIOSpecification"));
    assertTrue(iSpec.getRequiredSemanticTypes().contains(XSD.STRING.stringValue()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertTrue(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getValue().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());

    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals("10", iSpec.getValue().get());
    assertEquals("10", iSpec.getDefaultValue().get());
  }

  @Test
  public void testReadArtifactProfileWithListValueOutput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix saref: <https://saref.etsi.org/core/> .\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile;\n" +
            "  hmas:exposesSignifier [ a hmas:Signifier, ex:ExampleSignifier;\n" +
            "      hmas:signifies [ a sh:NodeShape;\n" +
            "          sh:class hmas:ActionExecution;\n" +
            "          sh:property [\n" +
            "              sh:path prov:used;\n" +
            "              sh:minCount \"1\"^^xs:int;\n" +
            "              sh:maxCount \"1\"^^xs:int;\n" +
            "              sh:hasValue ex:httpForm\n" +
            "            ], [ a sh:Shape;\n" +
            "              sh:name \"Label\" ;\n" +
            "              sh:description \"Description\" ;\n" +
            "              sh:order 5 ;\n" +
            "              sh:defaultValue ex:example-list;\n" +
            "              sh:hasValue ex:example-list;\n" +
            "              sh:class saref:State, rdf:List;\n" +
            "              sh:maxCount \"1\"^^xs:int;\n" +
            "              sh:path hmas:hasOutput\n" +
            "            ]\n" +
            "        ]\n" +
            "    ];\n" +
            "  hmas:isProfileOf [ a hmas:Artifact\n" +
            "    ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper>;\n" +
            "  htv:methodName \"GET\";\n" +
            "  hctl:forContentType \"application/json\" .\n" +
            "\n" +
            "ex:example-list a rdf:List;\n" +
            "  rdf:first \"first member\";\n" +
            "  rdf:rest ex:example-nested-list .\n" +
            "\n" +
            "ex:example-nested-list rdf:first \"second member\";\n" +
            "  rdf:rest rdf:nil .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getOutputSpecification();
    assertTrue(i.isPresent());

    ListSpecification iSpec = (ListSpecification) i.get();
    assertFalse(iSpec.getIRI().isPresent());
    assertTrue(iSpec.getRequiredSemanticTypes().contains(RDF.LIST.stringValue()));
    assertTrue(iSpec.getRequiredSemanticTypes().contains("https://saref.etsi.org/core/State"));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertFalse(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getValue().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());

    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals("http://example.org/example-list", iSpec.getValueAsString().get());
    assertEquals("http://example.org/example-list", iSpec.getDefaultValueAsString().get());

    assertEquals(0, iSpec.getMemberSpecifications().size());
  }

  @Test
  public void testReadArtifactProfileWithListOutput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix saref: <https://saref.etsi.org/core/> .\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile;\n" +
            "  hmas:exposesSignifier ex:signifier;\n" +
            "  hmas:isProfileOf [ a hmas:Artifact\n" +
            "    ] .\n\n" +
            "ex:signifier a hmas:Signifier, ex:ExampleSignifier;\n" +
            "  hmas:signifies ex:moveGripperSpecification .\n\n" +
            "ex:moveGripperSpecification a sh:NodeShape;\n" +
            "  sh:class hmas:ActionExecution, ex:ExampleActionExecution;\n" +
            "  sh:property [\n" +
            "      sh:path prov:used;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:hasValue ex:httpForm\n" +
            "    ], [\n" +
            "      sh:qualifiedValueShape ex:listShape;\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path hmas:hasOutput\n" +
            "    ] .\n\n" +
            "ex:httpForm a hctl:Form;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper>;\n" +
            "  htv:methodName \"GET\";\n" +
            "  hctl:forContentType \"application/json\" .\n\n" +
            "ex:listShape a sh:Shape, ex:ExampleListSpecification;\n" +
            "  sh:class saref:State, rdf:List;\n" +
            "  sh:property [\n" +
            "      sh:qualifiedValueShape [ a sh:Shape, ex:ExampleListSpecification;\n" +
            "          sh:class saref:State, rdf:List;\n" +
            "          sh:property [\n" +
            "              sh:qualifiedValueShape [ a sh:Shape, ex:ExampleListSpecification;\n" +
            "                  sh:class saref:State, rdf:List;\n" +
            "                  sh:property [ a sh:Shape;\n" +
            "                      sh:hasValue rdf:nil;\n" +
            "                      sh:datatype xs:anyURI;\n" +
            "                      sh:minCount \"1\"^^xs:int;\n" +
            "                      sh:maxCount \"1\"^^xs:int;\n" +
            "                      sh:path rdf:rest\n" +
            "                    ], [ a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "                      sh:datatype xs:int;\n" +
            "                      sh:minCount \"1\"^^xs:int;\n" +
            "                      sh:maxCount \"1\"^^xs:int;\n" +
            "                      sh:path rdf:first\n" +
            "                    ]\n" +
            "                ];\n" +
            "              sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "              sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "              sh:path rdf:rest\n" +
            "            ], [ a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "              sh:datatype xs:int;\n" +
            "              sh:minCount \"1\"^^xs:int;\n" +
            "              sh:maxCount \"1\"^^xs:int;\n" +
            "              sh:path rdf:first\n" +
            "            ]\n" +
            "        ];\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:rest\n" +
            "    ], [ a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "      sh:datatype xs:int;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:first\n" +
            "    ] .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getOutputSpecification();
    assertTrue(i.isPresent());

    ListSpecification iSpec = (ListSpecification) i.get();
    assertTrue(iSpec.getIRI().isPresent());
    assertEquals("http://example.org/listShape", iSpec.getIRIAsString().get());
    assertTrue(iSpec.getSemanticTypes().contains("http://example.org/ExampleListSpecification"));
    assertTrue(iSpec.getRequiredSemanticTypes().contains(RDF.LIST.stringValue()));
    assertTrue(iSpec.getRequiredSemanticTypes().contains("https://saref.etsi.org/core/State"));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());
    assertTrue(iSpec.isRequired());
    assertEquals(3, iSpec.getMemberSpecifications().size());

    List<IOSpecification> memberSpecs = iSpec.getMemberSpecifications();
    for (IOSpecification memberSpec : memberSpecs) {
      IntegerSpecification intSpec = (IntegerSpecification) memberSpec;
      assertTrue(intSpec.getSemanticTypes().contains("http://example.org/ExampleFirstSpecification"));
      assertTrue(intSpec.getRequiredSemanticTypes().contains(XSD.INT.stringValue()));
      assertTrue(intSpec.isRequired());
    }
  }

  @Test
  public void testReadArtifactProfileWithNestedListOutput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix saref: <https://saref.etsi.org/core/> .\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile;\n" +
            "  hmas:exposesSignifier ex:signifier;\n" +
            "  hmas:isProfileOf [ a hmas:Artifact\n" +
            "    ] .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier, ex:ExampleSignifier;\n" +
            "  hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape;\n" +
            "  sh:class hmas:ActionExecution, ex:ExampleActionExecution;\n" +
            "  sh:property [\n" +
            "      sh:path prov:used;\n" +
            "      sh:minCount \"1\"^^xs:int;\n" +
            "      sh:maxCount \"1\"^^xs:int;\n" +
            "      sh:hasValue ex:httpForm\n" +
            "    ], [\n" +
            "      sh:qualifiedValueShape ex:listShape;\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path hmas:hasOutput\n" +
            "    ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper>;\n" +
            "  htv:methodName \"GET\";\n" +
            "  hctl:forContentType \"application/json\" .\n" +
            "\n" +
            "ex:listShape a sh:Shape, ex:ExampleListSpecification;\n" +
            "  sh:class saref:State, rdf:List;\n" +
            "  sh:property [\n" +
            "      sh:qualifiedValueShape [ a sh:Shape, ex:ExampleListSpecification;\n" +
            "          sh:class saref:State, rdf:List;\n" +
            "          sh:property [ a sh:Shape;\n" +
            "              sh:hasValue rdf:nil;\n" +
            "              sh:datatype xs:anyURI;\n" +
            "              sh:minCount \"1\"^^xs:int;\n" +
            "              sh:maxCount \"1\"^^xs:int;\n" +
            "              sh:path rdf:rest\n" +
            "            ], [\n" +
            "              sh:qualifiedValueShape ex:nestedListShape;\n" +
            "              sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "              sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "              sh:path rdf:first\n" +
            "            ]\n" +
            "        ];\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:rest\n" +
            "    ], [\n" +
            "      sh:qualifiedValueShape ex:nestedListShape;\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:first\n" +
            "    ] .\n" +
            "\n" +
            "ex:nestedListShape a sh:Shape, ex:ExampleNestedListSpecification;\n" +
            "  sh:class rdf:List;\n" +
            "  sh:property [\n" +
            "      sh:qualifiedValueShape [ a sh:Shape, ex:ExampleNestedListSpecification;\n" +
            "          sh:class rdf:List;\n" +
            "          sh:property [ a sh:Shape;\n" +
            "              sh:hasValue rdf:nil;\n" +
            "              sh:datatype xs:anyURI;\n" +
            "              sh:minCount \"1\"^^xs:int;\n" +
            "              sh:maxCount \"1\"^^xs:int;\n" +
            "              sh:path rdf:rest\n" +
            "            ], ex:integer-member-spec\n" +
            "        ];\n" +
            "      sh:qualifiedMinCount \"1\"^^xs:int;\n" +
            "      sh:qualifiedMaxCount \"1\"^^xs:int;\n" +
            "      sh:path rdf:rest\n" +
            "    ], ex:integer-member-spec .\n" +
            "\n" +
            "ex:integer-member-spec a sh:Shape, ex:ExampleFirstSpecification;\n" +
            "  sh:datatype xs:int;\n" +
            "  sh:minCount \"1\"^^xs:int;\n" +
            "  sh:maxCount \"1\"^^xs:int;\n" +
            "  sh:path rdf:first .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getOutputSpecification();
    assertTrue(i.isPresent());

    ListSpecification iSpec = (ListSpecification) i.get();
    assertTrue(iSpec.getIRI().isPresent());
    assertEquals("http://example.org/listShape", iSpec.getIRIAsString().get());
    assertTrue(iSpec.getRequiredSemanticTypes().contains(RDF.LIST.stringValue()));
    assertTrue(iSpec.getRequiredSemanticTypes().contains("https://saref.etsi.org/core/State"));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertTrue(iSpec.isRequired());
    assertEquals(2, iSpec.getMemberSpecifications().size());

    List<IOSpecification> memberSpecs = iSpec.getMemberSpecifications();
    for (IOSpecification memberSpec : memberSpecs) {
      ListSpecification listSpec = (ListSpecification) memberSpec;
      assertTrue(listSpec.getSemanticTypes().contains("http://example.org/ExampleNestedListSpecification"));
      assertTrue(listSpec.getRequiredSemanticTypes().contains(RDF.LIST.stringValue()));
      assertTrue(listSpec.isRequired());
      assertEquals(2, listSpec.getMemberSpecifications().size());
      List<IOSpecification> nestedMemberSpecs = listSpec.getMemberSpecifications();
      for (IOSpecification nestedMemberSpec : nestedMemberSpecs) {
        IntegerSpecification intSpec = (IntegerSpecification) nestedMemberSpec;
        assertTrue(intSpec.getSemanticTypes().contains("http://example.org/ExampleFirstSpecification"));
        assertTrue(intSpec.getRequiredSemanticTypes().contains(XSD.INT.stringValue()));
        assertTrue(intSpec.isRequired());
      }
    }
  }

  @Test
  public void testReadArtifactProfileWithDataTypeInput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "  sh:property <urn:input-spec> .\n" +
            " <urn:input-spec> \n" +
            "    sh:path hmas:hasInput;\n" +
            "    sh:class hmas:ResourceProfile ;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:minCount 1 ;\n" +
            "    sh:order 5 ;\n" +
            "    sh:hasValue ex:myResourceProfile ;\n" +
            "    sh:defaultValue ex:myResourceProfile ;\n" +
            " .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());

    ValueSpecification iSpec = (ValueSpecification) i.get();
    assertTrue(iSpec.getIRI().isPresent());
    assertEquals("urn:input-spec", iSpec.getIRIAsString().get());

    assertTrue(iSpec.getRequiredSemanticTypes().contains(CORE.TERM.RESOURCE_PROFILE.toString()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertTrue(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());
    assertTrue(iSpec.getValue().isPresent());

    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals("http://example.org/myResourceProfile", iSpec.getValueAsString().get());
    assertEquals("http://example.org/myResourceProfile", iSpec.getDefaultValueAsString().get());

    IRI myResourceProfileIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/myResourceProfile");
    assertEquals(myResourceProfileIRI, iSpec.getValue().get());
    assertEquals(myResourceProfileIRI, iSpec.getDefaultValue().get());
  }

  @Test
  public void testReadArtifactProfileWithAnyURIUnspecifiedInput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "  sh:property <urn:input-spec> .\n" +
            " <urn:input-spec> \n" +
            "    sh:path hmas:hasInput;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:minCount 1 ;\n" +
            "    sh:order 5 ;\n" +
            "    sh:hasValue ex:myResourceProfile ;\n" +
            "    sh:defaultValue ex:myResourceProfile ;\n" +
            " .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());

    ValueSpecification iSpec = (ValueSpecification) i.get();
    assertTrue(iSpec.getIRI().isPresent());
    assertEquals("urn:input-spec", iSpec.getIRIAsString().get());

    assertTrue(iSpec.getRequiredSemanticTypes().contains(XSD.ANYURI.stringValue()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertTrue(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertTrue(iSpec.getDefaultValue().isPresent());
    assertTrue(iSpec.getValue().isPresent());


    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
    assertEquals("http://example.org/myResourceProfile", iSpec.getValueAsString().get());
    assertEquals("http://example.org/myResourceProfile", iSpec.getDefaultValueAsString().get());

    IRI myResourceProfileIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/myResourceProfile");
    assertEquals(myResourceProfileIRI, iSpec.getValue().get());
    assertEquals(myResourceProfileIRI, iSpec.getDefaultValue().get());
  }

  @Test
  public void testReadArtifactProfileWithAnyURIInput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
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
            "  sh:property <urn:input-spec> .\n" +
            " <urn:input-spec> \n" +
            "    sh:path hmas:hasInput;\n" +
            "    sh:name \"Label\" ;\n" +
            "    sh:datatype xs:anyURI;\n" +
            "    sh:description \"Description\" ;\n" +
            "    sh:minCount 1 ;\n" +
            "    sh:order 5 ;\n" +
            " .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"PUT\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    ActionSpecification actionSpec = signifier.getActionSpecification();
    Optional<IOSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());

    ValueSpecification iSpec = (ValueSpecification) i.get();
    assertTrue(iSpec.getIRI().isPresent());
    assertEquals("urn:input-spec", iSpec.getIRIAsString().get());

    assertTrue(iSpec.getRequiredSemanticTypes().contains(XSD.ANYURI.stringValue()));
    assertEquals(SHACL.SHAPE, iSpec.getTypeAsIRI());

    assertTrue(iSpec.isRequired());
    assertTrue(iSpec.getName().isPresent());
    assertTrue(iSpec.getDescription().isPresent());
    assertTrue(iSpec.getOrder().isPresent());
    assertFalse(iSpec.getDefaultValue().isPresent());
    assertFalse(iSpec.getValue().isPresent());


    assertEquals("Label", iSpec.getName().get());
    assertEquals("Description", iSpec.getDescription().get());
    assertEquals(5, iSpec.getOrder().get());
  }

  @Test
  public void testReadArtifactProfileWithQualifiedOutput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix saref: <https://saref.etsi.org/core/> .\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "  hmas:isProfileOf [ a hmas:Artifact ];\n" +
            "  hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier, ex:ExampleSignifier ;\n" +
            "  hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape, ex:ExampleActionSpecification ;\n" +
            "  sh:class hmas:ActionExecution, ex:ExampleActionExecution ;\n" +
            "  sh:property [\n" +
            "    sh:path prov:used ;\n" +
            "    sh:minCount 1;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:hasValue ex:httpForm ;\n" +
            "  ] ;\n" +
            "  sh:property <urn:input-spec>.\n" +
            "" +
            "<urn:input-spec> \n" +
            "    sh:path hmas:hasOutput;\n" +
            "    sh:qualifiedValueShape ex:gripperJointShape ;\n" +
            "    sh:qualifiedMinCount 1 ;\n" +
            "    sh:qualifiedMaxCount 1 \n" +
            " .\n" +
            "\n" +
            "ex:gripperJointShape a sh:NodeShape, ex:ExampleQualifiedValueSpecification ;\n" +
            "  sh:class ex:GripperJoint, saref:State ;\n" +
            "  sh:property [ a ex:ExampleIntSpecification ;\n" +
            "    sh:path ex:hasGripperValue ;\n" +
            "    sh:minCount 1;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:name \"Gripper\" ;\n" +
            "    sh:datatype xs:int\n" +
            "  ] ;\n" +
            "  sh:property [ a ex:ExampleDoubleSpecification ;\n" +
            "    sh:path ex:hasSpeedValue ;\n" +
            "    sh:name \"Speed\" ;\n" +
            "    sh:datatype xs:double\n" +
            "  ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"GET\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertTrue(signifier.getSemanticTypes().contains("http://example.org/ExampleSignifier"));
    assertTrue(signifier.getSemanticTypes().contains(CORE.TERM.SIGNIFIER.toString()));
    ActionSpecification actionSpec = signifier.getActionSpecification();

    assertEquals(2, actionSpec.getSemanticTypes().size());
    assertTrue(actionSpec.getSemanticTypes().contains("http://example.org/ExampleActionSpecification"));
    assertTrue(actionSpec.getRequiredSemanticTypes().contains("http://example.org/ExampleActionExecution"));

    Optional<IOSpecification> i = actionSpec.getOutputSpecification();
    assertTrue(i.isPresent());
    QualifiedValueSpecification input = (QualifiedValueSpecification) i.get();

    assertTrue(input.getIRIAsString().isPresent());
    assertEquals("http://example.org/gripperJointShape", input.getIRIAsString().get());

    assertTrue(input.getSemanticTypes().contains("http://example.org/ExampleQualifiedValueSpecification"));
    assertEquals(2, input.getRequiredSemanticTypes().size());
    assertTrue(input.getRequiredSemanticTypes().contains("http://example.org/GripperJoint"));
    assertTrue(input.getRequiredSemanticTypes().contains("https://saref.etsi.org/core/State"));

    Map<String, IOSpecification> properties = input.getPropertySpecifications();
    assertEquals(2, properties.size());
    assertTrue(properties.containsKey("http://example.org/hasGripperValue"));
    assertTrue(properties.containsKey("http://example.org/hasSpeedValue"));

    IntegerSpecification gripperValueSpec = (IntegerSpecification) properties.get("http://example.org/hasGripperValue");
    DoubleSpecification speedValueSpec = (DoubleSpecification) properties.get("http://example.org/hasSpeedValue");

    assertTrue(gripperValueSpec.getSemanticTypes().contains("http://example.org/ExampleIntSpecification"));
    assertTrue(gripperValueSpec.isRequired());
    assertTrue(gripperValueSpec.getName().isPresent());
    assertEquals("Gripper", gripperValueSpec.getName().get());

    assertTrue(speedValueSpec.getSemanticTypes().contains("http://example.org/ExampleDoubleSpecification"));
    assertFalse(speedValueSpec.isRequired());
    assertTrue(speedValueSpec.getName().isPresent());
    assertEquals("Speed", speedValueSpec.getName().get());
  }

  @Test
  public void testReadArtifactProfileWithQualifiedListInput() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "@prefix saref: <https://saref.etsi.org/core/> .\n" +
            "@prefix ex: <http://example.org/> .\n" +
            "@prefix xs: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix htv: <http://www.w3.org/2011/http#> .\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            "  hmas:isProfileOf [ a hmas:Artifact ];\n" +
            "  hmas:exposesSignifier ex:signifier .\n" +
            "\n" +
            "ex:signifier a hmas:Signifier, ex:ExampleSignifier ;\n" +
            "  hmas:signifies ex:moveGripperSpecification .\n" +
            "\n" +
            "ex:moveGripperSpecification a sh:NodeShape, ex:ExampleActionSpecification ;\n" +
            "  sh:class hmas:ActionExecution, ex:ExampleActionExecution ;\n" +
            "  sh:property [\n" +
            "    sh:path prov:used ;\n" +
            "    sh:minCount 1;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:hasValue ex:httpForm ;\n" +
            "  ] ;\n" +
            "  sh:property <urn:input-spec>.\n" +
            "" +
            "<urn:input-spec> \n" +
            "    sh:path hmas:hasInput;\n" +
            "    sh:qualifiedValueShape ex:listShape ;\n" +
            "    sh:qualifiedMinCount 1 ;\n" +
            "    sh:qualifiedMaxCount 1 \n" +
            " .\n" +
            "\n" +
            "ex:listShape a sh:NodeShape, ex:ExampleListSpecification ;\n" +
            "  sh:class ex:List, saref:State ;\n" +
            "  sh:property [ a ex:ExampleFirstSpecification ;\n" +
            "    sh:path rdf:first ;\n" +
            "    sh:minCount 1;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:datatype xs:int\n" +
            "  ] ;\n" +
            "  sh:property [ a ex:ExampleRestSpecification ;\n" +
            "    sh:path rdf:rest ;\n" +
            "    sh:qualifiedValueShape ex:restListShape ;\n" +
            "    sh:qualifiedMinCount 1 ;\n" +
            "    sh:qualifiedMaxCount 1 \n" +
            "  ] .\n" +
            "\n" +
            "ex:restListShape a sh:NodeShape, ex:ExampleListSpecification ;\n" +
            "  sh:class ex:List, saref:State ;\n" +
            "  sh:property [ a ex:ExampleFirstSpecification ;\n" +
            "    sh:path rdf:first ;\n" +
            "    sh:minCount 1;\n" +
            "    sh:maxCount 1 ;\n" +
            "    sh:datatype xs:int\n" +
            "  ] ;\n" +
            "  sh:property [ a ex:ExampleRestSpecification ;\n" +
            "    sh:path rdf:rest ;\n" +
            "    sh:hasValue rdf:nil\n" +
            "  ] .\n" +
            "\n" +
            "ex:httpForm a hctl:Form ;\n" +
            "  hctl:hasTarget <https://api.interactions.ics.unisg.ch/leubot1/v1.3.4/gripper> ;\n" +
            "  hctl:forContentType \"application/json\" ;\n" +
            "  htv:methodName \"GET\" .";

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
    assertEquals(ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
    Set<Signifier> signifiers = profile.getExposedSignifiers();

    List<Signifier> signifiersList = new ArrayList<>(signifiers);
    Signifier signifier = signifiersList.get(0);
    assertTrue(signifier.getSemanticTypes().contains("http://example.org/ExampleSignifier"));
    assertTrue(signifier.getSemanticTypes().contains(CORE.TERM.SIGNIFIER.toString()));
    ActionSpecification actionSpec = signifier.getActionSpecification();

    Optional<IOSpecification> i = actionSpec.getInputSpecification();
    assertTrue(i.isPresent());
    QualifiedValueSpecification input = (QualifiedValueSpecification) i.get();

    assertTrue(input.getIRIAsString().isPresent());
    assertEquals("http://example.org/listShape", input.getIRIAsString().get());
    assertTrue(input.isRequired());

    assertTrue(input.getSemanticTypes().contains("http://example.org/ExampleListSpecification"));
    assertEquals(2, input.getRequiredSemanticTypes().size());
    assertTrue(input.getRequiredSemanticTypes().contains("http://example.org/List"));
    assertTrue(input.getRequiredSemanticTypes().contains("https://saref.etsi.org/core/State"));

    Map<String, IOSpecification> properties = input.getPropertySpecifications();
    assertEquals(2, properties.size());
    assertTrue(properties.containsKey("http://www.w3.org/1999/02/22-rdf-syntax-ns#first"));
    assertTrue(properties.containsKey("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest"));

    IntegerSpecification memberSpec = (IntegerSpecification) properties
            .get("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
    QualifiedValueSpecification restSpec = (QualifiedValueSpecification) properties
            .get("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");

    assertTrue(memberSpec.getSemanticTypes().contains("http://example.org/ExampleFirstSpecification"));
    assertTrue(memberSpec.isRequired());

    assertTrue(restSpec.getSemanticTypes().contains("http://example.org/ExampleListSpecification"));
    assertEquals(2, restSpec.getRequiredSemanticTypes().size());
    assertTrue(restSpec.getRequiredSemanticTypes().contains("http://example.org/List"));
    assertTrue(restSpec.getRequiredSemanticTypes().contains("https://saref.etsi.org/core/State"));

    Map<String, IOSpecification> remainingProperties = restSpec.getPropertySpecifications();
    assertEquals(2, remainingProperties.size());
    assertTrue(remainingProperties.containsKey("http://www.w3.org/1999/02/22-rdf-syntax-ns#first"));
    assertTrue(remainingProperties.containsKey("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest"));

    IntegerSpecification secondMemberSpec = (IntegerSpecification) remainingProperties
            .get("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
    ValueSpecification nilSpec = (ValueSpecification) remainingProperties
            .get("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");
    assertTrue(nilSpec.getValueAsString().isPresent());
    assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil", nilSpec
            .getValueAsString().get());
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

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
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

    ResourceProfile profile = ResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
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
  }

}