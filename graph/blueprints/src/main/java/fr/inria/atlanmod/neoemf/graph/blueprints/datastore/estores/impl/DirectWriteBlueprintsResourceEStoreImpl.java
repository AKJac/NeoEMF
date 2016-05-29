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

package fr.inria.atlanmod.neoemf.graph.blueprints.datastore.estores.impl;

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.core.impl.NeoEObjectAdapterFactoryImpl;
import fr.inria.atlanmod.neoemf.datastore.InternalPersistentEObject;
import fr.inria.atlanmod.neoemf.datastore.estores.SearcheableResourceEStore;
import fr.inria.atlanmod.neoemf.graph.blueprints.datastore.BlueprintsPersistenceBackend;
import fr.inria.atlanmod.neoemf.logger.NeoLogger;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Arrays;
import java.util.Map;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

public class DirectWriteBlueprintsResourceEStoreImpl implements SearcheableResourceEStore {

	private static final String SEPARATOR = ":";
	private static final String POSITION = "position";
	private static final String SIZE_LITERAL = "size";

	private static final String CONTAINER = "eContainer";
	private static final String CONTAINING_FEATURE = "containingFeature";

	protected BlueprintsPersistenceBackend graph;
	private Resource.Internal resource;

	public DirectWriteBlueprintsResourceEStoreImpl(Resource.Internal resource, BlueprintsPersistenceBackend graph) {
		this.graph = graph;
		this.resource = resource;
        NeoLogger.info("DirectWrite Store Created");
	}

	@Override
	public Object get(InternalEObject object, EStructuralFeature feature, int index) {
		Object returnValue;
		if (feature instanceof EAttribute) {
			returnValue = get(object, (EAttribute) feature, index);
		} else if (feature instanceof EReference) {
			returnValue = get(object, (EReference) feature, index);
		} else {
			throw new IllegalArgumentException(feature.toString());
		}
		return returnValue;
	}

	protected Object get(InternalEObject object, EAttribute eAttribute, int index) {
		Vertex vertex = graph.getVertex(object);
		String propertyName = eAttribute.getName();
		if (eAttribute.isMany()) {
			checkElementIndex(index, getSize(vertex, eAttribute));
			propertyName += SEPARATOR + index;
		}
		return parseProperty(eAttribute, vertex.getProperty(propertyName));
	}

	protected Object get(InternalEObject object, EReference eReference, int index) {
		Object returnValue = null;
		Vertex vertex = graph.getVertex(object);
		Vertex referencedVertex;
		if (!eReference.isMany()) {
			referencedVertex = Iterables.getOnlyElement(
					vertex.getVertices(Direction.OUT, eReference.getName()), null
			);
		} else {
			checkElementIndex(index, getSize(vertex, eReference));
			referencedVertex = Iterables.getOnlyElement(
					vertex.query()
							.labels(eReference.getName())
							.direction(Direction.OUT)
							.has(POSITION, index)
							.vertices()
					, null);
		}
		if (referencedVertex != null) {
			returnValue = reifyVertex(referencedVertex);
		}
		return returnValue;
	}

	@Override
	public Object set(InternalEObject object, EStructuralFeature feature, int index, Object value) {
		Object returnValue;
		if (value == null) {
			returnValue = get(object, feature, index);
			clear(object, feature);
		} else {
			if (feature instanceof EAttribute) {
				returnValue = set(object, (EAttribute) feature, index, value);
			} else if (feature instanceof EReference) {
				returnValue = set(object, (EReference) feature, index, (EObject) value);
			} else {
				throw new IllegalArgumentException(feature.toString());
			}
		}
		return returnValue;
	}

	protected Object set(InternalEObject object, EAttribute eAttribute, int index, Object value) {
		Object returnValue;
		Vertex vertex = graph.getOrCreateVertex(object);
		String propertyName = eAttribute.getName();
		if (!eAttribute.isMany()) {
			Object property = vertex.getProperty(propertyName);
			returnValue = parseProperty(eAttribute, property);
		} else {
			checkElementIndex(index, getSize(vertex, eAttribute));
			propertyName += SEPARATOR + index;
			returnValue = vertex.getProperty(propertyName);
		}
		vertex.setProperty(propertyName, serializeToProperty(eAttribute, value));
		return returnValue;
	}

