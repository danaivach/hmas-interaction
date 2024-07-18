package ch.unisg.ics.interactions.hmas.interaction.validation;

import org.eclipse.rdf4j.common.exception.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ShaclValidationTest {
  @Test
  public void testValidation() {
    ShaclSail shaclSail = new ShaclSail(new MemoryStore());
    Repository repo = new SailRepository(shaclSail);

    try (RepositoryConnection connection = repo.getConnection()) {

      Reader shaclRules = new StringReader("""
              @prefix ex: <http://example.org/> .
              @prefix hmas: <https://purl.org/hmas/> .
              @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
              @prefix sh: <http://www.w3.org/ns/shacl#> .
              @prefix prov: <http://www.w3.org/ns/prov#> .
              @prefix xs: <http://www.w3.org/2001/XMLSchema#> .
              @prefix hctl: <https://www.w3.org/2019/wot/hypermedia#> .
              @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                        
              ex:moveGripper a sh:NodeShape;
              	sh:targetClass hmas:ActionExecution ;
               	sh:property [
              		sh:path prov:used ;
                      	sh:minCount 1;
                      	sh:maxCount 1 ;
                  		sh:or (
              			[ sh:hasValue ex:httpForm ]
              			[ sh:hasValue ex:coapForm ]
              	   	 ) ;
                	] ;
              	sh:property [
              		sh:path hmas:hasInput;
                  		sh:qualifiedValueShape ex:gripperJointShape ;
                  		sh:qualifiedMinCount 1 ;
                  		sh:qualifiedMaxCount 1 ;
                	] .
                        
              ex:gripperJointShape a sh:NodeShape ;
              	sh:targetClass ex:GripperJoint ;
              	sh:property [
              		    sh:path ex:hasGripperValue ;
                      sh:minCount 1;
                      sh:maxCount 1 ;
                      sh:datatype xs:integer ;
                      sh:minInclusive "40"^^xs:integer ;
                  ] .
              """);

      // add shapes
      connection.begin();
      connection.clear(RDF4J.SHACL_SHAPE_GRAPH);
      connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
      connection.commit();

      Reader shaclData = new StringReader("""
              @prefix ex: <http://example.org/> .
              @prefix hmas: <https://purl.org/hmas/> .
              @prefix prov: <http://www.w3.org/ns/prov#> .
              @prefix xs: <http://www.w3.org/2001/XMLSchema#> .
                        
              [] a hmas:ActionExecution ;
              	prov:used ex:httpForm ;
              	hmas:hasInput ex:gripperJoint.
                        
              ex:gripperJoint a ex:GripperJoint ;
              	ex:hasGripperValue "50"^^xs:integer.
              """);

      // add data
      connection.begin();
      connection.add(shaclData, "", RDFFormat.TURTLE);
      try {
        connection.commit();
      } catch (RepositoryException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof ValidationException) {
          Model validationReportModel = ((ValidationException) cause).validationReportAsModel();

          WriterConfig writerConfig = new WriterConfig()
                  .set(BasicWriterSettings.INLINE_BLANK_NODES, true)
                  .set(BasicWriterSettings.XSD_STRING_TO_PLAIN_LITERAL, true)
                  .set(BasicWriterSettings.PRETTY_PRINT, true);

          Rio.write(validationReportModel, System.out, RDFFormat.TURTLE, writerConfig);
        }
        throw exception;
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
