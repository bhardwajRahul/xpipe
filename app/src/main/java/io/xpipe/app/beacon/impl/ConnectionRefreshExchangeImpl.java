package io.xpipe.app.beacon.impl;

import io.xpipe.app.storage.DataStorage;
import io.xpipe.app.util.FixedHierarchyStore;
import io.xpipe.beacon.BeaconClientException;
import io.xpipe.beacon.api.ConnectionRefreshExchange;

import com.sun.net.httpserver.HttpExchange;

public class ConnectionRefreshExchangeImpl extends ConnectionRefreshExchange {

    @Override
    public Object handle(HttpExchange exchange, Request msg) throws Throwable {
        var e = DataStorage.get()
                .getStoreEntryIfPresent(msg.getConnection())
                .orElseThrow(() -> new BeaconClientException("Unknown connection: " + msg.getConnection()));
        if (e.getStore() instanceof FixedHierarchyStore) {
            DataStorage.get().refreshChildren(e, null, true);
        } else {
            e.validateOrThrowAndClose(null);
        }
        return Response.builder().build();
    }
}
