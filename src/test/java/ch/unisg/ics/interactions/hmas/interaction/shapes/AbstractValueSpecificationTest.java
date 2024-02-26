package ch.unisg.ics.interactions.hmas.interaction.shapes;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.junit.jupiter.api.Test;

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

    ValueSpecification spec = new ValueSpecification.Builder(CORE.AGENT.stringValue())
            .setValueAsString("http://example.org/example#me")
            .build();

    assertFalse(spec.getName().isPresent());
    assertFalse(spec.getDescription().isPresent());
    assertFalse(spec.getOrder().isPresent());
    assertFalse(spec.isRequired());


    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(CORE.AGENT.stringValue()));

    assertTrue(spec.getValue().isPresent());
    assertEquals(exampleAgentIRI, spec.getValue().get());
    assertEquals("http://example.org/example#me", spec.getValueAsString().get());
  }

  @Test
  public void testValueSpecificationNoValue() {

    ValueSpecification spec = new ValueSpecification.Builder(CORE.AGENT.stringValue())
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

    String expectedMessage = "IRIs of DataTypes and node values must be valid.";
    assertTrue(ex.getMessage().contains(expectedMessage));

  }

  @Test
  public void testValueSpecificationDefaultValueConflict() {

    ValueSpecification.Builder specBuilder = new ValueSpecification.Builder()
            .setValue(CORE.AGENT);

    ValueSpecification.Builder specBuilderStr = new ValueSpecification.Builder()
            .setValueAsString(CORE.AGENT.stringValue());

    Exception ex1 = assertThrows(IllegalArgumentException.class, () -> {
      specBuilder.setDefaultValue(CORE.ARTIFACT);
    });

    Exception ex2 = assertThrows(IllegalArgumentException.class, () -> {
      specBuilder.setDefaultValueAsString(CORE.ARTIFACT.stringValue());
    });

    Exception ex1Str = assertThrows(IllegalArgumentException.class, () -> {
      specBuilderStr.setDefaultValue(CORE.ARTIFACT);
    });

    Exception ex2Str = assertThrows(IllegalArgumentException.class, () -> {
      specBuilderStr.setDefaultValueAsString(CORE.ARTIFACT.stringValue());
    });

    String expectedMessage = "The specified value (" + CORE.AGENT + ") has a conflict with the specified default value (" + CORE.ARTIFACT + ").";

    assertTrue(ex1.getMessage().contains(expectedMessage));
    assertTrue(ex2.getMessage().contains(expectedMessage));
    assertTrue(ex1Str.getMessage().contains(expectedMessage));
    assertTrue(ex2Str.getMessage().contains(expectedMessage));
  }

  @Test
  public void testValueSpecificationValueConflict() {

    ValueSpecification.Builder specBuilder = new ValueSpecification.Builder()
            .setDefaultValue(CORE.AGENT);

    ValueSpecification.Builder specBuilderStr = new ValueSpecification.Builder()
            .setDefaultValueAsString(CORE.AGENT.stringValue());

    Exception ex1 = assertThrows(IllegalArgumentException.class, () -> {
      specBuilder.setValue(CORE.ARTIFACT);
    });

    Exception ex2 = assertThrows(IllegalArgumentException.class, () -> {
      specBuilder.setValueAsString(CORE.ARTIFACT.stringValue());
    });

    Exception ex1Str = assertThrows(IllegalArgumentException.class, () -> {
      specBuilderStr.setValue(CORE.ARTIFACT);
    });

    Exception ex2Str = assertThrows(IllegalArgumentException.class, () -> {
      specBuilderStr.setValueAsString(CORE.ARTIFACT.stringValue());
    });

    String expectedMessage = "The specified value (" + CORE.ARTIFACT + ") has a conflict with the specified default value (" + CORE.AGENT + ").";

    assertTrue(ex1.getMessage().contains(expectedMessage));
    assertTrue(ex2.getMessage().contains(expectedMessage));
    assertTrue(ex1Str.getMessage().contains(expectedMessage));
    assertTrue(ex2Str.getMessage().contains(expectedMessage));

  }

  @Test
  public void testValueSpecificationPrimitiveValueConflict() {

    BooleanSpecification.Builder booleanBuilder = new BooleanSpecification.Builder()
            .setValue(true);

    DoubleSpecification.Builder doubleBuilder = new DoubleSpecification.Builder()
            .setValue(10.00);

    FloatSpecification.Builder floatBuilder = new FloatSpecification.Builder()
            .setValue(10.5f);

    IntegerSpecification.Builder integerBuilder = new IntegerSpecification.Builder()
            .setValue(10);

    StringSpecification.Builder stringBuilder = new StringSpecification.Builder()
            .setValue("string");

    assertThrows(IllegalArgumentException.class, () -> {
      booleanBuilder.setDefaultValue(false);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      doubleBuilder.setDefaultValue(2.00);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      floatBuilder.setDefaultValue(2.5f);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      integerBuilder.setDefaultValue(0);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      stringBuilder.setDefaultValue("another-string");
    });
  }

  @Test
  public void testValueSpecificationPrimitiveDefaultValueConflict() {

    BooleanSpecification.Builder booleanBuilder = new BooleanSpecification.Builder()
            .setDefaultValue(true);

    DoubleSpecification.Builder doubleBuilder = new DoubleSpecification.Builder()
            .setDefaultValue(10.00);

    FloatSpecification.Builder floatBuilder = new FloatSpecification.Builder()
            .setDefaultValue(10.5f);

    IntegerSpecification.Builder integerBuilder = new IntegerSpecification.Builder()
            .setDefaultValue(10);

    StringSpecification.Builder stringBuilder = new StringSpecification.Builder()
            .setDefaultValue("string");

    assertThrows(IllegalArgumentException.class, () -> {
      booleanBuilder.setValue(false);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      doubleBuilder.setValue(2.00);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      floatBuilder.setValue(2.5f);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      integerBuilder.setValue(0);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      stringBuilder.setValue("another-string");
    });
  }

  @Test
  public void testValueSpecificationInvalidDataType() {

    Exception ex = assertThrows(IllegalArgumentException.class, () -> {
      new ValueSpecification.Builder("invalid-iri");
    });

    String expectedMessage = "IRIs of DataTypes and node values must be valid.";
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
