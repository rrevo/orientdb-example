package io.github.rrevo.orientdbex.schemahybrid;

import com.google.common.base.Preconditions;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.VertexFrame;
import io.github.rrevo.orientdbex.core.AbstractExample;

import java.util.HashMap;
import java.util.Map;

/**
 * See https://github.com/tinkerpop/frames/wiki/Getting-Started
 */
public class TypedElementExample extends AbstractExample {

    public static final String NAME_CHANDLER = "Chandler";
    public static final String NAME_JOEY = "Joey";
    public static final String NAME_ROSS = "Ross";

    public TypedElementExample(String url) {
        super(url);
    }

    @Override
    protected void runGraphExamples(OrientGraphFactory graphFactory) {
        OrientGraphNoTx graphNoTx = graphFactory.getNoTx();
        graphNoTx.createEdgeType("knows");

        OrientGraph graph = graphFactory.getTx();
        FramedGraphFactory factory = new FramedGraphFactory();
        FramedGraph manager = factory.create(graph);
        try {
            populateData(graph, manager);
            findData(graph, manager);
        } finally {
            graph.shutdown();
        }
    }

    private void populateData(OrientGraph graph, FramedGraph manager) {
        Person chandler = (Person) manager.frame(graph.addVertex(null), Person.class);
        chandler.setName(NAME_CHANDLER);
        chandler.setAge(12);

        Person joey = (Person) manager.frame(graph.addVertex(null), Person.class);
        joey.setName(NAME_JOEY);
        joey.setAge(15);

        Person ross = (Person) manager.frame(graph.addVertex(null), Person.class);
        ross.setName(NAME_ROSS);
        ross.setAge(14);

        chandler.addKnowsPerson(joey);
        chandler.addKnowsPerson(ross);
        ross.addKnowsPerson(joey);
        ross.addKnowsPerson(chandler);
        joey.addKnowsPerson(ross);
        joey.addKnowsPerson(chandler);
        graph.commit();
    }

    private void findData(OrientGraph graph, FramedGraph manager) {
        Person chandler = findPersonByName(graph, manager, NAME_CHANDLER);
        System.out.printf("Found Chandler: %s%n", chandler);

        VertexFrame chandlerFrame = (VertexFrame) chandler;
        Vertex chandlerVertex = chandlerFrame.asVertex();
        System.out.printf("Vertex: %s%n", chandlerVertex);


        Person ross = findPersonByName(graph, manager, NAME_ROSS);
        System.out.printf("Found Ross: %s%n", ross);

        Person joey = findPersonByName(graph, manager, NAME_JOEY);
        System.out.printf("Found Joey: %s%n", joey);

        checkKnows(chandler, ross, joey);
        checkKnows(ross, chandler, joey);
        checkKnows(joey, ross, chandler);
    }

    Person findPersonByName(OrientGraph graph, FramedGraph manager, String name) {
        for (Vertex vertex : graph.getVertices()) {
            Person person = (Person) manager.frame(vertex, Person.class);
            if (person.getName().equals(name)) {
                return person;
            }
        }
        return null;
    }

    Map<String, Person> getKnown(Person person) {
        Map<String, Person> people = new HashMap<>();
        person.getKnowsPeople().forEach(p -> people.put(p.getName(), p));
        return people;
    }

    void checkKnows(Person person, Person ... others) {
        Map<String, Person> people = new HashMap<>(getKnown(person));
        Preconditions.checkArgument(people.size() == others.length);
        for (Person other : others) {
            people.remove(other.getName());
        }
        Preconditions.checkArgument(people.isEmpty());
    }
}
