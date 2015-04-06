package io.github.rrevo.orientdbex.schemaless;

import com.google.common.base.Preconditions;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import io.github.rrevo.orientdbex.core.AbstractExample;

import java.util.Iterator;

public class ElementExample extends AbstractExample {

    public ElementExample(String url) {
        super(url);
    }

    @Override
    protected void runGraphExamples(OrientGraphFactory graphFactory) {
        OrientGraph graph = graphFactory.getTx();
        try {
            populateData(graph);
            findData(graph);
        } finally {
            graph.shutdown();
        }
    }

    private void findData(OrientGraph graph) {
        Vertex luca = null;
        for (Vertex vertex : graph.getVertices()) {
            if (vertex.getProperty("name").equals("Luca")) {
                luca = vertex;
                break;
            }
        }
        Preconditions.checkNotNull(luca);
        System.out.printf("Found Luca: %s%n", luca);
        Iterator<Vertex> vertices = luca.getVertices(Direction.OUT, "knows").iterator();
        Vertex marko = vertices.next();
        Preconditions.checkNotNull(marko);
        System.out.printf("Found Marko: %s%n", marko);
        Preconditions.checkArgument(!vertices.hasNext());
    }

    private void populateData(OrientGraph graph) {
        Vertex luca = graph.addVertex(null);
        luca.setProperty( "name", "Luca" );
        Vertex marko = graph.addVertex(null);
        marko.setProperty( "name", "Marko" );
        graph.addEdge(null, luca, marko, "knows");
        graph.commit();
    }
}
