package io.github.rrevo.orientdbex;

import io.github.rrevo.orientdbex.schemahybrid.TypedElementExample;
import io.github.rrevo.orientdbex.schemaless.ElementExample;

public class Main {

    private static int index;

    public static void main(String[] args) {
        ElementExample elementExample = new ElementExample(getNextDbUrl());
        elementExample.run();

        TypedElementExample typedElementExample = new TypedElementExample(getNextDbUrl());
        typedElementExample.run();
    }

    private static String getNextDbUrl() {
        return "memory:db" + index++;
    }
}
