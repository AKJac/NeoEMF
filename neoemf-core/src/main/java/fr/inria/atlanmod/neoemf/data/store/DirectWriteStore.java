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

package fr.inria.atlanmod.neoemf.data.store;

import fr.inria.atlanmod.neoemf.data.Backend;
import fr.inria.atlanmod.neoemf.data.mapper.AbstractMapperDecorator;
import fr.inria.atlanmod.neoemf.util.log.Log;

import org.eclipse.emf.ecore.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.nonNull;

/**
 * A {@link Store} that translates model-level operations into datastore calls.
 */
@ParametersAreNonnullByDefault
public final class DirectWriteStore extends AbstractMapperDecorator<Backend> implements Store {

    /**
     * The resource to store and access.
     */
    @Nullable
    private final Resource.Internal resource;

    /**
     * The thread used to close the back-end when the application will exit.
     */
    @Nullable
    private final Thread shutdownHook;

    /**
     * Constructs a new {@code DirectWriteStore} between the given {@code resource} and the {@code backend}.
     *
     * @param backend  the back-end used to store the model
     * @param resource the resource to store and access
     */
    public DirectWriteStore(Backend backend, @Nullable Resource.Internal resource) {
        super(backend);
        this.resource = resource;

        this.shutdownHook = new Thread(() -> {
            backend.close();

            if (nonNull(resource)) {
                Log.debug("{0} closed: {1}", backend.getClass().getSimpleName(), resource.getURI());
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    @Override
    public void close() {
        if (nonNull(shutdownHook)) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }

        super.close();
    }

    @Nullable
    @Override
    public Resource.Internal resource() {
        return resource;
    }

    @Nonnull
    @Override
    public Backend backend() {
        return next();
    }
}