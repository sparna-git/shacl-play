package fr.sparna.rdf.jena.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.sparna.rdf.jena.QueryExecutionLocalService;

/**
 * Test class for NodeShape.targets(Resource, QueryExecutionService) method.
 * Tests all target cases: sh:targetClass, sh:targetSubjectsOf, sh:targetObjectsOf,
 * sh:targetNode, and sh:target+sh:select.
 */
public class NodeShapeTargetsTest {

    private static final String EX = "http://example.org/";
    private static final String RICO = "https://www.ica.org/standards/RiC/ontology#";
    private static final String TYPE = "https://data.archives.haute-garonne.fr/type/";
    
    private static Model model;
    private static QueryExecutionLocalService queryExecutionService;

    @BeforeClass
    public static void setUp() throws Exception {
        model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, 
            NodeShapeTargetsTest.class.getClassLoader().getResourceAsStream("NodeShapeTargetsTestData.ttl"), 
            Lang.TURTLE);
        queryExecutionService = new QueryExecutionLocalService(model);
    }

    // ============================================
    // sh:targetClass tests
    // ============================================

    @Test
    public void testTargetsWithTargetClass_Matching() {
        Resource targetClassShape = model.getResource(EX + "TargetClassShape");
        NodeShape nodeShape = new NodeShape(targetClassShape);
        
        Resource activity1 = model.getResource(EX + "activity1");
        
        Assert.assertTrue("Should target activity1 as it has type rico:Activity",
            nodeShape.targets(activity1, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetClass_NotMatching() {
        Resource targetClassShape = model.getResource(EX + "TargetClassShape");
        NodeShape nodeShape = new NodeShape(targetClassShape);
        
        Resource notActivity = model.getResource(EX + "notActivity");
        
        Assert.assertFalse("Should not target non-activity resource",
            nodeShape.targets(notActivity, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetClass_SubClassOf() {
        Resource subClassShape = model.getResource(EX + "SubClassShape");
        NodeShape nodeShape = new NodeShape(subClassShape);
        
        // Test direct instance
        Resource myClassInstance = model.getResource(EX + "myClassInstance");
        Assert.assertTrue("Should target direct instance of MyClass",
            nodeShape.targets(myClassInstance, queryExecutionService));
        
        // Test subclass instance - ChildClass is a subclass of MyClass
        // The query checks if resource type is subclass of MyClass via rdfs:subClassOf*
        Resource myChildClassInstance = model.getResource(EX + "myChildClassInstance");
        Assert.assertTrue("Should target instance of ChildClass (subclass of MyClass via rdfs:subClassOf*)",
            nodeShape.targets(myChildClassInstance, queryExecutionService));
        
        // Test superclass instance - ParentClass is a superclass of MyClass
        // The query checks if resource type is subclass of MyClass, so ParentClass instance should NOT match
        Resource myParentClassInstance = model.getResource(EX + "myParentClassInstance");
        Assert.assertFalse("Should not target instance of ParentClass (superclass of MyClass)",
            nodeShape.targets(myParentClassInstance, queryExecutionService));
        
        // Test grandparent instance
        Resource myGrandParentClassInstance = model.getResource(EX + "myGrandParentClassInstance");
        Assert.assertFalse("Should not target instance of GrandParentClass (superclass of MyClass)",
            nodeShape.targets(myGrandParentClassInstance, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetClass_SuperClass() {
        Resource superClassShape = model.getResource(EX + "SuperClassShape");
        NodeShape nodeShape = new NodeShape(superClassShape);
        
        // Test direct instance
        Resource myParentClassInstance = model.getResource(EX + "myParentClassInstance");
        Assert.assertTrue("Should target direct instance of ParentClass",
            nodeShape.targets(myParentClassInstance, queryExecutionService));
        
        // Test subclass instance - MyClass is a subclass of ParentClass
        Resource myClassInstance = model.getResource(EX + "myClassInstance");
        Assert.assertTrue("Should target instance of MyClass (subclass of ParentClass via rdfs:subClassOf*)",
            nodeShape.targets(myClassInstance, queryExecutionService));
        
        // Test nested subclass instance - ChildClass is a subclass of MyClass which is a subclass of ParentClass
        Resource myChildClassInstance = model.getResource(EX + "myChildClassInstance");
        Assert.assertTrue("Should target instance of ChildClass (nested subclass of ParentClass via rdfs:subClassOf*)",
            nodeShape.targets(myChildClassInstance, queryExecutionService));
        
        // Test grandparent instance
        Resource myGrandParentClassInstance = model.getResource(EX + "myGrandParentClassInstance");
        Assert.assertFalse("Should not target instance of GrandParentClass (superclass of ParentClass)",
            nodeShape.targets(myGrandParentClassInstance, queryExecutionService));
    }

    @Test
    public void testTargetsWithClassShape_Itself() {
        Resource classShape = model.getResource(EX + "ClassShape");
        NodeShape nodeShape = new NodeShape(classShape);
        
        Resource classShapeDirect = model.getResource(EX + "ClassShapeDirect");
        
        Assert.assertTrue("Should target instance of the class that the shape itself is",
            nodeShape.targets(classShapeDirect, queryExecutionService));
    }

    // ============================================
    // sh:targetSubjectsOf tests
    // ============================================

    @Test
    public void testTargetsWithTargetSubjectsOf_Matching() {
        Resource targetSubjectsOfShape = model.getResource(EX + "TargetSubjectsOfShape");
        NodeShape nodeShape = new NodeShape(targetSubjectsOfShape);
        
        Resource subject1 = model.getResource(EX + "subject1");
        
        Assert.assertTrue("Should target subject1 as it is a subject of ex:property1",
            nodeShape.targets(subject1, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetSubjectsOf_NotMatching() {
        Resource targetSubjectsOfShape = model.getResource(EX + "TargetSubjectsOfShape");
        NodeShape nodeShape = new NodeShape(targetSubjectsOfShape);
        
        Resource subject2 = model.getResource(EX + "subject2");
        
        Assert.assertFalse("Should not target subject2 as it is not a subject of ex:property1",
            nodeShape.targets(subject2, queryExecutionService));
    }

    // ============================================
    // sh:targetObjectsOf tests
    // ============================================

    @Test
    public void testTargetsWithTargetObjectsOf_Matching() {
        Resource targetObjectsOfShape = model.getResource(EX + "TargetObjectsOfShape");
        NodeShape nodeShape = new NodeShape(targetObjectsOfShape);
        
        Resource object2 = model.getResource(EX + "object2");
        
        Assert.assertTrue("Should target object2 as it is an object of ex:property2",
            nodeShape.targets(object2, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetObjectsOf_NotMatching() {
        Resource targetObjectsOfShape = model.getResource(EX + "TargetObjectsOfShape");
        NodeShape nodeShape = new NodeShape(targetObjectsOfShape);
        
        Resource object1 = model.getResource(EX + "object1");
        
        Assert.assertFalse("Should not target object1 as it is not an object of ex:property2",
            nodeShape.targets(object1, queryExecutionService));
    }

    // ============================================
    // sh:targetNode tests
    // ============================================

    @Test
    public void testTargetsWithTargetNode_Matching() {
        Resource targetNodeShape = model.getResource(EX + "TargetNodeShape");
        NodeShape nodeShape = new NodeShape(targetNodeShape);
        
        Resource activity1 = model.getResource(EX + "activity1");
        
        Assert.assertTrue("Should target activity1 as it is explicitly targeted by sh:targetNode",
            nodeShape.targets(activity1, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetNode_NotMatching() {
        Resource targetNodeShape = model.getResource(EX + "TargetNodeShape");
        NodeShape nodeShape = new NodeShape(targetNodeShape);
        
        Resource activity2 = model.getResource(EX + "activity2");
        
        Assert.assertFalse("Should not target activity2 as it is not the target node",
            nodeShape.targets(activity2, queryExecutionService));
    }

    // ============================================
    // sh:target + sh:select tests
    // ============================================

    @Test
    public void testTargetsWithTargetSelect_Matching() {
        Resource targetSelectShape = model.getResource(EX + "TargetSelectShape");
        NodeShape nodeShape = new NodeShape(targetSelectShape);
        
        // activity1 has type rico:Activity and rico:hasActivityType type:acpro
        Resource activity1 = model.getResource(EX + "activity1");
        
        Assert.assertTrue("Should target activity1 as it matches the SPARQL query",
            nodeShape.targets(activity1, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetSelect_NotMatching_WrongActivityType() {
        Resource targetSelectShape = model.getResource(EX + "TargetSelectShape");
        NodeShape nodeShape = new NodeShape(targetSelectShape);
        
        // activity2 has type rico:Activity but NOT type:acpro
        Resource activity2 = model.getResource(EX + "activity2");
        
        Assert.assertFalse("Should not target activity2 as it doesn't match the SPARQL query (wrong activity type)",
            nodeShape.targets(activity2, queryExecutionService));
    }

    @Test
    public void testTargetsWithTargetSelect_NotMatching_WrongType() {
        Resource targetSelectShape = model.getResource(EX + "TargetSelectShape");
        NodeShape nodeShape = new NodeShape(targetSelectShape);
        
        // notActivity is not a rico:Activity
        Resource notActivity = model.getResource(EX + "notActivity");
        
        Assert.assertFalse("Should not target non-activity as it doesn't match the SPARQL query",
            nodeShape.targets(notActivity, queryExecutionService));
    }

    // ============================================
    // Null resource test
    // ============================================

    @Test
    public void testTargetsWithNullResource() {
        Resource targetClassShape = model.getResource(EX + "TargetClassShape");
        NodeShape nodeShape = new NodeShape(targetClassShape);
        
        Assert.assertFalse("Should return false for null resource",
            nodeShape.targets(null, queryExecutionService));
    }

    // ============================================
    // Multiple targets on same shape test
    // ============================================

    @Test
    public void testTargetsWithMultipleTargets_MatchingAny() {
        // Create a shape with multiple target types
        Model testModel = ModelFactory.createDefaultModel();
        testModel.add(model);
        
        Resource multiShape = testModel.createResource(EX + "MultiTargetShape");
        multiShape.addProperty(model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), 
            model.createProperty("http://www.w3.org/ns/shacl#NodeShape"));
        multiShape.addProperty(
            model.createProperty("http://www.w3.org/ns/shacl#targetClass"), 
            model.createResource(RICO + "Activity"));
        multiShape.addProperty(
            model.createProperty("http://www.w3.org/ns/shacl#targetNode"), 
            model.createResource(EX + "activity1"));
        
        NodeShape nodeShape = new NodeShape(multiShape);
        QueryExecutionLocalService testService = new QueryExecutionLocalService(testModel);
        
        // Should match via targetClass
        Resource activity2 = testModel.getResource(EX + "activity2");
        Assert.assertTrue("Should target activity2 via sh:targetClass",
            nodeShape.targets(activity2, testService));
        
        // Should match via targetNode
        Resource activity1 = testModel.getResource(EX + "activity1");
        Assert.assertTrue("Should target activity1 via sh:targetNode",
            nodeShape.targets(activity1, testService));
    }

    // ============================================
    // Test with no targets defined
    // ============================================

    @Test
    public void testTargetsWithNoTargetsDefined() {
        // Create a shape with no targets
        Model testModel = ModelFactory.createDefaultModel();
        testModel.add(model);
        
        Resource noTargetShape = testModel.createResource(EX + "NoTargetShape");
        noTargetShape.addProperty(model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), 
            model.createProperty("http://www.w3.org/ns/shacl#NodeShape"));
        
        NodeShape nodeShape = new NodeShape(noTargetShape);
        QueryExecutionLocalService testService = new QueryExecutionLocalService(testModel);
        
        Resource activity1 = testModel.getResource(EX + "activity1");
        Assert.assertFalse("Should not target anything when no targets are defined",
            nodeShape.targets(activity1, testService));
    }

    // ============================================
    // Test using the simple targets(Resource) method
    // ============================================

    @Test
    public void testTargetsWithLocalModel_UsingSimpleMethod() {
        Resource targetClassShape = model.getResource(EX + "TargetClassShape");
        NodeShape nodeShape = new NodeShape(targetClassShape);
        
        Resource activity1 = model.getResource(EX + "activity1");
        
        // This should work because activity1's model is the same as the shape's model
        Assert.assertTrue("Should target activity1 using simple targets(Resource) method",
            nodeShape.targets(activity1));
    }
}
