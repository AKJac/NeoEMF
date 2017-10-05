/*
 * Copyright (c) 2013-2017 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.data.bean.serializer;

import fr.inria.atlanmod.commons.io.serializer.AbstractSerializer;
import fr.inria.atlanmod.commons.io.serializer.Serializer;
import fr.inria.atlanmod.neoemf.core.Id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link Serializer} for {@link Id}s.
 */
@ParametersAreNonnullByDefault
final class IdSerializer extends AbstractSerializer<Id> {

    @SuppressWarnings("JavaDoc")
    private static final long serialVersionUID = 4870583637926304450L;

    @Override
    public void serialize(Id id, DataOutput out) throws IOException {
        out.writeLong(id.toLong());
    }

    @Nonnull
    @Override
    public Id deserialize(DataInput in) throws IOException {
        return Id.getProvider().fromLong(in.readLong());
    }
}
