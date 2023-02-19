/*
 * Copyright (C) 2013-2023 The JavaParser Team.
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

package com.github.javaparser.printer.lexicalpreservation;

//import com.github.javaparser.GeneratedJavaParserConstants;
import com.github.javaparser.printer.concretesyntaxmodel.CsmAttribute;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.body.TypeDeclaration;
        import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoreaBranchesTest extends AbstractLexicalPreservingTest {

    @Test
    public void testMoreBranches() {

        considerCode("package com.wangym.test;\nclass A{ }");
        CsmAttribute csa = new CsmAttribute(ObservableProperty.TYPE);
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        int expected = 19; //because it is a class, value can be found in GeneratedJavaParserConstants
        assertEquals(expected, csa.getTokenType(types.get(0), "class", "class A"));
    }
    @Test
    public void testID3() {

        considerCode("public class A{ }");
        CsmAttribute csa = new CsmAttribute(ObservableProperty.TYPE);
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            csa.getTokenType(types.get(0), "class B", "class A");
        }, "Runtime exception was expected");

        assertEquals("Attribute 'type' does not corresponding to any expected value. Text: class B", exception.getMessage());
    }

    @Test
    public void testEvenMore() {
        considerCode("class A extends B{ }");
        CsmAttribute csa = new CsmAttribute(ObservableProperty.KEYWORD);
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        int expected = 27; //because its a KEYWORD token
        assertEquals(expected, csa.getTokenType(types.get(0), "extends", "extends"));
    }

    @Test
    public void testOperator() {
        considerCode("class A{\n int a = 1 + 2; \n }");
        CsmAttribute csa = new CsmAttribute(ObservableProperty.OPERATOR);
        NodeList<TypeDeclaration<?>> types = cu.getTypes();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            csa.getTokenType(types.get(0), "+", "plus");
        }, "Runtime exception was expected");

        assertEquals("Attribute 'operator' does not corresponding to any expected value. Text: plus", exception.getMessage());

    }
}
