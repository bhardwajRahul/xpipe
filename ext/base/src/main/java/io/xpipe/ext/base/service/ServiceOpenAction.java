package io.xpipe.ext.base.service;

import io.xpipe.app.core.AppI18n;
import io.xpipe.app.ext.ActionProvider;
import io.xpipe.app.storage.DataStoreEntryRef;
import io.xpipe.app.util.Hyperlinks;

import javafx.beans.value.ObservableValue;

import lombok.Value;

public class ServiceOpenAction implements ActionProvider {

    @Override
    public LeafDataStoreCallSite<?> getLeafDataStoreCallSite() {
        return new LeafDataStoreCallSite<AbstractServiceStore>() {

            @Override
            public boolean isMajor(DataStoreEntryRef<AbstractServiceStore> o) {
                return true;
            }

            @Override
            public boolean canLinkTo() {
                return true;
            }

            @Override
            public ActionProvider.Action createAction(DataStoreEntryRef<AbstractServiceStore> store) {
                return new Action(store.getStore());
            }

            @Override
            public Class<AbstractServiceStore> getApplicableClass() {
                return AbstractServiceStore.class;
            }

            @Override
            public ObservableValue<String> getName(DataStoreEntryRef<AbstractServiceStore> store) {
                return AppI18n.observable("openWebsite");
            }

            @Override
            public String getIcon(DataStoreEntryRef<AbstractServiceStore> store) {
                return "mdi2s-search-web";
            }
        };
    }

    @Value
    static class Action implements ActionProvider.Action {

        AbstractServiceStore serviceStore;

        @Override
        public void execute() throws Exception {
            serviceStore.startSessionIfNeeded();
            var l = serviceStore.getSession().getLocalPort();
            Hyperlinks.open("http://localhost:" + l);
        }
    }
}
