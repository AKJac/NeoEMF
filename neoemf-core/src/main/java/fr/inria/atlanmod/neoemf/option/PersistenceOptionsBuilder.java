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

public class PersistenceOptionsBuilder extends AbstractPersistenceOptionsBuilder<PersistenceOptionsBuilder, PersistenceOptions> {

    protected PersistenceOptionsBuilder() {
    }

    public static PersistenceOptionsBuilder newBuilder() {
        return new PersistenceOptionsBuilder();
    }
}
