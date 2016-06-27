/*
 * Copyright (c) 2013 Atlanmod INRIA LINA Mines Nantes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 */

package fr.inria.atlanmod.neoemf.io.beans;

/**
 *
 */
public class Reference extends StructuralFeature {

    private boolean containment;

    @Override
    public boolean isReference() {
        return true;
    }

    public boolean isContainment() {
        return containment;
    }

    public void setContainment(boolean containment) {
        this.containment = containment;
    }
}
