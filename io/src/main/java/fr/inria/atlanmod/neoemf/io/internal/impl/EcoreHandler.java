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

package fr.inria.atlanmod.neoemf.io.internal.impl;

import fr.inria.atlanmod.neoemf.io.beans.Attribute;
import fr.inria.atlanmod.neoemf.io.beans.Classifier;
import fr.inria.atlanmod.neoemf.io.beans.NamedElement;
import fr.inria.atlanmod.neoemf.io.beans.Namespace;
import fr.inria.atlanmod.neoemf.io.beans.Reference;
import fr.inria.atlanmod.neoemf.io.internal.InternalHandler;
import fr.inria.atlanmod.neoemf.logger.NeoLogger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@link InternalHandler internal handler} that creates and links simple elements to an Ecore structure.
 */
public class EcoreHandler extends AbstractDelegatedInternalHandler {

    /**
     * Stack containing previous {@link EClass}.
     */
    private final Deque<EClass> classesStack;

    /**
     * Stack containing previous identifier.
     */
    private final Deque<String> idsStack;

    /**
     * Attribute waiting a value (via {@link #handleCharacters(String)}.
     */
    private Attribute waitingAttribute;

    /**
     * Defines if the previous element was an attribute, or not.
     */
    private boolean lastWasAttribute;

    public EcoreHandler(InternalHandler handler) {
        super(handler);
        this.classesStack = new ArrayDeque<>();
        this.idsStack = new ArrayDeque<>();
        this.lastWasAttribute = false;
    }

    @Override
    public void handleStartElement(Classifier classifier) throws Exception {
        // Is root
        if (classesStack.isEmpty()) {
            createRootObject(classifier);
        }
        // Is a feature of parent
        else {
            handleFeature(classifier);
        }
    }

    @Override
    public void handleCharacters(String characters) throws Exception {
        // Defines the value of the waiting attribute, if exists
        if (waitingAttribute != null) {
            waitingAttribute.setValue(characters);
            super.handleAttribute(waitingAttribute);

            waitingAttribute = null;
        }
    }

    @Override
    public void handleAttribute(Attribute attribute) throws Exception {
        EClass eClass = classesStack.getLast();
        EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(attribute.getLocalName());

        // Checks that the attribute is well a attribute
        if (eStructuralFeature instanceof EAttribute) {
            if (attribute.getNamespace() == null) {
                Namespace ns = Namespace.Registry.getInstance().getFromPrefix(eClass.getEPackage().getNsPrefix());
                attribute.setNamespace(ns);
            }
            super.handleAttribute(attribute);
        }

        // Otherwise redirect to the reference handler
        else if (eStructuralFeature instanceof EReference) {
            NeoLogger.warn(
                    "Feature misinterpreted during the analysis : the attribute {0} is an reference",
                    attribute.getLocalName());

            handleReference(Reference.from(attribute));
        }

        // Not a feature of this class
        else {
            NeoLogger.warn(
                    "Attribute {0}:{1} does not exist in the metamodel. It will be ignored",
                    eClass.getName(), attribute.getLocalName());
        }
    }

    @Override
    public void handleReference(Reference reference) throws Exception {
        EClass eClass = classesStack.getLast();
        EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(reference.getLocalName());

        // Checks that the reference is well a reference
        if (eStructuralFeature instanceof EReference) {
            // Update containment value
            EReference eReference = (EReference) eStructuralFeature;
            reference.setContainment(eReference.isContainment());

            // Update namespace if not already present
            if (reference.getNamespace() == null) {
                EPackage ePackage = eClass.getEPackage();
                reference.setNamespace(Namespace.Registry.getInstance().getFromPrefix(ePackage.getNsPrefix()));
            }
            super.handleReference(reference);
        }

        // Otherwise redirect to the attribute handler
        else if (eStructuralFeature instanceof EAttribute) {
            NeoLogger.warn(
                    "Feature misinterpreted during the analysis : the reference {0} is an attribute",
                    reference.getLocalName());

            handleAttribute(Attribute.from(reference));
        }

        // Not a feature of this class
        else {
            NeoLogger.warn(
                    "Reference {0}:{1} does not exist in the metamodel. It will be ignored",
                    eClass.getName(), reference.getLocalName());
        }
    }

