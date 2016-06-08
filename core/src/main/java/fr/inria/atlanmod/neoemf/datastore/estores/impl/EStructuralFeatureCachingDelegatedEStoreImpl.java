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

package fr.inria.atlanmod.neoemf.datastore.estores.impl;

import fr.inria.atlanmod.neoemf.datastore.estores.SearcheableResourceEStore;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * A {@link SearcheableResourceEStore} wrapper that caches {@link EStructuralFeature structural features}.
 * 
 */
public class EStructuralFeatureCachingDelegatedEStoreImpl extends AbstractCachingDelegatedEStore<Object> {
	
	public EStructuralFeatureCachingDelegatedEStoreImpl(SearcheableResourceEStore eStore) {
		super(eStore);
	}

	public EStructuralFeatureCachingDelegatedEStoreImpl(SearcheableResourceEStore eStore, int cacheSize) {
		super(eStore, cacheSize);
	}
	
	@Override
	public Object get(InternalEObject object, EStructuralFeature feature, int index) {
		Object returnValue = getValueFromCache(object, feature, index);
		if (returnValue == null) {
			returnValue = super.get(object, feature, index);
			cacheValue(object, feature, index, returnValue);
		}
		return returnValue;
	}
	
	@Override
	public Object set(InternalEObject object, EStructuralFeature feature, int index, Object value) {
		Object returnValue = super.set(object, feature, index, value);
		cacheValue(object, feature, index, value);
		return returnValue;
	}
	
	@Override
	public void add(InternalEObject object, EStructuralFeature feature, int index, Object value) {
		super.add(object, feature, index, value);
		cacheValue(object, feature, index, value);
		invalidateValues(object, feature, index + 1);
	}
	
	@Override
	public Object remove(InternalEObject object, EStructuralFeature feature, int index) {
		Object returnValue = super.remove(object, feature, index);
		invalidateValues(object, feature, index);
		return returnValue;
	}
	
	@Override
	public void clear(InternalEObject object, EStructuralFeature feature) {
		super.clear(object, feature);
		invalidateValues(object, feature, 0);
	}
	
	@Override
	public Object move(InternalEObject object, EStructuralFeature feature, int targetIndex, int sourceIndex) {
		Object returnValue = super.move(object, feature, targetIndex, sourceIndex);
		invalidateValues(object, feature, Math.min(sourceIndex, targetIndex));
		cacheValue(object, feature, targetIndex, returnValue);
		return returnValue;
	}
	
	@Override
	public void unset(InternalEObject object, EStructuralFeature feature) {
		if (!feature.isMany()) {
			invalidateValue(object, feature);
		} else {
			invalidateValues(object, feature, 0);
		}
		super.unset(object, feature);
	}

	/**
	 * Remove cached elements, from a initial position to the size of an element.
     */
	private void invalidateValues(InternalEObject object, EStructuralFeature feature, int startIndex) {
		for (int i = startIndex; i < size(object, feature); i++) {
			invalidateValue(object, feature, i);
		}
	}
}
