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

package fr.inria.atlanmod.neoemf.data.berkeleydb;

import fr.inria.atlanmod.neoemf.data.AbstractBackendFactoryTest;
import fr.inria.atlanmod.neoemf.data.Backend;
import fr.inria.atlanmod.neoemf.data.berkeleydb.context.BerkeleyDbTest;
import fr.inria.atlanmod.neoemf.data.berkeleydb.option.BerkeleyDbOptions;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BerkeleyDbBackendFactoryTest extends AbstractBackendFactoryTest implements BerkeleyDbTest {

    @Test
    public void testCreateTransientBackend() {
        Backend backend = context().factory().createTransientBackend();
        assertThat(backend).isInstanceOf(BerkeleyDbBackend.class);
    }

    @Test
    public void testCreateDefaultPersistentBackend() {
        Backend backend = context().factory().createPersistentBackend(context().createUri(file()), BerkeleyDbOptions.noOption());
        assertThat(backend).isInstanceOf(BerkeleyDbBackendIndices.class);
    }

    @Test
    public void testCreateIndicesPersistentBackend() {
        Map<String, Object> options = BerkeleyDbOptions.newBuilder()
                .withIndices()
                .asMap();

        Backend backend = context().factory().createPersistentBackend(context().createUri(file()), options);
        assertThat(backend).isInstanceOf(BerkeleyDbBackendIndices.class);
    }

    @Test
    public void testCreateArraysPersistentBackend() {
        Map<String, Object> options = BerkeleyDbOptions.newBuilder()
                .withArrays()
                .asMap();

        Backend backend = context().factory().createPersistentBackend(context().createUri(file()), options);
        assertThat(backend).isInstanceOf(BerkeleyDbBackendArrays.class);
    }

    @Test
    public void testCreateListsPersistentBackend() {
        Map<String, Object> options = BerkeleyDbOptions.newBuilder()
                .withLists()
                .asMap();

        Backend backend = context().factory().createPersistentBackend(context().createUri(file()), options);
        assertThat(backend).isInstanceOf(BerkeleyDbBackendLists.class);
    }

    @Test
    public void testCopyBackend() {
        Backend transientBackend = context().factory().createTransientBackend();
        assertThat(transientBackend).isInstanceOf(BerkeleyDbBackend.class);

        Backend persistentBackend = context().factory().createPersistentBackend(context().createUri(file()), BerkeleyDbOptions.noOption());
        assertThat(persistentBackend).isInstanceOf(BerkeleyDbBackend.class);

        transientBackend.copyTo(persistentBackend);
    }
}