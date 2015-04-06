package io.github.rrevo.orientdbex;

import com.google.common.base.Preconditions;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {
        OrientGraphFactory graphFactory = new OrientGraphFactory("memory:db");
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
