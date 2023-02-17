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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.printer.ConcreteSyntaxModel;
import com.github.javaparser.printer.SourcePrinter;
import com.github.javaparser.printer.lexicalpreservation.LexicalDifferenceCalculator;
import com.github.javaparser.printer.lexicalpreservation.changes.Change;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.github.javaparser.printer.lexicalpreservation.LexicalDifferenceCalculator.toToken;

public class CsmList implements CsmElement {

    private final ObservableProperty property;

    private final CsmElement separatorPost;

    private final CsmElement separatorPre;

    private final CsmElement preceeding;

    private final CsmElement following;

    public ObservableProperty getProperty() {
        return property;
    }

    public CsmElement getSeparatorPost() {
        return separatorPost;
    }

    public CsmElement getSeparatorPre() {
        return separatorPre;
    }

    public CsmElement getPreceeding() {
        return preceeding;
    }

    public CsmElement getFollowing() {
        return following;
    }

    public CsmList(ObservableProperty property, CsmElement separator) {
        this(property, new CsmNone(), separator, new CsmNone(), new CsmNone());
    }

    public CsmList(ObservableProperty property) {
        this(property, new CsmNone(), new CsmNone(), new CsmNone(), new CsmNone());
    }

    public CsmList(ObservableProperty property, CsmElement separatorPre, CsmElement separatorPost, CsmElement preceeding, CsmElement following) {
        this.property = property;
        this.separatorPre = separatorPre;
        this.separatorPost = separatorPost;
        this.preceeding = preceeding;
        this.following = following;
    }

    @Override
    public void prettyPrint(Node node, SourcePrinter printer) {
        if (property.isAboutNodes()) {
            NodeList<? extends Node> nodeList = property.getValueAsMultipleReference(node);
            if (nodeList == null) {
                return;
            }
            if (!nodeList.isEmpty() && preceeding != null) {
                preceeding.prettyPrint(node, printer);
            }
            for (int i = 0; i < nodeList.size(); i++) {
                if (separatorPre != null && i != 0) {
                    separatorPre.prettyPrint(node, printer);
                }
                ConcreteSyntaxModel.genericPrettyPrint(nodeList.get(i), printer);
                if (separatorPost != null && i != (nodeList.size() - 1)) {
                    separatorPost.prettyPrint(node, printer);
                }
            }
            if (!nodeList.isEmpty() && following != null) {
                following.prettyPrint(node, printer);
            }
        } else {
            Collection<?> values = property.getValueAsCollection(node);
            if (values == null) {
                return;
            }
            if (!values.isEmpty() && preceeding != null) {
                preceeding.prettyPrint(node, printer);
            }
            for (Iterator<?> it = values.iterator(); it.hasNext(); ) {
                if (separatorPre != null && it.hasNext()) {
                    separatorPre.prettyPrint(node, printer);
                }
                printer.print(PrintingHelper.printToString(it.next()));
                if (separatorPost != null && it.hasNext()) {
                    separatorPost.prettyPrint(node, printer);
                }
            }
            if (!values.isEmpty() && following != null) {
                following.prettyPrint(node, printer);
            }
        }
    }

    @Override
    public void calculateSyntaxModelForNode(Node node, List<CsmElement> elements, Change change) {
        if (this.getProperty().isAboutNodes()) {
            Object rawValue = change.getValue(this.getProperty(), node);
            NodeList<?> nodeList;
            if (rawValue instanceof Optional) {
                Optional<?> optional = (Optional<?>) rawValue;
                if (optional.isPresent()) {
                    if (!(optional.get() instanceof NodeList)) {
                        throw new IllegalStateException("Expected NodeList, found " + optional.get().getClass().getCanonicalName());
                    }
                    nodeList = (NodeList<?>) optional.get();
                } else {
                    nodeList = new NodeList<>();
                }
            } else {
                if (!(rawValue instanceof NodeList)) {
                    throw new IllegalStateException("Expected NodeList, found " + rawValue.getClass().getCanonicalName());
                }
                nodeList = (NodeList<?>) rawValue;
            }
            if (!nodeList.isEmpty()) {
                this.getPreceeding().calculateSyntaxModelForNode(node, elements, change);
                for (int i = 0; i < nodeList.size(); i++) {
                    if (i != 0) {
                        this.getSeparatorPre().calculateSyntaxModelForNode(node, elements, change);
//                        calculatedSyntaxModelForNode(csmList.getSeparatorPre(), node, elements, change);
                    }
                    elements.add(new LexicalDifferenceCalculator.CsmChild(nodeList.get(i)));
                    if (i != (nodeList.size() - 1)) {
                        this.getSeparatorPost().calculateSyntaxModelForNode(node, elements, change);
//                        calculatedSyntaxModelForNode(csmList.getSeparatorPost(), node, elements, change);
                    }
                }
                this.getFollowing().calculateSyntaxModelForNode(node, elements, change);
//                calculatedSyntaxModelForNode(csmList.getFollowing(), node, elements, change);
            }
        } else {
            Collection<?> collection = (Collection<?>) change.getValue(this.getProperty(), node);
            if (!collection.isEmpty()) {
//                calculatedSyntaxModelForNode(csmList.getPreceeding(), node, elements, change);
                this.getPreceeding().calculateSyntaxModelForNode(node, elements, change);

                boolean first = true;
                for (Iterator<?> it = collection.iterator(); it.hasNext(); ) {
                    if (!first) {
//                        calculatedSyntaxModelForNode(csmList.getSeparatorPre(), node, elements, change);
                        this.getSeparatorPre().calculateSyntaxModelForNode(node, elements, change);

                    }
                    Object value = it.next();
                    if (value instanceof Modifier) {
                        Modifier modifier = (Modifier) value;
                        elements.add(new CsmToken(toToken(modifier)));
                    } else {
                        throw new UnsupportedOperationException(it.next().getClass().getSimpleName());
                    }
                    if (it.hasNext()) {
//                        calculatedSyntaxModelForNode(csmList.getSeparatorPost(), node, elements, change);
                        this.getSeparatorPost().calculateSyntaxModelForNode(node, elements, change);

                    }
                    first = false;
                }
                this.getFollowing().calculateSyntaxModelForNode(node, elements, change);
//                calculatedSyntaxModelForNode(csmList.getFollowing(), node, elements, change);
            }
        }

    }

    @Override
    public String toString() {
        return String.format("%s(property:%s)", this.getClass().getSimpleName(), getProperty());
    }
}
