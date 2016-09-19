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

package fr.inria.atlanmod.neoemf.io.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;

import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.datastore.PersistenceBackend;
import fr.inria.atlanmod.neoemf.io.AlreadyExistingIdException;
import fr.inria.atlanmod.neoemf.io.PersistenceHandler;
import fr.inria.atlanmod.neoemf.io.beans.Attribute;
import fr.inria.atlanmod.neoemf.io.beans.Classifier;
import fr.inria.atlanmod.neoemf.io.beans.Identifier;
import fr.inria.atlanmod.neoemf.io.beans.MetaClassifier;
import fr.inria.atlanmod.neoemf.io.beans.Reference;
import fr.inria.atlanmod.neoemf.io.hash.HasherFactory;
import fr.inria.atlanmod.neoemf.logger.NeoLogger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An handler able to persist newly created data in a {@link PersistenceBackend persistence backend}.
 */
public abstract class AbstractPersistenceHandler<P extends PersistenceBackend> implements PersistenceHandler {

    private static final int OPS_BETWEEN_COMMITS_DEFAULT = adaptFromMemory(50000);
    protected static final int DEFAULT_CACHE_SIZE = adaptFromMemory(2000);

    private int opCount;

    private final P persistenceBackend;

    private final Deque<Id> elementIdStack;

    /**
     * Cache of recently processed {@code Id}.
     */
    private final Cache<String, Id> elementIdCache;

    /**
     * Cache of registered metaclasses.
     */
    private final Cache<String, Id> metaclassIdCache;

    /**
     * Cache of unlinked elements, waiting until their reference is created.
     * <p/>
     * In case of conflict detection only.
     */
    private final HashMultimap<String, UnlinkedElement> unlinkedElementsMap;

    /**
     * Cache of conflited {@code Id}.
     * <p/>
     * In case of conflict detection only.
     */
    private final Cache<String, Id> conflictElementIdCache;

    protected AbstractPersistenceHandler(P persistenceBackend) {
        this.persistenceBackend = persistenceBackend;
        this.opCount = 0;
        this.elementIdStack = new ArrayDeque<>();

        this.elementIdCache = CacheBuilder.newBuilder().maximumSize(DEFAULT_CACHE_SIZE).build();
        this.metaclassIdCache = CacheBuilder.newBuilder().build();

        this.unlinkedElementsMap = HashMultimap.create();
        this.conflictElementIdCache = CacheBuilder.newBuilder().build();

        NeoLogger.info("Autocommit Blueprints Handler created (chunk = {0})", OPS_BETWEEN_COMMITS_DEFAULT);
    }

    protected P getPersistenceBackend() {
        return persistenceBackend;
    }

    protected abstract Id getId(String reference);

    protected abstract void addElement(Id id, String nsUri, String name, boolean root) throws Exception;

    protected abstract void addAttribute(Id id, String name, int index, boolean many, Object value) throws Exception;

    protected abstract void addReference(Id id, String name, int index, boolean many, boolean containment, Id idReference) throws Exception;

    protected abstract void setMetaClass(Id id, Id metaClassId) throws Exception;

    @Override
    public void handleStartDocument() throws Exception {
        // Do nothing
    }

    @Override
    public void handleStartElement(final Classifier classifier) throws Exception {
        Id id = createElement(classifier);
        Id metaClassId = getOrCreateMetaClass(classifier.getMetaClassifier());

        setMetaClass(id, metaClassId);

        elementIdStack.addLast(id);
    }

    @Override
    public void handleAttribute(final Attribute attribute) throws Exception {
        Id id;
        if (attribute.getId() == null) {
            id = elementIdStack.getLast();
        } else {
            id = getOrCreateId(attribute.getId());
        }

        addAttribute(id,
                attribute.getLocalName(),
                attribute.getIndex(),
                attribute.isMany(),
                attribute.getValue());

        incrementAndCommit();
    }

    @Override
    public void handleReference(final Reference reference) throws Exception {
        Id id;
        if (reference.getId() == null) {
            id = elementIdStack.getLast();
        } else {
            id = getOrCreateId(reference.getId());
        }

        Id idReference = getOrCreateId(reference.getIdReference());

        try {
            addReference(id,
                    reference.getLocalName(),
                    reference.getIndex(),
                    reference.isMany(),
                    reference.isContainment(),
                    idReference);

            incrementAndCommit();
        } catch (NoSuchElementException e) {
            // Referenced element does not exist : we save it in a cache
            unlinkedElementsMap.put(
                    reference.getIdReference().getValue(),
                    new UnlinkedElement(id, reference.getLocalName(), reference.getIndex(), reference.isMany(), reference.isContainment()));
        }
    }

    @Override
    public void handleEndElement() throws Exception {
        elementIdStack.removeLast();
    }