	protected Object set(InternalEObject object, EReference eReference, int index, EObject value) {
		Object returnValue = null;
		Vertex vertex = graph.getOrCreateVertex(object);
		Vertex newReferencedVertex = graph.getOrCreateVertex(value);

		// Update the containment reference if needed
		if (eReference.isContainment()) {
			updateContainment(eReference, vertex, newReferencedVertex);
		}

		if (!eReference.isMany()) {
			Edge edge = Iterables.getOnlyElement(
					vertex.getEdges(Direction.OUT, eReference.getName()), null);
			if (edge != null) {
				Vertex referencedVertex = edge.getVertex(Direction.IN);
				returnValue = reifyVertex(referencedVertex);
				edge.remove();
			}
			vertex.addEdge(eReference.getName(), newReferencedVertex);
		} else {
			checkElementIndex(index, getSize(vertex, eReference));
			Iterable<Edge> edges = vertex.query()
					.labels(eReference.getName())
					.direction(Direction.OUT)
					.has(POSITION, index)
					.edges();

			for (Edge edge : edges) {
				Vertex referencedVertex = edge.getVertex(Direction.IN);
				returnValue = reifyVertex(referencedVertex);
				edge.remove();
			}
			Edge edge = vertex.addEdge(eReference.getName(), newReferencedVertex);
			edge.setProperty(POSITION, index);
		}
		return returnValue;
	}

	@Override
	public boolean isSet(InternalEObject object, EStructuralFeature feature) {
		boolean returnValue;
		if (feature instanceof EAttribute) {
			returnValue = isSet(object, (EAttribute) feature);
		} else if (feature instanceof EReference) {
			returnValue = isSet(object, (EReference) feature);
		} else {
			throw new IllegalArgumentException(feature.toString());
		}
		return returnValue;
	}

	protected boolean isSet(InternalEObject object, EAttribute eAttribute) {
		boolean returnValue = false;
		Vertex vertex = graph.getVertex(object);
		if (vertex != null) {
			String propertyName = eAttribute.getName();
			if (eAttribute.isMany()) {
				propertyName += SEPARATOR + SIZE_LITERAL;
			}
			returnValue = null != vertex.getProperty(propertyName);
		}
		return returnValue;
	}

	protected boolean isSet(InternalEObject object, EReference eReference) {
		boolean returnValue = false;
		Vertex vertex = graph.getVertex(object);
		if (vertex != null) {
			returnValue = !Iterables.isEmpty(vertex.getVertices(Direction.OUT, eReference.getName()));
		}
		return returnValue;
	}

	@Override
	public void unset(InternalEObject object, EStructuralFeature feature) {
		if (feature instanceof EAttribute) {
			unset(object, (EAttribute) feature);
		} else if (feature instanceof EReference) {
			unset(object, (EReference) feature);
		} else {
			throw new IllegalArgumentException(feature.toString());
		}
	}

	protected void unset(InternalEObject object, EAttribute eAttribute) {
		Vertex vertex = graph.getVertex(object);
		String propertyName = eAttribute.getName();
		if (eAttribute.isMany()) {
			propertyName += SEPARATOR + SIZE_LITERAL;
			Integer size = vertex.getProperty(propertyName);
			for (int i = 0; i < size; i++) {
				vertex.removeProperty(eAttribute.getName() + SEPARATOR + i);
			}
		}
		vertex.removeProperty(propertyName);
	}

	protected void unset(InternalEObject object, EReference eReference) {
		Vertex vertex = graph.getVertex(object);
		if (!eReference.isMany()) {
			Edge edge = Iterables.getOnlyElement(vertex.getEdges(Direction.OUT, eReference.getName()), null);
			if (edge != null) {
				edge.remove();
			}
		} else {
			for (Edge edge : vertex.query().labels(eReference.getName()).direction(Direction.OUT).edges()) {
				edge.remove();
			}
			vertex.removeProperty(eReference.getName() + SEPARATOR + SIZE_LITERAL);
		}
	}

	@Override
	public boolean isEmpty(InternalEObject object, EStructuralFeature feature) {
		return size(object, feature) == 0;
	}

	@Override
	public int size(InternalEObject object, EStructuralFeature feature) {
		Vertex vertex = graph.getVertex(object);
		return vertex != null ? getSize(vertex, feature) : 0;
	}

