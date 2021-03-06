/*
 * Copyright (c) 2008-2015, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.parameters.MultiMapClearParameters;
import com.hazelcast.client.impl.protocol.parameters.VoidResultParameters;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.OperationFactory;

import java.security.Permission;
import java.util.Map;

/**
 * Client Protocol Task for handling messages with type id:
 * {@link com.hazelcast.client.impl.protocol.parameters.MultiMapMessageType#MULTIMAP_CLEAR}
 */
public class MultiMapClearMessageTask extends AbstractAllPartitionsMessageTask<MultiMapClearParameters> {

    public MultiMapClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    protected OperationFactory createOperationFactory() {
        return new MultiMapOperationFactory(parameters.name, MultiMapOperationFactory.OperationFactoryType.CLEAR);
    }

    @Override
    protected ClientMessage reduce(Map<Integer, Object> map) {
        int totalAffectedEntries = 0;
        for (Object affectedEntries : map.values()) {
            totalAffectedEntries += (Integer) affectedEntries;
        }
        final MultiMapService service = getService(MultiMapService.SERVICE_NAME);
        service.publishMultiMapEvent(parameters.name, EntryEventType.CLEAR_ALL, totalAffectedEntries);
        return VoidResultParameters.encode();
    }


    @Override
    protected MultiMapClearParameters decodeClientMessage(ClientMessage clientMessage) {
        return null;
    }

    @Override
    public String getServiceName() {
        return MultiMapService.SERVICE_NAME;
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(parameters.name, ActionConstants.ACTION_REMOVE);
    }

    @Override
    public String getDistributedObjectName() {
        return parameters.name;
    }

    @Override
    public String getMethodName() {
        return "clear";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}
