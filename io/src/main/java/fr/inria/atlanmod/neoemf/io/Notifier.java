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

package fr.inria.atlanmod.neoemf.io;

public interface Notifier {

    /**
     * Add an {@link Handler handler} that will be notified.
     *
     * @param handler the handler to add
     */
    void addHandler(Handler handler);

    /**
     * Defines if this notifier has at least one {@link Handler handler} to notify.
     *
     * @return {@code true} if this notifier has at least one handler to notify.
     */
    boolean hasHandler();
}
