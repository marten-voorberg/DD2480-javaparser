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

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.printer.ConcreteSyntaxModel;
import com.github.javaparser.printer.SourcePrinter;
import com.github.javaparser.printer.lexicalpreservation.LexicalDifferenceCalculator;
import com.github.javaparser.printer.lexicalpreservation.changes.Change;
import com.github.javaparser.printer.lexicalpreservation.changes.PropertyChange;

import java.util.List;

public class CsmSingleReference implements CsmElement {

    private final ObservableProperty property;

    public ObservableProperty getProperty() {
        return property;
    }

    public CsmSingleReference(ObservableProperty property) {
        this.property = property;
    }

    @Override
    public void prettyPrint(Node node, SourcePrinter printer) {
        Node child = property.getValueAsSingleReference(node);
        if (child != null) {
            ConcreteSyntaxModel.genericPrettyPrint(child, printer);
        }
    }

    @Override
    public void calculateSyntaxModelForNode(Node node, List<CsmElement> elements, Change change) {
        Node child;
        if (change instanceof PropertyChange && ((PropertyChange) change).getProperty() == this.getProperty()) {
            child = (Node) ((PropertyChange) change).getNewValue();
        } else {
            child = this.getProperty().getValueAsSingleReference(node);
        }
        if (child != null) {
            elements.add(new LexicalDifferenceCalculator.CsmChild(child));
        }
    }

    @Override
    public String toString() {
        return String.format("%s(property:%s)", this.getClass().getSimpleName(), getProperty());
    }
}
