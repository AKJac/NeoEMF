/*******************************************************************************
 * Copyright (c) 2013 Atlanmod INRIA LINA Mines Nantes
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.neoemf.hbase.datastore;

import java.io.File;
import java.util.Map;

import fr.inria.atlanmod.neoemf.datastore.AbstractPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.datastore.InvalidDataStoreException;
import fr.inria.atlanmod.neoemf.datastore.PersistenceBackend;
import fr.inria.atlanmod.neoemf.datastore.estores.SearcheableResourceEStore;
import fr.inria.atlanmod.neoemf.datastore.estores.impl.InvalidTransientResourceEStoreImpl;
import fr.inria.atlanmod.neoemf.datastore.estores.impl.IsSetCachingDelegatedEStoreImpl;
import fr.inria.atlanmod.neoemf.datastore.estores.impl.SizeCachingDelegatedEStoreImpl;
import fr.inria.atlanmod.neoemf.hbase.datastore.estores.impl.DirectWriteHbaseResourceEStoreImpl;
import fr.inria.atlanmod.neoemf.hbase.datastore.estores.impl.ReadOnlyHbaseResourceEStoreImpl;
import fr.inria.atlanmod.neoemf.hbase.resources.HBaseResourceOptions;
import fr.inria.atlanmod.neoemf.logger.NeoLogger;
import fr.inria.atlanmod.neoemf.resources.PersistentResource;

public class HbasePersistenceBackendFactory extends AbstractPersistenceBackendFactory {

    public static final String HBASE_BACKEND = "hbase";
    
    @Override
    public PersistenceBackend createTransientBackend() {
        return new HBasePersistenceBackend();
    }

    @Override
    public SearcheableResourceEStore createTransientEStore(PersistentResource resource,
            PersistenceBackend backend) {
        NeoLogger.log(NeoLogger.SEVERITY_WARNING, "NeoEMF/HBase does not provide a transient layer, you must save/load your resource before using it");
        return new InvalidTransientResourceEStoreImpl();
    }

    @Override
    public PersistenceBackend createPersistentBackend(File file, Map<?, ?> options)
            throws InvalidDataStoreException {
        // TODO Externalise the backend implementation from the HBase EStores
        return new HBasePersistenceBackend();
    }

    @Override
    protected SearcheableResourceEStore internalCreatePersistentEStore(PersistentResource resource,
            PersistenceBackend backend, Map<?, ?> options) throws InvalidDataStoreException {
        try {
            if(options.containsKey(HBaseResourceOptions.OPTIONS_HBASE_READ_ONLY)) {
                if(Boolean.TRUE.equals(options.get(HBaseResourceOptions.OPTIONS_HBASE_READ_ONLY))) {
                    // Create a read-only EStore
                    return new IsSetCachingDelegatedEStoreImpl(new SizeCachingDelegatedEStoreImpl(new ReadOnlyHbaseResourceEStoreImpl(resource)));
                }
                else {
                    // Create a default EStore
                    return new IsSetCachingDelegatedEStoreImpl(new SizeCachingDelegatedEStoreImpl(new DirectWriteHbaseResourceEStoreImpl(resource)));
                }
            } else {
                // Create a default EStore
                return new IsSetCachingDelegatedEStoreImpl(new SizeCachingDelegatedEStoreImpl(new DirectWriteHbaseResourceEStoreImpl(resource)));
            }
        } catch(Exception e) {
            throw new InvalidDataStoreException(e);
        }
    }

    @Override
    public void copyBackend(PersistenceBackend from, PersistenceBackend to) {
        // TODO Auto-generated method stub
        NeoLogger.log(NeoLogger.SEVERITY_WARNING, "NeoEMF/HBase does not support copy backend feature");;
    }

}