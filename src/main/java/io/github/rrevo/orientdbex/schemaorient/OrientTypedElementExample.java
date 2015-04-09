package io.github.rrevo.orientdbex.schemaorient;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.*;
import io.github.rrevo.orientdbex.core.AbstractExample;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * See http://www.orientechnologies.com/docs/last/Graph-Schema.html
 */
public class OrientTypedElementExample extends AbstractExample {

    public OrientTypedElementExample(String url) {
        super(url);
    }

    @Override
    protected void runGraphExamples(OrientGraphFactory graphFactory) {
        createSchema(graphFactory);
        populateData(graphFactory);
        findData(graphFactory);
    }

    private void createSchema(OrientGraphFactory graphFactory) {
        OrientGraphNoTx graph = graphFactory.getNoTx();
        OrientVertexType personType = graph.createVertexType("Person");
        personType.createProperty("name", OType.STRING).setMandatory(true).setNotNull(true);
        personType.createProperty("age", OType.INTEGER);
        OrientEdgeType knowsEdge = graph.createEdgeType("Knows");
        knowsEdge.createProperty("since", OType.DATE);
    }

    private void populateData(OrientGraphFactory graphFactory) {
        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex me = graph.addVertex("class:Person", "name", "Moi");
            Vertex you = graph.addVertex("class:Person", "name", "Vous");
            you.setProperty("age", 1);
            you.setProperty("height", 2);
            OrientEdge knows = graph.addEdge("class:Knows", me, you, "Knows");
            knows.setProperty("since", new Date());

            Vertex random = graph.addVertex(null, "name", "Someone");

            graph.commit();
        } finally {
            graph.shutdown();
        }
    }

    private void findData(OrientGraphFactory graphFactory) {
        OrientGraph graph = graphFactory.getTx();
        try {
            OrientVertex me = findPersonByName(graph, "Moi");
            System.out.printf("Found Me: %s%n", me);
            Preconditions.checkArgument(me.getLabel().equals("Person"));

            Iterator<Vertex> vertices = me.getVertices(Direction.OUT, "Knows").iterator();
            OrientVertex you = (OrientVertex) vertices.next();
            Preconditions.checkNotNull(you);
            System.out.printf("Found You: %s%n", you);
            Preconditions.checkArgument(!vertices.hasNext());
            Preconditions.checkArgument(you.getLabel().equals("Person"));
            int age = you.getProperty("age");
            Preconditions.checkArgument(age == 1);
            int height = you.getProperty("height");
            Preconditions.checkArgument(height == 2);

            OrientVertex someone = findVertexByName(graph, "Someone");
            Preconditions.checkNotNull(someone);
        } finally {
            graph.shutdown();
        }
    }

    OrientVertex findPersonByName(OrientGraph graph, String name) {
        try (OrientDynaElementIterable resultsIter = graph.command(
                new OCommandSQL("select from Person where name = '" + name + "'")).execute()) {
            List<Object> results = Lists.newArrayList(resultsIter);
            if (results.isEmpty()) {
                return null;
            } else {
                Preconditions.checkArgument(results.size() == 1);
                Vertex vertex = (Vertex) results.get(0);
                Preconditions.checkArgument(vertex.getProperty("name").equals(name));
                OrientVertex orientVertex = (OrientVertex) vertex;
                return orientVertex;
            }
        }
    }

    OrientVertex findVertexByName(OrientGraph graph, String name) {
        try (OrientDynaElementIterable resultsIter = graph.command(
                new OCommandSQL("select from V where name = '" + name + "'")).execute()) {
            List<Object> results = Lists.newArrayList(resultsIter);
            if (results.isEmpty()) {
                return null;
            } else {
                Preconditions.checkArgument(results.size() == 1);
                Vertex vertex = (Vertex) results.get(0);
                Preconditions.checkArgument(vertex.getProperty("name").equals(name));
                OrientVertex orientVertex = (OrientVertex) vertex;
                return orientVertex;
            }
        }
    }
}
