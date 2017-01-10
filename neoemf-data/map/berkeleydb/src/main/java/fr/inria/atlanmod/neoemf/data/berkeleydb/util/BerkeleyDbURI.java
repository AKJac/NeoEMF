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

package fr.inria.atlanmod.neoemf.data.berkeleydb.util;

import fr.inria.atlanmod.neoemf.util.PersistenceURI;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;

import java.io.File;
import java.text.MessageFormat;
import java.util.Objects;

public class BerkeleyDbURI extends PersistenceURI {

    public static final String SCHEME = "neo-berkeleydb";

    protected BerkeleyDbURI(int hashCode, URI internalURI) {
        super(hashCode, internalURI);
    }

    public static URI createURI(URI uri) {
        if (Objects.equals(PersistenceURI.FILE_SCHEME, uri.scheme())) {
            return createFileURI(FileUtils.getFile(uri.toFileString()));
        } else if (Objects.equals(SCHEME, uri.scheme())) {
            return PersistenceURI.createURI(uri);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Can not create BerkeleyDbURI from the URI scheme {0}", uri.scheme()));
        }
    }

    public static URI createFileURI(File file) {
        return PersistenceURI.createFileURI(file, SCHEME);
    }
}
