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

package fr.inria.atlanmod.neoemf.tests;

import fr.inria.atlanmod.neoemf.AllTest;
import fr.inria.atlanmod.neoemf.test.commons.models.mapSample.MapSampleFactory;
import fr.inria.atlanmod.neoemf.test.commons.models.mapSample.MapSamplePackage;
import fr.inria.atlanmod.neoemf.test.commons.models.mapSample.SampleModel;
import fr.inria.atlanmod.neoemf.test.commons.models.mapSample.SampleModelContentObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Test cases that check List method consistency for the @see{TransientEStoreImpl} class.
 * Issue #7 reported the error for isEmpty method @see{Issue7Test}
 */
public class TransientEStoreListTest extends AllTest {

    protected SampleModel model;
    protected MapSamplePackage mapPackage;
    protected MapSampleFactory mapFactory;

    @Before
    public void setUp() {
        mapPackage = MapSamplePackage.eINSTANCE;
        mapFactory = MapSampleFactory.eINSTANCE;
        this.model = mapFactory.createSampleModel();
    }

    @Test
    public void testListNotNull() {
        assertThat("Accessed list is null", model.getContentObjects(), notNullValue());
    }

    @Test
    public void testAdd() {
        assertThat("Adding valid item to the list returns false", model.getContentObjects().add(mapFactory.createSampleModelContentObject()), is(true));
    }

    @Test
    public void testAddAllCollection() {
        List<SampleModelContentObject> list = new ArrayList<SampleModelContentObject>();
        list.add(mapFactory.createSampleModelContentObject());
        list.add(mapFactory.createSampleModelContentObject());
        assertThat("Adding valid item list returns false", model.getContentObjects().addAll(list), is(true));
    }

    @Test
    public void testAddAllCollectionIndex() {
        List<SampleModelContentObject> list = new ArrayList<SampleModelContentObject>();
        list.add(mapFactory.createSampleModelContentObject());
        list.add(mapFactory.createSampleModelContentObject());
        assertThat("Adding valid item list at a given index returns false", model.getContentObjects().addAll(0, list), is(true));
    }

    @Test
    public void testClear() {
        model.getContentObjects().clear();
        assertThat("List isn't empty after clean", model.getContentObjects().isEmpty(), is(true));
    }

    @Test
    public void testContains() {
        assertThat("Accessed list contains the created element", model.getContentObjects().contains(mapFactory.createSampleModelContentObject()), is(false));
    }

    @Test
    public void testContainsAll() {
        List<SampleModelContentObject> list = new ArrayList<SampleModelContentObject>();
        list.add(mapFactory.createSampleModelContentObject());
        list.add(mapFactory.createSampleModelContentObject());
        assertThat("Accessed list contains the given collection", model.getContentObjects().containsAll(list), is(false));
    }

    @Test
    public void testEquals() {
        List<SampleModelContentObject> list = new ArrayList<SampleModelContentObject>();
        list.add(mapFactory.createSampleModelContentObject());
        list.add(mapFactory.createSampleModelContentObject());
        assertThat("Accessed list is equal to the given collection", model.getContentObjects().equals(list), is(false));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet() {
        model.getContentObjects().get(0);
    }

    @Test
    public void testHashCode() {
        model.getContentObjects().hashCode();
    }

    @Test
    public void testIndexOf() {
        assertThat("IndexOf returns a wrong value", model.getContentObjects().indexOf(mapFactory.createSampleModelContentObject()), equalTo(-1));
    }

    @Test
    public void testIsEmpty() {
        assertThat("Accessed list is not empty", model.getContentObjects().isEmpty(), is(true));
    }

    @Test
    public void testIterator() {
        model.getContentObjects().iterator();
    }

    @Test
    public void testLastIndexOf() {
        assertThat("LastIndexOf returns a wrong value", model.getContentObjects().lastIndexOf(mapFactory.createSampleModelContentObject()), equalTo(-1));
    }

    @Test
    public void testListIterator() {
        model.getContentObjects().listIterator();
    }

    @Test
    public void testListIteratorIndex() {
        model.getContentObjects().listIterator(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testListIteratorInvalidIndex() {
        model.getContentObjects().listIterator(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIndex() {
        model.getContentObjects().move(0, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveObject() {
        model.getContentObjects().move(0, mapFactory.createSampleModelContentObject());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveInvalidIndex() {
        model.getContentObjects().remove(0);
    }

    @Test
    public void testRemoveObject() {
        model.getContentObjects().remove(mapFactory.createSampleModelContentObject());
    }

    @Test
    public void testRemoveAllCollection() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().removeAll(collection);
    }

    @Test
    public void testRemoveAllEmptyCollection() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        model.getContentObjects().removeAll(collection);
    }

    @Test
    public void testRetainAllCollection() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().retainAll(collection);
    }

    @Test
    public void testRetainAllEmptyCollection() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        model.getContentObjects().retainAll(collection);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetInvalidIndex() {
        model.getContentObjects().set(0, mapFactory.createSampleModelContentObject());
    }

    @Test
    public void testSetValidIndex() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().addAll(collection);
        model.getContentObjects().set(0, mapFactory.createSampleModelContentObject());
    }

    @Test(expected = ClassCastException.class)
    public void testSetInvalidObject() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().addAll(collection);
        model.getContentObjects().set(0, (SampleModelContentObject) mapFactory.createSampleModel());
    }

    @Test
    public void testSizeEmptyList() {
        assertThat(model.getContentObjects().size(), equalTo(0));
    }

    @Test
    public void testSize() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().addAll(collection);
        assertThat(model.getContentObjects().size(), equalTo(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSubListInvalidIndexes() {
        model.getContentObjects().subList(0, 1);
    }

    @Test
    public void testSubList() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().addAll(collection);
        assertThat(model.getContentObjects().subList(0, 1).size(), equalTo(1));
    }

    @Test
    public void testToArrayEmptyList() {
        assertThat(model.getContentObjects().toArray().length, equalTo(0));
    }

    @Test
    public void testToArray() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().addAll(collection);
        assertThat(model.getContentObjects().toArray().length, equalTo(2));
    }

    @Test
    public void testToArrayParameterEmptyList() {
        Object[] array = new Object[0];
        model.getContentObjects().toArray(array);
    }

    @Test
    public void testToArrayParameter() {
        Object[] array = new Object[2];
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().addAll(collection);
        assertThat(model.getContentObjects().toArray(array).length, equalTo(2));
    }

    @Test
    public void testToStringEmptyList() {
        model.getContentObjects().toString();
    }

    @Test
    public void testToString() {
        Collection<SampleModelContentObject> collection = new ArrayList<SampleModelContentObject>();
        collection.add(mapFactory.createSampleModelContentObject());
        collection.add(mapFactory.createSampleModelContentObject());
        model.getContentObjects().addAll(collection);
        model.getContentObjects().toString();
    }

}
