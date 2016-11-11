/*
 * Copyright (c) 2013-2016 Atlanmod INRIA LINA Mines Nantes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 */

package fr.inria.atlanmod.neoemf.datastore;

import org.eclipse.emf.ecore.EClass;

import java.util.Map;

public interface PersistenceBackend {

    /**
     * Starts the underlying data store with the given {@code options}.
     */
    // TODO: All implementations are empty.
    void start(Map<?, ?> options);

    /**
     * Returns whether the underlying data store has been started or not.
     */
    boolean isStarted();

    /**
     * Cleanly stops the underlying data store.
     */
    void stop();

    /**
     * Saves the modifications of the owned {@link org.eclipse.emf.ecore.EObject}s in the persistence
     * back-end.
     */
    void save();

    /**
     * Back-end specific computation of {@link org.eclipse.emf.ecore.resource.Resource#getAllContents()}.
     *
     * @param eClass the class to compute the instances of
     * @param strict {@code true} if the lookup searches for strict instances
     *
     * @return an {@link Object} containing the back-end specific objects corresponding to the instances of the {@link
     *         EClass}
     *
     * @throws UnsupportedOperationException if the back-end does not support allInstances lookup
     */
    Object getAllInstances(EClass eClass, boolean strict) throws UnsupportedOperationException;
}
