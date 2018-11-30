/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.test.java.JavaTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openhab.binding.vrsmonitor.internal.VrsMonitorBindingConstants;
import org.openhab.binding.vrsmonitor.internal.VrsMonitorHandler;

/**
 * Test cases for {@link VrsMonitorHandler}. The tests provide mocks for supporting entities using Mockito.
 *
 * @author Martin Koehler - Initial contribution
 */
public class VrsMonitorHandlerTest extends JavaTest {

    private ThingHandler handler;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Thing thing;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new VrsMonitorHandler(thing);
        handler.setCallback(callback);
    }

    @Test
    public void initializeShouldCallTheCallback() {
        // mock getConfiguration to prevent NPEs
        when(thing.getUID()).thenReturn(new ThingUID(VrsMonitorBindingConstants.BINDING_ID, "test"));
        when(thing.getConfiguration()).thenReturn(new Configuration());

        // we expect the handler#initialize method to call the callback during execution and
        // pass it the thing and a ThingStatusInfo object containing the ThingStatus of the thing.
        handler.initialize();

        // the argument captor will capture the argument of type ThingStatusInfo given to the
        // callback#statusUpdated method.
        ArgumentCaptor<ThingStatusInfo> statusInfoCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);

        // verify the interaction with the callback and capture the ThingStatusInfo argument:
        waitForAssert(() -> {
            verify(callback, times(1)).statusUpdated(eq(thing), statusInfoCaptor.capture());
        });

        // assert that the (temporary) UNKNOWN status was to the mocked thing first:
        assertThat(statusInfoCaptor.getAllValues().get(0).getStatus(), is(ThingStatus.UNKNOWN));

        waitForAssert(() -> {
            verify(callback, times(1)).statusUpdated(eq(handler.getThing()), statusInfoCaptor.capture());
        });

        // assert that ONLINE status was given later to the edited thing:
        assertThat(statusInfoCaptor.getAllValues().get(1).getStatus(), is(ThingStatus.ONLINE));
    }

}
