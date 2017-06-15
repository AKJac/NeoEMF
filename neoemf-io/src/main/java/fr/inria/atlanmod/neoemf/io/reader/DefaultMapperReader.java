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

package fr.inria.atlanmod.neoemf.io.reader;

import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.data.mapper.DataMapper;
import fr.inria.atlanmod.neoemf.data.structure.ClassDescriptor;
import fr.inria.atlanmod.neoemf.data.structure.ContainerDescriptor;
import fr.inria.atlanmod.neoemf.data.structure.SingleFeatureKey;
import fr.inria.atlanmod.neoemf.io.Handler;
import fr.inria.atlanmod.neoemf.io.structure.BasicAttribute;
import fr.inria.atlanmod.neoemf.io.structure.BasicElement;
import fr.inria.atlanmod.neoemf.io.structure.BasicId;
import fr.inria.atlanmod.neoemf.io.structure.BasicMetaclass;
import fr.inria.atlanmod.neoemf.io.structure.BasicNamespace;
import fr.inria.atlanmod.neoemf.io.structure.BasicReference;
import fr.inria.atlanmod.neoemf.io.util.MapperConstants;

import org.eclipse.emf.ecore.EClass;

import java.util.Objects;
import java.util.stream.IntStream;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The default implementation of a {@link MapperReader}.
 */
@ParametersAreNonnullByDefault
public class DefaultMapperReader extends AbstractReader<DataMapper> implements MapperReader {

    /**
     * The mapper to read.
     */
    private DataMapper mapper;

    /**
     * Constructs a new {@code DefaultMapperReader} with the given {@code handler}.
     *
     * @param handler the handler to notify
     */
    public DefaultMapperReader(Handler handler) {
        super(handler);
    }

    @Override
    public void read(DataMapper source) {
        mapper = source;

        notifyInitialize();

        SingleFeatureKey rootKey = SingleFeatureKey.of(MapperConstants.ROOT_ID, MapperConstants.ROOT_FEATURE_NAME);
        source.allReferencesOf(rootKey).forEach(id -> readElement(id, true));

        notifyComplete();

        mapper = null;
    }

    /**
     * Reads the element identified by its {@code id}.
     *
     * @param id the identifier of the element
     */
    protected void readElement(Id id) {
        readElement(id, false);
    }

    /**
     * Reads the element identified by its {@code id}.
     *
     * @param id     the identifier of the element
     * @param isRoot {@code true} if the element is a root element
     */
    protected void readElement(Id id, boolean isRoot) {
        // Retrieve the metaclass and namespace
        ClassDescriptor metaclass = mapper.metaclassOf(id).<IllegalArgumentException>orElseThrow(IllegalArgumentException::new);
        EClass realMetaclass = metaclass.get();

        BasicNamespace ns = BasicNamespace.Registry.getInstance().register(realMetaclass.getEPackage().getNsPrefix(), realMetaclass.getEPackage().getNsURI());

        // Retrieve the name of the element
        // If root it's the name of the metaclass, otherwise the name of the containing feature
        String elementName = isRoot ? metaclass.name() : mapper.containerOf(id).map(ContainerDescriptor::name).orElse(null);

        // Create the element
        BasicElement element = new BasicElement(ns, elementName);
        element.id(BasicId.original(id.toString()));
        element.metaclass(new BasicMetaclass(ns, metaclass.name()));
        element.isRoot(isRoot);

        // Retrieve the real name of this element
        String name = mapper.valueOf(SingleFeatureKey.of(id, MapperConstants.FEATURE_NAME)).map(Object::toString).orElse(null);
        element.className(name);

        notifyStartElement(element);

        // Process all attributes
        realMetaclass.getEAllAttributes().stream()
                .filter(attribute -> !Objects.equals(MapperConstants.FEATURE_NAME, attribute.getName())) // "name" has a special treatment
                .forEach(attribute -> {
                    SingleFeatureKey key = SingleFeatureKey.of(id, attribute.getName());
                    if (!attribute.isMany()) {
                        readValue(key);
                    }
                    else {
                        readAllValues(key);
                    }
                });

        // Process all references
        realMetaclass.getEAllReferences()
                .forEach(reference -> {
                    SingleFeatureKey key = SingleFeatureKey.of(id, reference.getName());
                    if (!reference.isMany()) {
                        readReference(key, reference.isContainment());
                    }
                    else {
                        readAllReferences(key, reference.isContainment());
                    }
                });

        notifyEndElement();
    }

