/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2023 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser.ast.expr;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.utils.TestParser;
import org.junit.jupiter.api.Test;

import static com.github.javaparser.StaticJavaParser.parseBodyDeclaration;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectCreationExprTest {
    @Test
    void aaa() {
        Expression e = TestParser.parseExpression("new @Test N()");
        assertEquals("new @Test N()", e.toString());
    }

    @Test
    void BodyHasSameNameAsClass() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        ObjectCreationExpr oc2 = new ObjectCreationExpr();
        NodeList<Expression> list1 = new NodeList<>();
        list1.add(parseExpression("new OtherClass()"));
        list1.add(parseExpression("new InnerClass()"));
        objectCreationExpr.setArguments(list1);

        NodeList<Expression> list2 = new NodeList<>();
        list2.add(parseExpression("new IntegerClass()"));
        oc2.setArguments(list2);

        NodeList<BodyDeclaration<?>> body = new NodeList<>();
        body.add(parseBodyDeclaration("int x = 0;"));
        objectCreationExpr.setAnonymousClassBody(body);

        Node node = objectCreationExpr.getAnonymousClassBody().get().get(0);
        Node Replacement = objectCreationExpr.getAnonymousClassBody().get().get(0);

        assertEquals(true, objectCreationExpr.replace(node,Replacement));
    }

    @Test
    void nodeisNull() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        ObjectCreationExpr oc2 = new ObjectCreationExpr();
        NodeList<Expression> list1 = new NodeList<>();
        list1.add(parseExpression("new OtherClass()"));
        list1.add(parseExpression("new InnerClass()"));
        objectCreationExpr.setArguments(list1);

        NodeList<Expression> list2 = new NodeList<>();
        list2.add(parseExpression("new IntegerClass()"));
        oc2.setArguments(list2);

        NodeList<BodyDeclaration<?>> body = new NodeList<>();
        body.add(parseBodyDeclaration("int x = 0;"));
        objectCreationExpr.setAnonymousClassBody(body);

        Node node = null;
        Node Replacement = objectCreationExpr.getAnonymousClassBody().get().get(0);

        assertEquals(false, objectCreationExpr.replace(node,Replacement));
    }

    @Test
    void argumentEqualsNode() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        ObjectCreationExpr oc2 = new ObjectCreationExpr();
        NodeList<Expression> list1 = new NodeList<>();
        list1.add(parseExpression("new OtherClass()"));
        list1.add(parseExpression("new InnerClass()"));
        objectCreationExpr.setArguments(list1);

        NodeList<Expression> list2 = new NodeList<>();
        list2.add(parseExpression("new IntegerClass()"));
        oc2.setArguments(list2);

        Node node = objectCreationExpr.getArguments().get(0);
        Node Replacement = oc2.getArguments().get(0).asObjectCreationExpr();

        assertEquals(true, objectCreationExpr.replace(node,Replacement));
    }

    @Test
    void scopeEqualsNode() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        ObjectCreationExpr oc2 = new ObjectCreationExpr();
        objectCreationExpr.setScope(parseExpression("new OtherClass()"));
        oc2.setScope(parseExpression("new IntegerClass()"));

        Node node = objectCreationExpr.getScope().get();
        Node Replacement = oc2.getScope().get();

        assertEquals(true, objectCreationExpr.replace(node,Replacement));
    }
}
