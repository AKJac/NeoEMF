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

package fr.inria.atlanmod.neoemf.data.mapdb.util;

import fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.mapdb.MapDbBackendFactory;
import fr.inria.atlanmod.neoemf.util.URIBuilder;

import org.eclipse.emf.common.util.URI;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link URIBuilder} that creates MapDB specific resource {@link URI}s.
 *
 * @see BackendFactoryRegistry
 * @see fr.inria.atlanmod.neoemf.data.mapdb.MapDbBackendFactory
 * @see fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory
 */
@ParametersAreNonnullByDefault
public class MapDbURI extends URIBuilder {

    /**
     * The scheme associated to the {@link URI}.
     */
    @Nonnull
    public static final String SCHEME = formatScheme(MapDbBackendFactory.getInstance());

    /**
     * Constructs a new {@code MapDbURI}.
     */
    private MapDbURI() {
        super();
    }

    /**
     * Creates a new {@code MapDbURI} with the pre-configured scheme.
     *
     * @return a new builder
     */
    @Nonnull
    public static URIBuilder newBuilder() {
        return new MapDbURI().withScheme(SCHEME);
    }

    @Nonnull
    @Override
    public URI fromServer(String host, int port, URI model) {
        throw new UnsupportedOperationException("MapDbURI does not support server-based URIs");
    }
}
