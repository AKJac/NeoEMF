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

package fr.inria.atlanmod.neoemf.data;

import fr.inria.atlanmod.neoemf.context.Context;
import fr.inria.atlanmod.neoemf.context.CoreContext;
import fr.inria.atlanmod.neoemf.context.CoreTest;
import fr.inria.atlanmod.neoemf.data.store.AutoSaveStoreDecorator;
import fr.inria.atlanmod.neoemf.data.store.DirectWriteStore;
import fr.inria.atlanmod.neoemf.data.store.FeatureCachingStoreDecorator;
import fr.inria.atlanmod.neoemf.data.store.IsSetCachingStoreDecorator;
import fr.inria.atlanmod.neoemf.data.store.LoadedObjectCounterStoreDecorator;
import fr.inria.atlanmod.neoemf.data.store.LoggingStoreDecorator;
import fr.inria.atlanmod.neoemf.data.store.SizeCachingStoreDecorator;
import fr.inria.atlanmod.neoemf.data.store.Store;
import fr.inria.atlanmod.neoemf.option.CommonOptions;
import fr.inria.atlanmod.neoemf.option.InvalidOptionException;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases about {@link BackendFactory#createStore(Backend, PersistentResource, Map)}.
 */
public class PersistentBackendFactoryTest extends AbstractBackendFactoryTest implements CoreTest {

    /**
     * Checks the setup of the default store, without any decorator ({@link DirectWriteStore}).
     */
    @Test
    public void testNoOption() {
        Map<String, Object> options = CommonOptions.noOption();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the default store, without any decorator ({@link DirectWriteStore}).
     */
    @Test
    public void testAlreadyDefinedOption() {
        Throwable thrown = catchThrowable(() -> CommonOptions.newBuilder().autoSave().autoSave());
        assertThat(thrown).isExactlyInstanceOf(InvalidOptionException.class);
    }

    /**
     * Checks the setup of the {@link IsSetCachingStoreDecorator}.
     */
    @Test
    public void testIsSetCachingOption() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .cacheIsSet()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(IsSetCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the {@link LoggingStoreDecorator}.
     */
    @Test
    public void testLoggingOption() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .log()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(LoggingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the {@link SizeCachingStoreDecorator}.
     */
    @Test
    public void testSizeCachingOption() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .cacheSizes()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(SizeCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the {@link FeatureCachingStoreDecorator}.
     */
    @Test
    public void testFeatureCachingOption() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .cacheFeatures()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(FeatureCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the {@link LoadedObjectCounterStoreDecorator}.
     */
    @Test
    public void testLoadedObjectCounterLoggingOption() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .countLoadedObjects()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(LoadedObjectCounterStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the {@link AutoSaveStoreDecorator} without chunk.
     */
    @Test
    public void testAutoSaveOption() {
        final long expectedChunk = 50_000L;

        Map<String, Object> options = CommonOptions.newBuilder()
                .autoSave()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(AutoSaveStoreDecorator.class);

        long actualChunk = getValue(store, "autoSaveChunk", AutoSaveStoreDecorator.class, Long.class);
        assertThat(actualChunk).isEqualTo(expectedChunk);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the {@link AutoSaveStoreDecorator} with chunk.
     */
    @Test
    public void testAutoSaveWithChunkOption() {
        final long expectedChunk = 12_345;

        Map<String, Object> options = CommonOptions.newBuilder()
                .autoSave(expectedChunk)
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(AutoSaveStoreDecorator.class);

        long actualChunk = getValue(store, "autoSaveChunk", AutoSaveStoreDecorator.class, Long.class);
        assertThat(actualChunk).isEqualTo(expectedChunk);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks the setup of the {@link AutoSaveStoreDecorator} with negative chunk.
     */
    @Test
    public void testAutoSaveWithNegativeChunkOption() {
        Throwable thrown = catchThrowable(() -> CommonOptions.newBuilder().autoSave(-2));
        assertThat(thrown).isExactlyInstanceOf(InvalidOptionException.class);
    }

    /**
     * Checks store containment order (depend on the instantiation policy defined in {@link BackendFactory}.
     * <ul>
     * <li>{@link IsSetCachingStoreDecorator}</li>
     * <li>{@link LoggingStoreDecorator}</li>
     * </ul>
     */
    @Test
    public void testIsSetCachingLoggingOptions() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .cacheIsSet()
                .log()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isExactlyInstanceOf(LoggingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(IsSetCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks store containment order (depend on the instantiation policy defined in {@link BackendFactory}.
     * <ul>
     * <li>{@link IsSetCachingStoreDecorator}</li>
     * <li>{@link SizeCachingStoreDecorator}</li>
     * </ul>
     */
    @Test
    public void testIsSetCachingSizeCachingOptions() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .cacheIsSet()
                .cacheSizes()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isExactlyInstanceOf(SizeCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(IsSetCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks store containment order (depend on the instantiation policy defined in {@link BackendFactory}.
     * <ul>
     * <li>{@link SizeCachingStoreDecorator}</li>
     * <li>{@link FeatureCachingStoreDecorator}</li>
     * </ul>
     */
    @Test
    public void testSizeCachingFeatureCachingOptions() {
        Map<String, Object> options = CommonOptions.newBuilder()
                .cacheSizes()
                .cacheFeatures()
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isExactlyInstanceOf(FeatureCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(SizeCachingStoreDecorator.class);


        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    /**
     * Checks store containment order (depend on the instantiation policy defined in {@link BackendFactory}.
     * <ul>
     * <li>{@link IsSetCachingStoreDecorator}</li>
     * <li>{@link SizeCachingStoreDecorator}</li>
     * <li>{@link FeatureCachingStoreDecorator}</li>
     * <li>{@link LoggingStoreDecorator}</li>
     * <li>{@link AutoSaveStoreDecorator}</li>
     * </ul>
     */
    @Test
    public void testAllOptions() {
        long expectedChunk = 12_345;

        Map<String, Object> options = CommonOptions.newBuilder()
                .cacheIsSet()
                .cacheSizes()
                .cacheFeatures()
                .log()
                .autoSave(expectedChunk)
                .asMap();

        Store store = context().factory().createStore(mock(Backend.class), mock(PersistentResource.class), options);
        assertThat(store).isInstanceOf(AutoSaveStoreDecorator.class);

        long actualChunk = getValue(store, "autoSaveChunk", AutoSaveStoreDecorator.class, Long.class);
        assertThat(actualChunk).isEqualTo(expectedChunk);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(LoggingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(FeatureCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(SizeCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(IsSetCachingStoreDecorator.class);

        store = getInnerStore(store);
        assertThat(store).isExactlyInstanceOf(DirectWriteStore.class);
    }

    @Override
    public Context context() {
        return new CoreContext() {

            @Override
            public BackendFactory factory() {
                AbstractBackendFactory factory = (AbstractBackendFactory) super.factory();

                when(factory.createStore(any(), any(), notNull())).thenCallRealMethod();
                when(factory.newInstanceOf(any(), any())).thenCallRealMethod();

                return factory;
            }
        };
    }
}