	protected static Integer getSize(Vertex vertex, EStructuralFeature feature) {
		Integer size = vertex.getProperty(feature.getName() + SEPARATOR + SIZE_LITERAL);
		return size != null ? size : 0;
	}

	protected static void setSize(Vertex vertex, EStructuralFeature feature, int size) {
		vertex.setProperty(feature.getName() + SEPARATOR + SIZE_LITERAL, size);
	}

	@Override
	public boolean contains(InternalEObject object, EStructuralFeature feature, Object value) {
		boolean found = false;
		if (value != null) {
		    Vertex v = graph.getOrCreateVertex(object);
		    InternalPersistentEObject eValue = checkNotNull(
					NeoEObjectAdapterFactoryImpl.getAdapter(value, InternalPersistentEObject.class)
			);
		    if(feature instanceof EReference) {
				for (Vertex vOut : v.getVertices(Direction.OUT, feature.getName())) {
					if (vOut.getId().equals(eValue.id().toString())) {
						return true;
					}
				}
			}
		    else {
		        // feature is an EAttribute
				found = ArrayUtils.contains(toArray(object, feature), value);
		    }
		}
		return found;
	}

	@Override
	public int indexOf(InternalEObject object, EStructuralFeature feature, Object value) {
		int resultValue = ArrayUtils.INDEX_NOT_FOUND;
	    if(feature instanceof EAttribute) {
			resultValue = ArrayUtils.indexOf(toArray(object, feature), value);
	    }
	    else if(feature instanceof EReference) {
	        if(value != null) {
				Vertex inVertex = graph.getVertex(object);
				Vertex outVertex = graph.getVertex((EObject) value);
				for (Edge e : outVertex.getEdges(Direction.IN, feature.getName())) {
					if (e.getVertex(Direction.OUT).equals(inVertex)) {
						return e.getProperty(POSITION);
					}
				}
			}
	    }
	    else {
	        throw new IllegalArgumentException(feature.toString());
	    }
		return resultValue;
	}

	@Override
	public int lastIndexOf(InternalEObject object, EStructuralFeature feature, Object value) {
		int resultValue;
	    if(feature instanceof EAttribute) {
			resultValue = ArrayUtils.lastIndexOf(toArray(object, feature), value);
	    }
	    else if(feature instanceof EReference) {
			if (value == null) {
				resultValue = ArrayUtils.INDEX_NOT_FOUND;
			} else {
				Vertex inVertex = graph.getVertex(object);
				Vertex outVertex = graph.getVertex((EObject) value);
				Edge lastPositionEdge = null;
				for (Edge e : outVertex.getEdges(Direction.IN, feature.getName())) {
					if (e.getVertex(Direction.OUT).equals(inVertex)
							&& (lastPositionEdge == null
							|| (int) e.getProperty(POSITION) > (int) lastPositionEdge.getProperty(POSITION))) {
						lastPositionEdge = e;
					}
				}
				resultValue = lastPositionEdge == null ?
						ArrayUtils.INDEX_NOT_FOUND :
						(int) lastPositionEdge.getProperty(POSITION);
			}
		}
	    else {
	        throw new IllegalArgumentException(feature.toString());
	    }
		return resultValue;
	}

	@Override
	public void add(InternalEObject object, EStructuralFeature feature, int index, Object value) {
		if (feature instanceof EAttribute) {
			add(object, (EAttribute) feature, index, value);
		} else if (feature instanceof EReference) {
			add(object, (EReference) feature, index, (EObject) value);
		} else {
			throw new IllegalArgumentException(feature.toString());
		}
	}

	protected void add(InternalEObject object, EAttribute eAttribute, int index, Object value) {
		Vertex vertex = graph.getOrCreateVertex(object);
		Integer size = getSize(vertex, eAttribute);
		size++;
		setSize(vertex, eAttribute, size);
		checkPositionIndex(index, size);
		for (int i = size - 1; i > index; i--) {
			Object movingProperty = vertex.getProperty(eAttribute.getName() + SEPARATOR + (i - 1));
			vertex.setProperty(eAttribute.getName() + SEPARATOR + i, movingProperty);
		}
		vertex.setProperty(eAttribute.getName() + SEPARATOR + index, serializeToProperty(eAttribute, value));
	}

