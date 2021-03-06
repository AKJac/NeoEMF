/*
 * Copyright (c) 2013-2018 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.io.bean;

import fr.inria.atlanmod.commons.AbstractTest;
import fr.inria.atlanmod.neoemf.core.Id;

import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test-case about {@link BasicReference}.
 */
@ParametersAreNonnullByDefault
class BasicReferenceTest extends AbstractTest {

    @Test
    void testName() {
        String name0 = "reference0";
        String name1 = "reference1";

        BasicReference ref0 = new BasicReference();
        ref0.setName(name0);
        assertThat(ref0.getName()).isEqualTo(name0);

        BasicReference ref1 = new BasicReference();
        ref1.setName(name1);
        assertThat(ref1.getName()).isEqualTo(name1);

        assertThat(ref0.getName()).isNotEqualTo(ref1.getName());
    }

    @Test
    void testId() {
        BasicReference ref0 = new BasicReference();
        ref0.setName("reference0");
        assertThat(ref0.getOwner()).isNull();

        Id id0 = Id.getProvider().fromLong(42);
        Id id1 = Id.getProvider().fromLong(44);

        ref0.setOwner(id0);
        assertThat(ref0.getOwner()).isEqualTo(id0);

        ref0.setOwner(id1);
        assertThat(ref0.getOwner()).isNotEqualTo(id0).isEqualTo(id1);
    }

    @Test
    void testMany() {
        BasicReference ref0 = new BasicReference();
        ref0.setName("reference0");
        assertThat(ref0.isMany()).isFalse();

        ref0.setMany(true);
        assertThat(ref0.isMany()).isTrue();

        ref0.setMany(false);
        assertThat(ref0.isMany()).isFalse();
    }

    @Test
    void testIdReference() {
        BasicReference ref0 = new BasicReference();
        ref0.setName("reference0");
        assertThat(ref0.getValue().isPresent()).isFalse();

        Id idRef0 = Id.getProvider().fromLong(42);
        Id idRef1 = Id.getProvider().fromLong(44);

        ref0.setValue(Data.resolved(idRef0));
        assertThat(ref0.getValue().getResolved()).isEqualTo(idRef0);

        ref0.setValue(Data.resolved(idRef1));
        assertThat(ref0.getValue().getResolved()).isNotEqualTo(idRef0).isEqualTo(idRef1);
    }

    @Test
    void testContainment() {
        BasicReference ref0 = new BasicReference();
        ref0.setName("reference0");
        assertThat(ref0.isContainment()).isFalse();

        ref0.isContainment(true);
        assertThat(ref0.isContainment()).isTrue();

        ref0.isContainment(false);
        assertThat(ref0.isContainment()).isFalse();
    }

    @Test
    void testHashCode() {
        BasicReference ref0 = new BasicReference();
        ref0.setName("reference0");
        BasicReference ref0Bis = new BasicReference();
        ref0Bis.setName("reference0");
        BasicReference ref1 = new BasicReference();
        ref1.setName("reference1");

        assertThat(ref0.hashCode()).isEqualTo(ref0Bis.hashCode());
        assertThat(ref0.hashCode()).isNotEqualTo(ref1.hashCode());
        assertThat(ref1.hashCode()).isNotEqualTo(ref0Bis.hashCode());
    }

    @Test
    void testEquals() {
        BasicReference ref0 = new BasicReference();
        ref0.setName("reference0");
        BasicReference ref0Bis = new BasicReference();
        ref0Bis.setName("reference0");
        BasicReference ref1 = new BasicReference();
        ref1.setName("reference1");

        assertThat(ref0).isEqualTo(ref0Bis);
        assertThat(ref0).isNotEqualTo(ref1);
        assertThat(ref1).isNotEqualTo(ref0Bis);

        assertThat(ref0).isEqualTo(ref0);
        assertThat(ref0).isNotEqualTo(null);
        assertThat(ref0).isNotEqualTo(0);
    }
}