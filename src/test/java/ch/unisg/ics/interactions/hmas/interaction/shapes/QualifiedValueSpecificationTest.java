package ch.unisg.ics.interactions.hmas.interaction.shapes;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class QualifiedValueSpecificationTest {

  @Test
  public void testQualifiedValueSpecification() {

    QualifiedValueSpecification spec = new QualifiedValueSpecification.Builder()
            .setIRIAsString("http://example.org/specification")
            .addRequiredSemanticTypes(Set.of(CORE.HOSTABLE.stringValue(), "http://example.org/resource"))
            .addRequiredSemanticType(CORE.RESOURCE_PROFILE.stringValue())
            .build();

    assertTrue(spec.getIRI().isPresent());
    assertTrue(spec.getIRIAsString().isPresent());
    assertEquals("http://example.org/specification", spec.getIRIAsString().get());

    assertEquals(3, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(CORE.HOSTABLE.stringValue()));
    assertTrue(spec.getRequiredSemanticTypes().contains("http://example.org/resource"));
    assertTrue(spec.getRequiredSemanticTypes().contains(CORE.RESOURCE_PROFILE.stringValue()));

    assertEquals(0, spec.getPropertySpecifications().size());
  }

  @Test
  public void testQualifiedValueSpecificationPrimitiveProperty() {

    StringSpecification propertySpec = new StringSpecification.Builder()
            .setName("Label")
            .build();

    QualifiedValueSpecification spec = new QualifiedValueSpecification.Builder()
            .addRequiredSemanticType(CORE.ARTIFACT.stringValue())
            .addPropertySpecification("https://purl.org/hmas/agents-artifacts#hasName", propertySpec)
            .addPropertySpecification("https://purl.org/hmas/agents-artifacts#hasClass", propertySpec)
            .addPropertySpecification("https://purl.org/hmas/agents-artifacts#hasInitialisationParameter", propertySpec)
            .build();

    assertFalse(spec.getIRI().isPresent());

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(CORE.ARTIFACT.stringValue()));

    assertEquals(3, spec.getPropertySpecifications().size());
    Map<String, IOSpecification> properties = spec.getPropertySpecifications();
    assertTrue(properties.containsKey("https://purl.org/hmas/agents-artifacts#hasName"));
    assertTrue(properties.containsKey("https://purl.org/hmas/agents-artifacts#hasClass"));
    assertTrue(properties.containsKey("https://purl.org/hmas/agents-artifacts#hasInitialisationParameter"));

    assertEquals(propertySpec, properties.get("https://purl.org/hmas/agents-artifacts#hasName"));
    assertEquals(propertySpec, properties.get("https://purl.org/hmas/agents-artifacts#hasClass"));
    assertEquals(propertySpec, properties.get("https://purl.org/hmas/agents-artifacts#hasInitialisationParameter"));
  }

  @Test
  public void testQualifiedValueSpecificationValueSpecification() {

    ValueSpecification profileSpec = new ValueSpecification.Builder()
            .addRequiredSemanticType(CORE.RESOURCE_PROFILE.stringValue())
            .build();

    ValueSpecification bodySpec = new ValueSpecification.Builder()
            .addRequiredSemanticType(CORE.ARTIFACT.stringValue())
            .build();

    ValueSpecification platformSpec = new ValueSpecification.Builder()
            .addRequiredSemanticType(CORE.HMAS_PLATFORM.stringValue())
            .build();

    QualifiedValueSpecification spec = new QualifiedValueSpecification.Builder()
            .addRequiredSemanticType(CORE.ARTIFACT.stringValue())
            .addPropertySpecification(CORE.TERM.HAS_PROFILE.toString(), profileSpec)
            .addPropertySpecification("https://purl.org/hmas/agents-artifacts#hasBody", bodySpec)
            .addPropertySpecification(CORE.TERM.IS_HOSTED_ON.toString(), platformSpec)
            .build();

    assertFalse(spec.getIRI().isPresent());

    assertEquals(1, spec.getRequiredSemanticTypes().size());
    assertTrue(spec.getRequiredSemanticTypes().contains(CORE.ARTIFACT.stringValue()));

    assertEquals(3, spec.getPropertySpecifications().size());
    Map<String, IOSpecification> properties = spec.getPropertySpecifications();
    assertTrue(properties.containsKey("https://purl.org/hmas/agents-artifacts#hasBody"));
    assertTrue(properties.containsKey(CORE.TERM.HAS_PROFILE.toString()));
    assertTrue(properties.containsKey(CORE.TERM.IS_HOSTED_ON.toString()));

    assertEquals(bodySpec, properties.get("https://purl.org/hmas/agents-artifacts#hasBody"));
    assertEquals(profileSpec, properties.get(CORE.TERM.HAS_PROFILE.toString()));
    assertEquals(platformSpec, properties.get(CORE.TERM.IS_HOSTED_ON.toString()));
  }
}
