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

package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.parameters.MapAddEntryListenerToKeyWithPredicateParameters;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.QueryEventFilter;
import com.hazelcast.nio.Connection;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.EventFilter;

public class MapAddEntryListenerToKeyWithPredicateMessageTask
        extends AbstractMapAddEntryListenerMessageTask<MapAddEntryListenerToKeyWithPredicateParameters> {

    public MapAddEntryListenerToKeyWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected EventFilter getEventFilter() {
        final Predicate predicate = serializationService.toObject(parameters.predicate);
        return new QueryEventFilter(parameters.includeValue, parameters.key, predicate);
    }

    @Override
    protected MapAddEntryListenerToKeyWithPredicateParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddEntryListenerToKeyWithPredicateParameters.decode(clientMessage);
    }

    @Override
    public String getDistributedObjectName() {
        return parameters.name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, parameters.predicate, parameters.key, parameters.includeValue};
    }
}