    @Override
    public void handleEndElement() throws Exception {
        if (!lastWasAttribute) {
            classesStack.removeLast();
            idsStack.removeLast();

            super.handleEndElement();
        }

        lastWasAttribute = false;
    }

    /**
     * Creates the root element from the given {@code classifier}.
     */
    private void createRootObject(Classifier classifier) throws Exception {
        Namespace ns = checkNotNull(classifier.getNamespace(),
                "The root element must have a namespace");

        // Retreives the EPackage from NS prefix
        EPackage ePackage = checkNotNull((EPackage) EPackage.Registry.INSTANCE.get(ns.getPrefix()),
                "EPackage " + ns.getPrefix() + " is not registered.");

        // Gets the current EClass
        EClass eClass = (EClass) ePackage.getEClassifier(classifier.getLocalName());

        // Defines the metaclass of the current element if not present
        if (classifier.getMetaclass() == null) {
            classifier.setMetaclass(new NamedElement(ns, eClass.getName()));
        }

        // Defines the classname of the current element
        classifier.setClassName(eClass.getName());

        // Notifies next handlers
        super.handleStartElement(classifier);

        // Saves the current EClass
        classesStack.addLast(eClass);

        // Gets the identifier of the element created by next handlers, and save it
        idsStack.addLast(classifier.getId());
    }

    private void handleFeature(Classifier classifier) throws Exception {
        // Retreive the parent EClass
        EClass parentEClass = classesStack.getLast();

        // Gets the EPackage from it
        EPackage ePackage = parentEClass.getEPackage();
        Namespace ns = Namespace.Registry.getInstance().getFromPrefix(ePackage.getNsPrefix());

        // Gets the structural feature from the parent, according the its local name
        EStructuralFeature eStructuralFeature = parentEClass.getEStructuralFeature(classifier.getLocalName());

        if (eStructuralFeature instanceof EAttribute) {
            handleAttribute(classifier, ns, (EAttribute) eStructuralFeature);
        }
        else if (eStructuralFeature instanceof EReference) {
            handleReference(classifier, ns, (EReference) eStructuralFeature, ePackage);
        }
        else {
            NeoLogger.warn(
                    "Feature {0}:{1} does not exist in the metamodel. It will be ignored",
                    parentEClass.getName(), classifier.getLocalName());
        }
    }

    private void handleAttribute(Classifier classifier, Namespace ns, EAttribute eAttribute) {
        Attribute attr = new Attribute();
        attr.setNamespace(ns);
        attr.setLocalName(eAttribute.getName());

        // Waiting a plain text value
        this.waitingAttribute = attr;
        lastWasAttribute = true;
    }

    private void handleReference(Classifier classifier, Namespace ns, EReference eReference, EPackage ePackage) throws Exception {
        // Gets the type the reference or gets the type from the registered metaclass
        EClass eClass = getEClass(classifier, ns, (EClass) eReference.getEType(), ePackage);

        // Defines the class name and the namespace of the element
        classifier.setClassName(eClass.getName());
        classifier.setNamespace(ns);

        // Notify next handlers of new element, and retreive its identifier
        super.handleStartElement(classifier);
        String currentId = classifier.getId();

        // Create a reference from the parent to this element, with the given local name
        Reference ref = new Reference();
        ref.setNamespace(ns);
        ref.setLocalName(eReference.getName());
        ref.setId(idsStack.getLast());
        ref.setValue(currentId);
        ref.setContainment(eReference.isContainment());
        super.handleReference(ref);

        // Save EClass and identifier
        classesStack.addLast(eClass);
        idsStack.addLast(currentId);
    }

    private EClass getEClass(Classifier classifier, Namespace ns, EClass eClass, EPackage ePackage) throws Exception {
        NamedElement metaClass = classifier.getMetaclass();

        if (metaClass != null) {
            EClass subEClass = (EClass) ePackage.getEClassifier(metaClass.getLocalName());

            // Checks that the metaclass is a subtype of the reference type.
            // If true, use it instead of supertype
            if (eClass.isSuperTypeOf(subEClass)) {
                eClass = subEClass;
            }
            else {
                throw new Exception(subEClass.getName() + " is not a subclass of " + eClass.getName());
            }
        }

        // If not present, create the metaclass from the current class
        else {
            metaClass = new NamedElement(ns, eClass.getName());
            classifier.setMetaclass(metaClass);
        }

        return eClass;
    }
}
