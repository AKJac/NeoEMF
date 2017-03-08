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

import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.data.PersistenceBackend;
import fr.inria.atlanmod.neoemf.data.mapper.PersistenceMapper;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link PersistenceMapper} to establish a mapping between {@link PersistentResource}s and {@link
 * PersistenceBackend}s.
 */
@ParametersAreNonnullByDefault
// TODO Remove inheritance from InternalEObject.EStore
public interface PersistentStore extends InternalEObject.EStore, PersistenceMapper {

    /**
     * Returns the {@link Resource} to persist and access.
     *
     * @return the resource to persist and access
     */
    @Nullable
    PersistentResource resource();

    /**
     * Returns the {@link PersistenceBackend} where data are persisted.
     *
     * @return the backend where data are persisted
     */
    @Nonnull
    PersistenceBackend backend();

    /**
     * Returns the resolved {@link PersistentEObject} identified by the given {@code id}.
     *
     * @param id the identifier of the {@link PersistentEObject} to resolve
     *
     * @return the resolved {@link PersistentEObject}
     *
     * @throws java.util.NoSuchElementException if no object can be resolved with the {@code id}
     */
    @Nonnull
    PersistentEObject resolve(Id id);

    @Override
    default EObject create(EClass eClass) {
        throw new IllegalStateException("This method should not be called");
    }
}
