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

package fr.inria.atlanmod.neoemf.benchmarks;

import com.google.common.collect.Iterators;

import fr.inria.atlanmod.neoemf.benchmarks.queries.QueryFactory;
import fr.inria.atlanmod.neoemf.datastore.PersistenceBackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.graph.blueprints.datastore.BlueprintsPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.graph.blueprints.util.NeoBlueprintsURI;
import fr.inria.atlanmod.neoemf.resources.PersistentResourceFactory;
import fr.inria.atlanmod.neoemf.resources.impl.PersistentResourceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// in = ${java.io.tmpdir}/neoemf-benchmarks/temp/*.neoemfgraphresource

public class NeoEMFGraphQuery {

    private static final Logger LOG = LogManager.getLogger();

    public void queryClassDeclarationAttributes(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            QueryFactory.getClassDeclarationAttributes(resource).callWithTimeSpent();

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void queryGrabats(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            QueryFactory.grabats09(resource).callWithTimeSpent();

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void queryInvisibleMethodDeclarations(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            QueryFactory.getInvisibleMethodDeclarations(resource).callWithTimeSpent();

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void queryOrphanNonPrimitiveTypes(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            QueryFactory.getOrphanNonPrimitivesTypes(resource).callWithTimeSpent();

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void queryRenameAllMethods(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);
            String name = UUID.randomUUID().toString();

            QueryFactory.renameAllMethods(resource, name).callWithTimeSpent();
            resource.save(Collections.emptyMap());

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void queryThrownExceptionsPerPackage(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            QueryFactory.getThrownExceptionsPerPackage(resource).callWithTimeSpent();

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void queryUnusedMethodsList(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            QueryFactory.getUnusedMethodsList(resource).callWithTimeSpent();

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void queryUnusedMethodsLoop(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            QueryFactory.getUnusedMethodsLoop(resource).callWithTimeSpent(); // Query result (loops)

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    public void traverse(String in) {
        try {
            PersistenceBackendFactoryRegistry.register(NeoBlueprintsURI.NEO_GRAPH_SCHEME, BlueprintsPersistenceBackendFactory.getInstance());

            URI uri = NeoBlueprintsURI.createNeoGraphURI(new File(in));

            org.eclipse.gmt.modisco.java.neoemf.impl.JavaPackageImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(NeoBlueprintsURI.NEO_GRAPH_SCHEME, PersistentResourceFactory.getInstance());

            Resource resource = resourceSet.createResource(uri);

            Map<String, Object> loadOpts = new HashMap<>();

            resource.load(loadOpts);

            LOG.info("Start counting");
            Instant begin = Instant.now();
            int count = Iterators.size(resource.getAllContents());
            Instant end = Instant.now();
            LOG.info("End counting");
            LOG.info("Resource {0} contains {1} elements", uri, count);
            LOG.info("Time spent: {0}", Duration.between(begin, end));

            if (resource instanceof PersistentResourceImpl) {
                PersistentResourceImpl.shutdownWithoutUnload((PersistentResourceImpl) resource);
            }
            else {
                resource.unload();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }
}
