/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Arrays;
import java.util.List;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Node;

/**
 * Generates builder subclass source code for rules that describe list nodes.
 *
 * @since 1.0
 */
final class ListNodeBuilderConstructor extends NodeConstructor {
    /**
     * The 'boolean' string.
     */
    private static final String STR_BOOLEAN = "boolean";

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    ListNodeBuilderConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        this.createFragmentWithSetter();
        this.createNoDataSetter();
        this.createSetterChildrenList();
        this.createValidator();
        this.createCreator();
    }

    /**
     * Creates the method 'setChildrenList'.
     */
    private void createSetterChildrenList() {
        final Descriptor descriptor = (Descriptor) this.getRule().getComposition().get(0);
        final String type = descriptor.getType();
        final Klass klass = this.getKlass();
        final Field field = new Field(
            "List of child nodes",
            String.format("List<%s>", type),
            "children"
        );
        field.setInitExpr("Collections.emptyList()");
        klass.addField(field);
        final Method method = new Method("setChildrenList");
        method.makeOverridden();
        method.addArgument("List<Node>", "list");
        method.setReturnType(ListNodeBuilderConstructor.STR_BOOLEAN);
        final List<String> code = Arrays.asList(
            "boolean result = true;",
            String.format(
                "final List<%s> clarified = new ArrayList<>(list.size());",
                type
            ),
            String.format(
                "for (final Node node : list) { if (node instanceof %s) {",
                type
            ),
            String.format(
                "clarified.add((%s) node); } else { result = false;\nbreak; } }",
                type
            ),
            "if (result) { this.children = Collections.unmodifiableList(clarified); }",
            "return result;"
        );
        method.setCode(String.join("\n", code));
        klass.addMethod(method);
    }

    /**
     * Creates the method 'isValid'.
     */
    private void createValidator() {
        final Method method = new Method("isValid");
        method.makeOverridden();
        method.setReturnType(ListNodeBuilderConstructor.STR_BOOLEAN);
        method.setCode("return true;");
        this.getKlass().addMethod(method);
    }

    /**
     * Creates the method 'createNode'.
     */
    private void createCreator() {
        final String type = this.getRule().getType();
        final Method method = new Method("createNode");
        method.makeOverridden();
        method.setReturnType(type);
        final List<String> code = Arrays.asList(
            String.format("final %s node = new %s();", type, type),
            "node.fragment = this.fragment;",
            "node.children = this.children;",
            "return node;"
        );
        method.setCode(String.join("\n", code));
        this.getKlass().addMethod(method);
    }
}