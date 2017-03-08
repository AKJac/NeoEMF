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

package fr.inria.atlanmod.neoemf.data;

import fr.inria.atlanmod.neoemf.data.store.PersistentStore;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;

import org.eclipse.emf.common.util.URI;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A factory of {@link PersistenceBackend} and {@link PersistentStore}.
 * <p>
 * The creation can be configured using {@link PersistentResource#save(Map)} and {@link PersistentResource#load(Map)}
 * option maps.
 *
 * @see fr.inria.atlanmod.neoemf.option.PersistenceOptionsBuilder
 */
@ParametersAreNonnullByDefault
public interface PersistenceBackendFactory {

    /**
     * The name of the configuration file of a back-end persistence.
     */
    String CONFIG_FILE = "neoconfig.properties";

    /**
     * The "back-end" property in the configuration file.
     */
    String BACKEND_PROPERTY = "backend";

    /**
     * Creates an in-memory {@link PersistenceBackend}.
     *
     * @return the persistence back-end
     *
     * @throws InvalidDataStoreException if there is at least one invalid value in {@code options}, or if an option is
     *                                   missing
     */
    @Nonnull
    PersistenceBackend createTransientBackend();

    /**
     * Creates a {@link PersistenceBackend} in the given {@code directory}.
     *
     * @param uri     the directory
     * @param options the options that defines the behaviour of the back-end
     *
     * @return the persistence back-end
     *
     * @throws InvalidDataStoreException if there is at least one invalid value in {@code options}, or if an option is
     *                                   missing
     */
    @Nonnull
    PersistenceBackend createPersistentBackend(URI uri, Map<String, Object> options);

    /**
     * Creates a {@link PersistentStore} between the given {@code resource} and the given in-memory {@code backend}.
     *
     * @param resource the resource
     * @param backend  the back-end
     *
     * @return the newly created persistent store.
     */
    @Nonnull
    PersistentStore createTransientStore(PersistentResource resource, PersistenceBackend backend);

    /**
     * Creates a {@link PersistentStore} between the given {@code resource} and the default in-memory {@code backend}.
     *
     * @param resource the resource
     *
     * @return the newly created persistent store.
     *
     * @see #createTransientStore(PersistentResource, PersistenceBackend)
     * @see #createTransientBackend()
     */
    @Nonnull
    default PersistentStore createTransientStore(PersistentResource resource) {
        return createTransientStore(resource, createTransientBackend());
    }

    /**
     * Creates a {@link PersistentStore} between the given {@code resource} and the given {@code backend}
     * according to the given {@code options}.
     * <p>
     * The returned {@link PersistentStore} may be a succession of several {@link PersistentStore}.
     *
     * @param resource the resource
     * @param backend  the back-end
     * @param options  the options that defines the behaviour of the back-end
     *
     * @return the newly created persistent store.
     *
     * @throws InvalidDataStoreException if there is at least one invalid value in {@code options}, or if an option is
     *                                   missing
     * @throws IllegalArgumentException  if the given {@code backend} is not an instance of the targeted {@link
     *                                   PersistenceBackend} for this factory
     */
    @Nonnull
    PersistentStore createPersistentStore(PersistentResource resource, PersistenceBackend backend, Map<String, Object> options);

    /**
     * Creates a {@link PersistentStore} between the given {@code resource} and the default persistent {@code backend}
     * according to the given {@code options}.
     * <p>
     * The returned {@link PersistentStore} may be a succession of several {@link PersistentStore}.
     *
     * @param resource the resource
     * @param options  the options that defines the behaviour of the back-end
     *
     * @return the newly created persistent store.
     *
     * @throws InvalidDataStoreException if there is at least one invalid value in {@code options}, or if an option is
     *                                   missing
     * @throws IllegalArgumentException  if the given {@code backend} is not an instance of the targeted {@link
     *                                   PersistenceBackend} for this factory
     * @see #createPersistentStore(PersistentResource, PersistenceBackend, Map)
     * @see #createPersistentBackend(URI, Map)
     */
    default PersistentStore createPersistentStore(PersistentResource resource, Map<String, Object> options) {
        return createPersistentStore(resource, createPersistentBackend(resource.getURI(), options), options);
    }
}
