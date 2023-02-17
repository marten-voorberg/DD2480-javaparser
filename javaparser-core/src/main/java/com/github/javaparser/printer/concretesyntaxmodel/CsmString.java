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
package com.github.javaparser.printer.concretesyntaxmodel;

import com.github.javaparser.GeneratedJavaParserConstants;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.printer.SourcePrinter;
import com.github.javaparser.printer.lexicalpreservation.changes.Change;
import com.github.javaparser.printer.lexicalpreservation.changes.PropertyChange;

import java.util.List;

public class CsmString implements CsmElement {

    private final ObservableProperty property;

    public CsmString(ObservableProperty property) {
        this.property = property;
    }

    public ObservableProperty getProperty() {
        return property;
    }

    @Override
    public void prettyPrint(Node node, SourcePrinter printer) {
        printer.print("\"");
        printer.print(property.getValueAsStringAttribute(node));
        printer.print("\"");
    }

    @Override
    public void calculateSyntaxModelForNode(Node node, List<CsmElement> elements, Change change) {
        if (node instanceof StringLiteralExpr) {
            // fix #2382:
            // This method calculates the syntax model _after_ the change has been applied.
            // If the given change is a PropertyChange, the returned model should
            // contain the new value, otherwise the original/current value should be used.
            if (change instanceof PropertyChange) {
                elements.add(new CsmToken(GeneratedJavaParserConstants.STRING_LITERAL, "\"" + ((PropertyChange) change).getNewValue() + "\""));
            } else {
                elements.add(new CsmToken(GeneratedJavaParserConstants.STRING_LITERAL, "\"" + ((StringLiteralExpr) node).getValue() + "\""));
            }
        } else if (node instanceof TextBlockLiteralExpr) {
            // FIXME: csm should be CsmTextBlock -- See also #2677
            if (change instanceof PropertyChange) {
                elements.add(new CsmToken(GeneratedJavaParserConstants.TEXT_BLOCK_LITERAL, "\"\"\"" + ((PropertyChange) change).getNewValue() + "\"\"\""));
            } else {
                elements.add(new CsmToken(GeneratedJavaParserConstants.TEXT_BLOCK_LITERAL, "\"\"\"" + ((TextBlockLiteralExpr) node).getValue() + "\"\"\""));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s(property:%s)", this.getClass().getSimpleName(), getProperty());
    }
}
