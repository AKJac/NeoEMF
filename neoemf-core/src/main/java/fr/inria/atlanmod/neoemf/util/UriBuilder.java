/*
 * Copyright (c) 2013-2017 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.util;

import fr.inria.atlanmod.neoemf.bind.Bindings;

import org.eclipse.emf.common.util.URI;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A builder of {@link URI}s. Created {@link URI}s are used to locate a {@link fr.inria.atlanmod.neoemf.resource.PersistentResource}
 * and select the {@link fr.inria.atlanmod.neoemf.data.BackendFactory} to use for persistence.
 */
@ParametersAreNonnullByDefault
public interface UriBuilder {

    /**
     * Creates a {@link URI} scheme from the specified {@code baseName}.
     *
     * @param baseName the base name of the scheme
     *
     * @return the {@link URI} scheme
     */
    @Nonnull
    static String createScheme(String baseName) {
        return String.format("neo-%s", baseName.toLowerCase());
    }

    /**
     * Retrieves the instance of {@code UriBuilder} that is associated to a {@link fr.inria.atlanmod.neoemf.data.BackendFactory}
     * wearing the given {@code name}.
     *
     * @param name the name of the factory
     *
     * @return a new instance of {@code UriBuilder}
     */
    @Nonnull
    static UriBuilder forName(String name) {
        return Bindings.findBy(UriBuilder.class, name, Bindings::nameOf);
    }

    /**
     * Retrieves the instance of {@code UriBuilder} that uses the given {@code scheme}.
     *
     * @param scheme the scheme of the builder
     *
     * @return a new instance of {@code UriBuilder}
     */
    @Nonnull
    static UriBuilder forScheme(String scheme) {
        return Bindings.findBy(UriBuilder.class, scheme, Bindings::schemeOf);
    }

    /**
     * Creates a new {@code URI} from the given {@code uri}.
     * <p>
     * This method checks that the scheme of the provided {@code uri} can be used to create a new {@link UriBuilder}.
     * Its scheme must be registered in the {@link fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry}.
     *
     * @param uri the base file-based {@link URI}
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if the {@code uri} is {@code null}
     * @throws IllegalArgumentException      if the scheme of the provided {@code uri} is not registered in the {@link
     *                                       fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry}
     */
    @Nonnull
    URI fromUri(URI uri);

    /**
     * Creates a new {@code URI} from the given {@code file} descriptor.
     *
     * @param filePath the path of the {@link File} to build a {@link URI} from
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if the {@code filePath} is {@code null}
     */
    @Nonnull
    URI fromFile(String filePath);

    /**
     * Creates a new {@code URI} from the given {@code file} descriptor.
     *
     * @param file the {@link File} to build a {@link URI} from
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if the {@code file} is {@code null}
     */
    @Nonnull
    URI fromFile(File file);

    /**
     * Creates a new {@code URI} from the {@code host}, {@code port}, and {@code model} by creating a hierarchical
     * {@link URI} that references the distant model resource.
     *
     * @param host  the address of the server (use {@code "localhost"} if the server is running locally)
     * @param port  the port of the server
     * @param model a {@link URI} identifying the model in the database
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if any of the parameters is {@code null}
     * @throws IllegalArgumentException      if {@code port < 0}
     */
    @Nonnull
    URI fromServer(String host, int port, URI model);

    /**
     * Creates a new {@code URI} from the {@code host}, {@code port}, and {@code model} by creating a hierarchical
     * {@link URI} that references the distant model resource.
     *
     * @param host     the address of the server (use {@code "localhost"} if the server is running locally)
     * @param port     the port of the server
     * @param segments a string identifying the model in the database
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if any of the parameters is {@code null}
     * @throws IllegalArgumentException      if {@code port < 0}
     */
    @Nonnull
    URI fromServer(String host, int port, String... segments);
}