    /**
     * Reads the single-valued attribute of the specified {@code key}.
     *
     * @param key the key identifying the attribute
     */
    protected void readValue(SingleFeatureKey key) {
        mapper.<String>valueOf(key).ifPresent(value -> {
            BasicAttribute attribute = new BasicAttribute(key.name());

            attribute.id(BasicId.original(key.id().toString()));

            attribute.value(value);

            notifyAttribute(attribute);
        });
    }

    /**
     * Reads the multi-valued attribute of the specified {@code key}.
     *
     * @param key the key identifying the attributes
     */
    protected void readAllValues(SingleFeatureKey key) {
        int size = mapper.sizeOfValue(key).orElse(0);

        IntStream.range(0, size).forEach(position ->
                mapper.<String>valueOf(key.withPosition(0)).ifPresent(value -> {
                    BasicAttribute attribute = new BasicAttribute(key.name());

                    attribute.id(BasicId.original(key.id().toString()));
                    attribute.value(value);

                    attribute.index(position);

                    attribute.isMany(true);

                    notifyAttribute(attribute);
                }));
    }

    /**
     * Reads the single-valued reference of the specified {@code key}.
     *
     * @param key           the key identifying the reference
     * @param isContainment {@code true} if the reference is a containment
     */
    protected void readReference(SingleFeatureKey key, boolean isContainment) {
        mapper.referenceOf(key).ifPresent(id -> {
            BasicReference reference = new BasicReference(key.name());

            reference.id(BasicId.original(key.id().toString()));
            reference.idReference(BasicId.original(id.toString()));

            reference.isContainment(isContainment);

            mapper.metaclassOf(id).ifPresent(m -> {
                BasicNamespace ns = BasicNamespace.Registry.getInstance().getFromUri(m.uri());
                reference.metaclassReference(new BasicMetaclass(ns, m.name()));
            });

            notifyReference(reference);

            next(key.id(), id);
        });
    }

    /**
     * Reads the multi-valued reference of the specified {@code key}.
     *
     * @param key           the key identifying the reference
     * @param isContainment {@code true} if the reference is a containment
     */
    protected void readAllReferences(SingleFeatureKey key, boolean isContainment) {
        int size = mapper.sizeOfReference(key).orElse(0);

        IntStream.range(0, size).forEach(position ->
                mapper.referenceOf(key.withPosition(position)).ifPresent(id -> {
                    BasicReference reference = new BasicReference(key.name());

                    reference.id(BasicId.original(key.id().toString()));
                    reference.idReference(BasicId.original(id.toString()));

                    reference.index(position);

                    reference.isContainment(isContainment);
                    reference.isMany(true);

                    mapper.metaclassOf(id).ifPresent(m -> {
                        BasicNamespace ns = BasicNamespace.Registry.getInstance().getFromUri(m.uri());
                        reference.metaclassReference(new BasicMetaclass(ns, m.name()));
                    });

                    notifyReference(reference);

                    next(key.id(), id);
                }));
    }

    /**
     * Reads the {@code next} element if {@code containerOf(next) == parent}.
     *
     * @param parent the parent identifier of {@code next}
     * @param next   the next identifier
     */
    protected void next(Id parent, Id next) {
        mapper.containerOf(next)
                .filter(c -> Objects.equals(c.id(), parent))
                .ifPresent(container -> readElement(next));
    }
}