    @Override
    public void handleEndDocument() throws Exception {
        long unlinkedNumber = unlinkedElementsMap.size();
        if(unlinkedNumber > 0) {
            NeoLogger.warn("Some elements have not been linked ({0})", unlinkedNumber);
            for (String e : unlinkedElementsMap.asMap().keySet()) {
                NeoLogger.warn(" > " + e);
            }
            unlinkedElementsMap.clear();
        }

        long conflictedId = conflictElementIdCache.size();
        if (conflictedId > 0) {
            NeoLogger.info("{0} key conflicts", conflictElementIdCache.size());
            conflictElementIdCache.invalidateAll();
        }

        persistenceBackend.save();
    }

    /**
     * Creates an element from the given {@code classifier} with the given {@code id}, and returns the given {@code id}.
     * <p/>
     * If {@code id} is {@code null}, it is calculated by the {@link #getId(String)} method.

     * @return the given {@code id}
     */
    protected Id createElement(final Classifier classifier, final Id id) throws Exception {
        checkNotNull(id);

        addElement(id,
                classifier.getNamespace().getUri(),
                classifier.getClassName(),
                classifier.isRoot());

        incrementAndCommit();

        tryLink(classifier.getId().getValue(), id);

        return id;
    }

    private Id createElement(final Classifier classifier) throws Exception {
        checkNotNull(classifier.getId());

        String idValue = classifier.getId().getValue();

        Id id = createId(classifier.getId());
        boolean conflict = false;

        do {
            try {
                createElement(classifier, id);
                elementIdCache.put(idValue, id);
            }
            catch (AlreadyExistingIdException e) {
                // Id already exists in the backend : try another
                id = createId(Identifier.generated(id.toString()));
                conflictElementIdCache.put(idValue, id);
                conflict = true;
            }
        } while (conflict);

        return id;
    }

    /**
     * Creates a metaclass form the given {@code metaClassifier} and returns its {@link Id}.

     * @return the {@link Id} of the newly created metaclass
     */
    protected Id getOrCreateMetaClass(final MetaClassifier metaClassifier) throws Exception {
        String idValue = metaClassifier.getNamespace().getUri() + ':' + metaClassifier.getLocalName();

        // Gets from cache
        Id id = metaclassIdCache.getIfPresent(idValue);

        // If metaclass doesn't already exist, we create it
        if (id == null) {
            id = createId(Identifier.generated(idValue));
            boolean conflict = false;

            do {
                try {
                    addElement(id,
                            metaClassifier.getNamespace().getUri(),
                            metaClassifier.getLocalName(),
                            false);

                    metaclassIdCache.put(idValue, id);
                }
                catch (AlreadyExistingIdException e) {
                    id = createId(Identifier.generated(id.toString()));
                    conflict = true;
                }
            }
            while (conflict);
        }

        incrementAndCommit();

        return id;
    }

    /**
     * Get the {@link Id id} of the given identifier.
     *
     * @param identifier the identifier
     *
     * @return the registered {@code Id} of the given identifier, or {@code null} if the identifier is not registered.
     */
    private Id getOrCreateId(final Identifier identifier) {
        Id id = conflictElementIdCache.getIfPresent(identifier.getValue());
        if (id == null) {
            id = elementIdCache.getIfPresent(identifier.getValue());
            if (id == null) {
                id = createId(identifier);
                elementIdCache.put(identifier.getValue(), id);
            }
        }
        return id;
    }

    private Id createId(final Identifier identifier) {
        String idValue = identifier.getValue();

        // If identifier has been generated we hash it, otherwise we use the original
        if (identifier.isGenerated()) {
            idValue = HasherFactory.md5().hash(idValue).toString();
        }

        return getId(idValue);
    }

    /**
     * Tries to link elements that have not been linked at their creation.
     *
     * @param reference the reference of the targetted element
     * @param id the identifier of the targetted element
     */
    private void tryLink(final String reference, final Id id) throws Exception {
        for (UnlinkedElement e : unlinkedElementsMap.removeAll(reference)) {
            addReference(e.id, e.name, e.index, e.many, e.containment, id);
        }
    }

    /**
     * Increments the operation counter, and commit the persistence backend if the number of operation is equals to
     * {@code OPS_BETWEEN_COMMITS_DEFAULT}.
     */
    private void incrementAndCommit() {
        opCount = (opCount + 1) % OPS_BETWEEN_COMMITS_DEFAULT;
        if (opCount == 0) {
            persistenceBackend.save();
        }
    }

    /**
     * Adapts the given {@code value} according to the max memory.
     * <p/>
     * The formulas can be improved, for sure.
     */
    private static int adaptFromMemory(int value) {
        double maxMemoryGB = Math.round(Runtime.getRuntime().maxMemory() / 1000 / 1000 / 1000);

        double factor = maxMemoryGB;
        if (maxMemoryGB > 1) {
            factor *= 2;
        }

        return (int) (value * factor);
    }

    /**
     * An element that could not be linked at its creation.
     */
    private class UnlinkedElement {

        public final Id id;
        public final String name;
        public final int index;
        public final boolean many;
        public final boolean containment;

        public UnlinkedElement(Id id, String name, int index, boolean many, boolean containment) {
            this.id = id;
            this.name = name;
            this.index = index;
            this.many = many;
            this.containment = containment;
        }
    }
}