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

package fr.inria.atlanmod.neoemf.option;

import fr.inria.atlanmod.neoemf.annotations.Experimental;
import fr.inria.atlanmod.neoemf.data.BackendFactory;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents options managed by {@link BackendFactory}.
 * <p>
 * <b>Note:</b> Not implemented yet.
 * <p>
 * <b>Future:</b> This class is not used in the current release of the tool, it will simplify option management in the
 * near future.
 *
 * @see PersistenceOptionsBuilder
 */
@Experimental
@ParametersAreNonnullByDefault
public interface PersistenceOptions {

    /**
     * Converts this {@code PersistenceOptions} as a {@link Map} that contains all defined options.
     *
     * @return an immutable {@link Map}
     *
     * @throws InvalidOptionException if a conflict is detected
     */
    @Nonnull
    Map<String, Object> toMap();

    /**
     * Fills this {@code PersistenceOptions} with all options contained in the given {@link Map}.
     *
     * @param options the options to parse
     */
    void fromMap(Map<String, Object> options);
}
