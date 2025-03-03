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

package com.github.javaparser.ast;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.utils.CodeGenerationUtils.mavenModuleRoot;
import static org.junit.jupiter.api.Assertions.*;

class CompilationUnitTest {
    @Test
    void issue578TheFirstCommentIsWithinTheCompilationUnit() {
        CompilationUnit compilationUnit = parse("// This is my class, with my comment\n" +
                "class A {\n" +
                "    static int a;\n" +
                "}");

        assertEquals(1, compilationUnit.getAllContainedComments().size());
    }

    @Test
    void testGetSourceRoot() throws IOException {
        Path sourceRoot = mavenModuleRoot(CompilationUnitTest.class).resolve(Paths.get("src", "test", "resources")).normalize();
        Path testFile = sourceRoot.resolve(Paths.get("com", "github", "javaparser", "storage", "Z.java"));

        CompilationUnit cu = parse(testFile);
        Path sourceRoot1 = cu.getStorage().get().getSourceRoot();
        assertEquals(sourceRoot, sourceRoot1);
    }

    @Test
    void testGetSourceRootWithBadPackageDeclaration() {
        assertThrows(RuntimeException.class, () -> {
            Path sourceRoot = mavenModuleRoot(CompilationUnitTest.class).resolve(Paths.get("src", "test", "resources")).normalize();
            Path testFile = sourceRoot.resolve(Paths.get("com", "github", "javaparser", "storage", "A.java"));
            CompilationUnit cu = parse(testFile);
            cu.getStorage().get().getSourceRoot();
        });

    }

    @Test
    void testGetSourceRootInDefaultPackage() throws IOException {
        Path sourceRoot = mavenModuleRoot(CompilationUnitTest.class).resolve(Paths.get("src", "test", "resources", "com", "github", "javaparser", "storage")).normalize();
        Path testFile = sourceRoot.resolve(Paths.get("B.java"));

        CompilationUnit cu = parse(testFile);
        Path sourceRoot1 = cu.getStorage().get().getSourceRoot();
        assertEquals(sourceRoot, sourceRoot1);
    }

    @Test
    void testGetPrimaryTypeName() throws IOException {
        Path sourceRoot = mavenModuleRoot(CompilationUnitTest.class).resolve(Paths.get("src", "test", "resources")).normalize();
        Path testFile = sourceRoot.resolve(Paths.get("com", "github", "javaparser", "storage", "PrimaryType.java"));
        CompilationUnit cu = parse(testFile);

        assertEquals("PrimaryType", cu.getPrimaryTypeName().get());
    }

    @Test
    void testNoPrimaryTypeName() {
        CompilationUnit cu = parse("class PrimaryType{}");

        assertFalse(cu.getPrimaryTypeName().isPresent());
    }

    @Test
    void testGetPrimaryType() throws IOException {
        Path sourceRoot = mavenModuleRoot(CompilationUnitTest.class).resolve(Paths.get("src", "test", "resources")).normalize();
        Path testFile = sourceRoot.resolve(Paths.get("com", "github", "javaparser", "storage", "PrimaryType.java"));
        CompilationUnit cu = parse(testFile);

        assertEquals("PrimaryType", cu.getPrimaryType().get().getNameAsString());
    }

    @Test
    void testNoPrimaryType() throws IOException {
        Path sourceRoot = mavenModuleRoot(CompilationUnitTest.class).resolve(Paths.get("src", "test", "resources")).normalize();
        Path testFile = sourceRoot.resolve(Paths.get("com", "github", "javaparser", "storage", "PrimaryType2.java"));
        CompilationUnit cu = parse(testFile);

        assertFalse(cu.getPrimaryType().isPresent());
    }

    /**
     * To cover all the branches in the remove function, we test these 6 cases:
     * 1) The node is null.
     * 2) The node is contained in imports.
     * 3) The node is contained in module.
     * 4) The node is contained in packageDeclaration.
     * 5) The node is contained in types.
     * 6) The node isn't contained in any variable mentioned above.
     */
    @Test
    void testRemove() throws IOException {
        Path sourceRoot = mavenModuleRoot(CompilationUnitTest.class).resolve(Paths.get("src", "test", "resources")).normalize();
        Path testFile = sourceRoot.resolve(Paths.get("com", "github", "javaparser", "storage", "PrimaryType2.java"));
        CompilationUnit cu = parse(testFile);

        assertFalse(cu.remove(null));

        // test branch of Imports
        NodeList<ImportDeclaration> imports = new NodeList<>();
        ImportDeclaration test1 = new ImportDeclaration("importTest1", true, true);
        ImportDeclaration test2 = new ImportDeclaration("importTest2", false, true);
        ImportDeclaration test3 = new ImportDeclaration("importTest3", true, false);
        ImportDeclaration test4 = new ImportDeclaration("importTest4", false, false);
        imports.add(test1);
        imports.add(test2);
        imports.add(test3);
        imports.add(test4);
        cu.setImports(imports);

        assertTrue(cu.remove(test3));

        // test branch of Module
        ModuleDeclaration module = new ModuleDeclaration();
        ModuleDeclaration module2 = new ModuleDeclaration();
        cu.setModule(module2);

        assertFalse(cu.remove(module));
        assertTrue(cu.remove(module2));

        // test branch of packageDeclaration
        PackageDeclaration packageDeclaration = new PackageDeclaration();
        PackageDeclaration packageDeclaration2 = new PackageDeclaration();
        cu.setPackageDeclaration(packageDeclaration);

        assertTrue(cu.remove(packageDeclaration));
        assertFalse(cu.remove(packageDeclaration2));

        // test branch of types
        NodeList<TypeDeclaration<?>> types = new NodeList<>();
        AnnotationDeclaration test5 = new AnnotationDeclaration();
        AnnotationDeclaration test6 = new AnnotationDeclaration();
        types.add(test5);
        types.add(test6);
        cu.setTypes(types);

        assertTrue(cu.remove(test6));

        // test the last branch
        ImportDeclaration test7 = new ImportDeclaration("finalTest", false, false);

        assertFalse(cu.remove(test7));
    }

}