	protected void add(InternalEObject object, EReference eReference, int index, EObject value) {
		Vertex vertex = graph.getOrCreateVertex(object);

		Vertex referencedVertex = graph.getOrCreateVertex(value);
		// Update the containment reference if needed
		if (eReference.isContainment()) {
			updateContainment(eReference, vertex, referencedVertex);
		}

		Integer size = getSize(vertex, eReference);
		int newSize = size + 1;
		checkPositionIndex(index, newSize);
		if(index != size) {
			Iterable<Edge> edges = vertex.query()
					.labels(eReference.getName())
					.direction(Direction.OUT)
					.interval(POSITION, index, newSize)
					.edges();

		    // Avoid unnecessary database access
			for (Edge edge : edges) {
				int position = edge.getProperty(POSITION);
				edge.setProperty(POSITION, position + 1);
			}
		}
		Edge edge = vertex.addEdge(eReference.getName(), referencedVertex);
		edge.setProperty(POSITION, index);

		setSize(vertex, eReference, newSize);
	}

	@Override
	public Object remove(InternalEObject object, EStructuralFeature feature, int index) {
		Object returnValue;
		if (feature instanceof EAttribute) {
			returnValue = remove(object, (EAttribute) feature, index);
		} else if (feature instanceof EReference) {
			returnValue = remove(object, (EReference) feature, index);
		} else {
			throw new IllegalArgumentException(feature.toString());
		}
		return returnValue;
	}

	protected Object remove(InternalEObject object, EAttribute eAttribute, int index) {
		Vertex vertex = graph.getVertex(object);
		Integer size = getSize(vertex, eAttribute);
		Object returnValue;
		checkPositionIndex(index, size);
		returnValue = parseProperty(eAttribute, vertex.getProperty(eAttribute.getName() + SEPARATOR + index));
		int newSize = size - 1;
		for (int i = newSize; i > index; i--) {
			Object movingProperty = vertex.getProperty(eAttribute.getName() + SEPARATOR + i);
			vertex.setProperty(eAttribute.getName() + SEPARATOR + (i - 1), movingProperty);
		}
		setSize(vertex, eAttribute, newSize);
		return returnValue;
	}

