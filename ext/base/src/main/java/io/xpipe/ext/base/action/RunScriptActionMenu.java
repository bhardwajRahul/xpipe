package io.xpipe.ext.base.action;

import io.xpipe.app.comp.store.StoreViewState;
import io.xpipe.app.core.AppI18n;
import io.xpipe.app.ext.ActionProvider;
import io.xpipe.app.storage.DataStoreEntryRef;
import io.xpipe.app.util.TerminalLauncher;
import io.xpipe.core.process.ShellStoreState;
import io.xpipe.core.store.ShellStore;
import io.xpipe.ext.base.script.ScriptHierarchy;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import lombok.Value;

import java.util.List;

public class RunScriptActionMenu implements ActionProvider {

    @Value
    private static class ScriptActionProvider implements ActionProvider {

        ScriptHierarchy hierarchy;

        @Value
        private class Action implements ActionProvider.Action {

            DataStoreEntryRef<ShellStore> shellStore;

            @Override
            public void execute() throws Exception {
                try (var sc = shellStore.getStore().control().start()) {
                    var script = hierarchy.getLeafBase().getStore().assembleScriptChain(sc);
                    TerminalLauncher.open(
                            shellStore.getEntry(),
                            hierarchy.getLeafBase().get().getName() + " - " + shellStore.get().getName(),
                            null,
                            sc.command(script));
                }
            }
        }

        @Override
        public LeafDataStoreCallSite<?> getLeafDataStoreCallSite() {
            if (!hierarchy.isLeaf()) {
                return null;
            }

            return new LeafDataStoreCallSite<ShellStore>() {
                @Override
                public Action createAction(DataStoreEntryRef<ShellStore> store) {
                    return new Action(store);
                }

                @Override
                public ObservableValue<String> getName(DataStoreEntryRef<ShellStore> store) {
                    return new SimpleStringProperty(hierarchy.getBase().get().getName());
                }

                @Override
                public String getIcon(DataStoreEntryRef<ShellStore> store) {
                    return "mdi2p-play-box-multiple-outline";
                }

                @Override
                public Class<?> getApplicableClass() {
                    return ShellStore.class;
                }
            };
        }

        public BranchDataStoreCallSite<?> getBranchDataStoreCallSite() {
            if (hierarchy.isLeaf()) {
                return null;
            }

            return new BranchDataStoreCallSite<ShellStore>() {

                @Override
                public Class<ShellStore> getApplicableClass() {
                    return ShellStore.class;
                }

                @Override
                public ObservableValue<String> getName(DataStoreEntryRef<ShellStore> store) {
                    return new SimpleStringProperty(hierarchy.getBase().get().getName());
                }

                @Override
                public boolean isDynamicallyGenerated() {
                    return true;
                }

                @Override
                public String getIcon(DataStoreEntryRef<ShellStore> store) {
                    return "mdi2p-play-box-multiple-outline";
                }

                @Override
                public List<? extends ActionProvider> getChildren(DataStoreEntryRef<ShellStore> store) {
                    return hierarchy.getChildren().stream().map(c -> new ScriptActionProvider(c)).toList();
                }
            };
        }
    }

    private static class NoScriptsActionProvider implements ActionProvider {

        private static class Action implements ActionProvider.Action {

            @Override
            public void execute() throws Exception {
                StoreViewState.get().getAllScriptsCategory().select();
            }
        }

        @Override
        public LeafDataStoreCallSite<?> getLeafDataStoreCallSite() {
            return new LeafDataStoreCallSite<ShellStore>() {
                @Override
                public Action createAction(DataStoreEntryRef<ShellStore> store) {
                    return new Action();
                }

                @Override
                public ObservableValue<String> getName(DataStoreEntryRef<ShellStore> store) {
                    return AppI18n.observable("noScriptsAvailable");
                }

                @Override
                public String getIcon(DataStoreEntryRef<ShellStore> store) {
                    return "mdi2i-image-filter-none";
                }

                @Override
                public Class<?> getApplicableClass() {
                    return ShellStore.class;
                }
            };
        }
    }

    @Override
    public BranchDataStoreCallSite<?> getBranchDataStoreCallSite() {
        return new BranchDataStoreCallSite<ShellStore>() {

            @Override
            public Class<ShellStore> getApplicableClass() {
                return ShellStore.class;
            }

            @Override
            public boolean isMajor(DataStoreEntryRef<ShellStore> o) {
                return true;
            }

            @Override
            public ObservableValue<String> getName(DataStoreEntryRef<ShellStore> store) {
                return AppI18n.observable("runScript");
            }

            @Override
            public boolean isDynamicallyGenerated() {
                return true;
            }

            @Override
            public String getIcon(DataStoreEntryRef<ShellStore> store) {
                return "mdi2p-play-box-multiple-outline";
            }

            @Override
            public boolean isApplicable(DataStoreEntryRef<ShellStore> o) {
                var state = o.getEntry().getStorePersistentState();
                if (!(state instanceof ShellStoreState shellStoreState) || shellStoreState.getShellDialect() == null) {
                    return false;
                }

                return true;
            }

            @Override
            public List<? extends ActionProvider> getChildren(DataStoreEntryRef<ShellStore> store) {
                var state = store.getEntry().getStorePersistentState();
                if (!(state instanceof ShellStoreState shellStoreState) || shellStoreState.getShellDialect() == null) {
                    return List.of(new NoScriptsActionProvider());
                }

                var hierarchy = ScriptHierarchy.buildEnabledHierarchy(ref -> {
                    if (!ref.getStore().isRunnableScript()) {
                        return false;
                    }

                    if (!ref.getStore().isCompatible(shellStoreState.getShellDialect())) {
                        return false;
                    }

                    return true;
                });
                var list = hierarchy.getChildren().stream().map(c -> new ScriptActionProvider(c)).toList();
                if (list.isEmpty()) {
                    return List.of(new NoScriptsActionProvider());
                } else {
                    return list;
                }
            }
        };
    }
}
