/*
 * Copyright (c) 2013-2018 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.data.hbase.context;

import fr.inria.atlanmod.neoemf.config.Config;
import fr.inria.atlanmod.neoemf.context.AbstractContext;
import fr.inria.atlanmod.neoemf.context.Context;
import fr.inria.atlanmod.neoemf.data.BackendFactory;
import fr.inria.atlanmod.neoemf.data.hbase.HBaseBackendFactory;
import fr.inria.atlanmod.neoemf.data.hbase.config.HBaseConfig;
import fr.inria.atlanmod.neoemf.util.UriBuilder;

import org.eclipse.emf.common.util.URI;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A specific {@link Context} for the HBase implementation.
 */
@ParametersAreNonnullByDefault
public abstract class HBaseContext extends AbstractContext {

    /**
     * Creates a new {@code HBaseContext} with a mapping with arrays and strings.
     *
     * @return a new context.
     */
    @Nonnull
    public static Context getDefault() {
        return new HBaseContext() {
            @Nonnull
            @Override
            public Config config() {
                return HBaseConfig.newConfig();
            }
        };
    }

    @Override
    public boolean isInitialized() {
        return HBaseCluster.isInitialized();
    }

    @Override
    public Context init() {
        HBaseCluster.init();
        return this;
    }

    @Nonnull
    @Override
    public String name() {
        return "HBase";
    }

    @Nonnull
    @Override
    public BackendFactory factory() {
        return HBaseBackendFactory.getInstance();
    }

    @Nonnull
    @Override
    public URI createUri(URI uri) {
        return UriBuilder.forScheme(uriScheme()).fromServer(HBaseCluster.host(), HBaseCluster.port(), uri);
    }

    @Nonnull
    @Override
    public URI createUri(File file) {
        return UriBuilder.forScheme(uriScheme()).fromServer(HBaseCluster.host(), HBaseCluster.port(), file.getName());
    }
}
