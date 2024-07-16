package ch.unisg.ics.interactions.hmas.interaction.shapes;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractValueSpecificationTest {

  @Test
  public void testBooleanSpecification() {
    BooleanSpecification spec = new BooleanSpecification.Builder()
            .setName("Label")
            .setDescription("Label explanation")
            .setOrder(0)
            .setRequired(true)
            .setValue(true)
            .setDefaultValue(true)
            .build();

    testAbstractValueMetadata(spec);

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(XSD.BOOLEAN.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals(true, spec.getValue().get());

    assertTrue(spec.getDefaultValue().isPresent());
    assertEquals(true, spec.getDefaultValue().get());
  }

  @Test
  public void testDoubleSpecification() {
    DoubleSpecification spec = new DoubleSpecification.Builder()
            .setName("Label")
            .setDescription("Label explanation")
            .setOrder(0)
            .setRequired(true)
            .setValue(10.00)
            .setDefaultValue(10.00)
            .build();

    testAbstractValueMetadata(spec);

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(XSD.DOUBLE.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals(10.00, spec.getValue().get());

    assertTrue(spec.getDefaultValue().isPresent());
    assertEquals(10.00, spec.getDefaultValue().get());
  }

  @Test
  public void testFloatSpecification() {
    FloatSpecification spec = new FloatSpecification.Builder()
            .setName("Label")
            .setDescription("Label explanation")
            .setOrder(0)
            .setRequired(true)
            .setValue(10.5f)
            .setDefaultValue(10.5f)
            .build();

    testAbstractValueMetadata(spec);

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(XSD.FLOAT.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals(10.5f, spec.getValue().get());

    assertTrue(spec.getDefaultValue().isPresent());
    assertEquals(10.5f, spec.getDefaultValue().get());
  }

  @Test
  public void testIntegerSpecification() {
    IntegerSpecification spec = new IntegerSpecification.Builder()
            .setName("Label")
            .setDescription("Label explanation")
            .setOrder(0)
            .setRequired(true)
            .setValue(10)
            .setDefaultValue(10)
            .build();

    testAbstractValueMetadata(spec);

    assertTrue(spec.getRequiredSemanticTypes().contains(XSD.INT.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals(10, spec.getValue().get());

    assertTrue(spec.getDefaultValue().isPresent());
    assertEquals(10, spec.getDefaultValue().get());
  }

  @Test
  public void testStringSpecification() {
    StringSpecification spec = new StringSpecification.Builder()
            .setName("Label")
            .setDescription("Label explanation")
            .setOrder(0)
            .setRequired(true)
            .setValue("string")
            .setDefaultValue("string")
            .build();

    testAbstractValueMetadata(spec);

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(XSD.STRING.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals("string", spec.getValue().get());

    assertTrue(spec.getDefaultValue().isPresent());
    assertEquals("string", spec.getDefaultValue().get());
  }

  @Test
  public void testListSpecification() {

    IntegerSpecification member1Spec = new IntegerSpecification.Builder()
            .build();

    ValueSpecification member2Spec = new ValueSpecification.Builder()
            .build();

    ListSpecification member3Spec = new ListSpecification.Builder()
            .addMemberSpecifications(Arrays.asList(member1Spec, member2Spec))
            .build();

    ListSpecification spec = new ListSpecification.Builder()
            .setName("Label")
            .setDescription("Label explanation")
            .setOrder(0)
            .setRequired(true)
            .setValueAsString("http://example.org/list")
            .setDefaultValueAsString("http://example.org/default-list")
            .addMemberSpecification(member1Spec)
            .addMemberSpecifications(Arrays.asList(member2Spec))
            .addMemberSpecification(member3Spec)
            .build();

    testAbstractValueMetadata(spec);

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(RDF.LIST.stringValue()));

    assertTrue(spec.getValue().isPresent());
    IRI listIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/list");
    assertEquals(listIRI, spec.getValue().get());

    assertTrue(spec.getDefaultValue().isPresent());
    IRI defaultListIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/default-list");
    assertEquals(defaultListIRI, spec.getDefaultValue().get());

    List<IOSpecification> memberSpecifications = spec.getMemberSpecifications();
    assertEquals(3, memberSpecifications.size());
    assertTrue(memberSpecifications.contains(member1Spec));
    assertTrue(memberSpecifications.contains(member2Spec));
    assertTrue(memberSpecifications.contains(member3Spec));

    List<IOSpecification> nestedMemberSpecifications = member3Spec.getMemberSpecifications();
    assertEquals(2, nestedMemberSpecifications.size());
    assertTrue(nestedMemberSpecifications.contains(member1Spec));
    assertTrue(nestedMemberSpecifications.contains(member2Spec));
  }

  @Test
  public void testValueSpecification() {

    IRI exampleAgentIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/example#me");

    ValueSpecification spec = new ValueSpecification.Builder()
            .setName("Label")
            .setDescription("Label explanation")
            .setOrder(0)
            .setRequired(true)
            .setValue(exampleAgentIRI)
            .setDefaultValue(exampleAgentIRI)
            .build();

    testAbstractValueMetadata(spec);

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(XSD.ANYURI.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals(exampleAgentIRI, spec.getValue().get());
    assertEquals("http://example.org/example#me", spec.getValueAsString().get());

    assertTrue(spec.getDefaultValue().isPresent());
    assertEquals(exampleAgentIRI, spec.getDefaultValue().get());
    assertEquals("http://example.org/example#me", spec.getDefaultValueAsString().get());
  }

  @Test
  public void testValueSpecificationCustomDataType() {

    IRI exampleAgentIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/example#me");

    ValueSpecification spec = new ValueSpecification.Builder()
            .addRequiredSemanticType(CORE.AGENT.stringValue())
            .setValueAsString("http://example.org/example#me")
            .build();

    assertFalse(spec.getName().isPresent());
    assertFalse(spec.getDescription().isPresent());
    assertFalse(spec.getOrder().isPresent());
    assertFalse(spec.isRequired());


    assertEquals(2, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(CORE.AGENT.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals(exampleAgentIRI, spec.getValue().get());
    assertEquals("http://example.org/example#me", spec.getValueAsString().get());
  }

  @Test
  public void testValueSpecificationNoValue() {

    ValueSpecification spec = new ValueSpecification.Builder()
            .addRequiredSemanticType(CORE.AGENT.stringValue())
            .build();

    assertFalse(spec.getValue().isPresent());
    assertFalse(spec.getValueAsString().isPresent());

  }

  @Test
  public void testValueSpecificationInvalidIRIValue() {

    ValueSpecification.Builder specBuilder = new ValueSpecification.Builder();

    Exception ex = assertThrows(IllegalArgumentException.class, () -> {
      specBuilder.setValueAsString("invalid-iri");
    });

    String expectedMessage = "IRIs of DataTypes, Classes and node values must be valid.";
    assertTrue(ex.getMessage().contains(expectedMessage));
  }

  private void testAbstractValueMetadata(AbstractValueSpecification specification) {
    assertTrue(specification.getName().isPresent());
    assertEquals("Label", specification.getName().get());

    assertTrue(specification.getDescription().isPresent());
    assertEquals("Label explanation", specification.getDescription().get());

    assertTrue(specification.getOrder().isPresent());
    assertEquals(0, specification.getOrder().get());

    assertEquals(true, specification.isRequired());
  }

}
