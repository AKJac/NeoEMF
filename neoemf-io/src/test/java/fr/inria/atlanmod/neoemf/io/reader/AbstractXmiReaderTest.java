/*
 * Copyright (c) 2013-2017 Atlanmod INRIA LINA Mines Nantes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 */

package fr.inria.atlanmod.neoemf.io.reader;

import fr.inria.atlanmod.neoemf.io.AbstractInputTest;
import fr.inria.atlanmod.neoemf.io.mock.StructuralPersistanceHandler;
import fr.inria.atlanmod.neoemf.io.mock.beans.ClassifierMock;
import fr.inria.atlanmod.neoemf.io.persistence.PersistenceNotifier;
import fr.inria.atlanmod.neoemf.io.processor.Processor;
import fr.inria.atlanmod.neoemf.io.structure.Attribute;
import fr.inria.atlanmod.neoemf.io.structure.MetaClassifier;
import fr.inria.atlanmod.neoemf.io.structure.Namespace;
import fr.inria.atlanmod.neoemf.io.structure.Reference;

import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractXmiReaderTest extends AbstractInputTest {

    @Before
    public void readResource() throws IOException {
        persistanceHandler = read(sample);
    }

    @After
    public final void unregisterNamespaces() {
        Namespace.Registry.getInstance().clean();
    }

    protected void assertValidElement(final ClassifierMock mock, final String name, final int size, final String id) {
        assertThat(mock.getLocalName()).isEqualTo(name);
        assertThat(mock.getElements()).hasSize(size);

        if (isNull(id)) {
            assertThat(mock.getId()).isNull();
        }
        else {
            assertThat(mock.getId().getValue()).isEqualTo(id);
        }
    }

    protected void assertValidMetaClass(final MetaClassifier metaClassifier, final String name, final Namespace ns) {
        assertThat(metaClassifier.getLocalName()).isEqualTo(name);
        assertThat(metaClassifier.getNamespace()).isEqualTo(ns);
    }

    protected void assertValidReference(final Reference reference, final String name, final int index, final String idReference) {
        assertThat(reference.getLocalName()).isEqualTo(name);
        assertThat(reference.getIndex()).isEqualTo(index);
        assertThat(reference.getIdReference().getValue()).isEqualTo(idReference);
    }

    protected void assertValidAttribute(final Attribute attribute, final String name, final Object value) {
        assertThat(attribute.getLocalName()).isEqualTo(name);
        assertThat(attribute.getValue()).isEqualTo(value);
    }

    protected StructuralPersistanceHandler read(File filePath) throws IOException {
        StructuralPersistanceHandler persistanceHandler = new StructuralPersistanceHandler();

        XmiStreamReader reader = new XmiStreamReader();

        Processor processor = new PersistenceNotifier();
        processor.addHandler(persistanceHandler);

        reader.addHandler(processor);
        reader.read(new FileInputStream(filePath));

        return persistanceHandler;
    }
}