	protected Object remove(InternalEObject object, EReference eReference, int index) {
		Vertex vertex = graph.getVertex(object);
		String referenceName = eReference.getName();
		Integer size = getSize(vertex, eReference);
		InternalEObject returnValue = null;
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException();
		} else {
			Iterable<Edge> edges = vertex.query()
					.labels(referenceName)
					.direction(Direction.OUT)
					.interval(POSITION, index, size)
					.edges();

			for (Edge edge : edges) {
				int position = edge.getProperty(POSITION);
				if (position == index) {
					Vertex referencedVertex = edge.getVertex(Direction.IN);
					returnValue = reifyVertex(referencedVertex);
					edge.remove();
					if (eReference.isContainment()) {
						for (Edge conEdge : referencedVertex.getEdges(Direction.OUT, CONTAINER)) {
							conEdge.remove();
						}
					}
				} else {
					edge.setProperty(POSITION, position - 1);
				}
			}
		}
		setSize(vertex, eReference, size - 1); // Update size
		checkNotNull(returnValue);
		if(eReference.isContainment()) {
			returnValue.eBasicSetContainer(null, -1, null);
			((InternalPersistentEObject)returnValue).resource(null);
		}
		return returnValue;
	}

	@Override
	public Object move(InternalEObject object, EStructuralFeature feature, int targetIndex, int sourceIndex) {
		Object movedElement = remove(object, feature, sourceIndex);
		add(object, feature, targetIndex, movedElement);
		return movedElement;
	}

	@Override
	public void clear(InternalEObject object, EStructuralFeature feature) {
		if (feature instanceof EAttribute) {
			clear(object, (EAttribute) feature);
		} else if (feature instanceof EReference) {
			clear(object, (EReference) feature);
		} else {
			throw new IllegalArgumentException(feature.toString());
		}
	}

	protected void clear(InternalEObject object, EAttribute eAttribute) {
		Vertex vertex = graph.getVertex(object);
		Integer size = getSize(vertex, eAttribute);
		for (int i = 0; i < size; i++) {
			vertex.removeProperty(eAttribute.getName() + SEPARATOR + i);
		}
		setSize(vertex, eAttribute, 0);
	}

	protected void clear(InternalEObject object, EReference eReference) {
		Vertex vertex = graph.getOrCreateVertex(object);
		for (Edge edge : vertex.query().labels(eReference.getName()).direction(Direction.OUT).edges()) {
			edge.remove();
		}
		setSize(vertex, eReference, 0);
	}

	@Override
	public Object[] toArray(InternalEObject object, EStructuralFeature feature) {
		int size = size(object, feature);
		Object[] result = new Object[size];
		for (int index = 0; index < size; index++) {
			result[index] = get(object, feature, index);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(InternalEObject object, EStructuralFeature feature, T[] array) {
		int size = size(object, feature);
		T[] result = array.length < size ? Arrays.copyOf(array, size) : array;
		for (int index = 0; index < size; index++) {
			result[index] = (T) get(object, feature, index);
		}
		return result;
	}

	@Override
	public int hashCode(InternalEObject object, EStructuralFeature feature) {
		return Arrays.hashCode(toArray(object, feature));
	}

	@Override
	public InternalEObject getContainer(InternalEObject object) {
		InternalEObject returnValue = null;
		Vertex vertex = graph.getVertex(object);
		Vertex containerVertex = Iterables.getOnlyElement(vertex.getVertices(Direction.OUT, CONTAINER), null);
		if (containerVertex != null) {
			returnValue = reifyVertex(containerVertex);
		}
		return returnValue;
	}

	@Override
	public EStructuralFeature getContainingFeature(InternalEObject object) {
		EStructuralFeature resultValue = null;
		Vertex vertex = graph.getVertex(object);
		Edge edge = Iterables.getOnlyElement(vertex.getEdges(Direction.OUT, CONTAINER), null);
		if (edge != null) {
			String featureName = edge.getProperty(CONTAINING_FEATURE);
			Vertex containerVertex = edge.getVertex(Direction.IN);
	        if (featureName != null) {
                EObject container = reifyVertex(containerVertex);
				resultValue = container.eClass().getEStructuralFeature(featureName);
			}
		}
		return resultValue;
	}

	@Override
	public EObject create(EClass eClass) {
		throw new IllegalStateException("This method should not be called");
	}

	protected static Object parseProperty(EAttribute eAttribute, Object property) {
		return property != null ? EcoreUtil.createFromString(eAttribute.getEAttributeType(), property.toString()) : null;
	}

	protected static Object serializeToProperty(EAttribute eAttribute, Object value) {
		return value != null ? EcoreUtil.convertToString(eAttribute.getEAttributeType(), value) : null;
	}

	protected static void updateContainment(EReference eReference, Vertex parentVertex, Vertex childVertex) {
		for (Edge edge : childVertex.getEdges(Direction.OUT, CONTAINER)) {
			edge.remove();
		}
		Edge edge = childVertex.addEdge(CONTAINER, parentVertex);
		edge.setProperty(CONTAINING_FEATURE, eReference.getName());
	}

	protected InternalEObject reifyVertex(Vertex vertex) {
		return reifyVertex(vertex, null);
	}

	protected InternalEObject reifyVertex(Vertex vertex, EClass eClass) {
		InternalPersistentEObject internalEObject = graph.reifyVertex(vertex, eClass);
		if(internalEObject.resource() != resource()) {
			if(Iterables.isEmpty(vertex.getEdges(Direction.OUT, CONTAINER))) {
				if(!Iterables.isEmpty(vertex.getVertices(Direction.IN,"eContents"))) {
					internalEObject.resource(resource());
				}
				// else : not part of the resource
			}
			else {
				internalEObject.resource(resource());
			}
		}
		return internalEObject;
	}
	
	@Override
	public EObject eObject(Id uriFragment) {
		Vertex vertex = graph.getVertex(uriFragment);
		return vertex != null ? reifyVertex(vertex) : null;
	}

	@Override
	public Resource.Internal resource() {
		return resource;
	}
	
	@Override
	public EList<EObject> getAllInstances(EClass eClass, boolean strict) {
		Map<EClass, Iterable<Vertex>> indexHits = graph.getAllInstances(eClass, strict);
		EList<EObject> instances = new BasicEList<>();
		for(Map.Entry<EClass, Iterable<Vertex>> entry : indexHits.entrySet()) {
			for (Vertex instanceVertex : entry.getValue()) {
				instances.add(reifyVertex(instanceVertex, entry.getKey()));
			}
		}
		return instances;
	}
}
