package io.github.rrevo.orientdbex.core;

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public abstract class AbstractExample {

    private final String url;

    public AbstractExample(String url) {
        this.url = url;
    }

    public final void run() {
        OrientGraphFactory graphFactory = new OrientGraphFactory(url);
        runGraphExamples(graphFactory);
    }

    abstract protected void runGraphExamples(OrientGraphFactory graphFactory);
}
