package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.websocket.hydra.HydraMenuItem;
import io.getmedusa.medusa.core.websocket.hydra.meta.HydraStatus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.getmedusa.medusa.core.injector.DOMChanges.DOMChange.DOMChangeType;

public class HydraRegistry {

    private static HydraStatus status;

    private HydraRegistry() {}

    public static HydraStatus getStatus() {
        return status;
    }

    public static void update(HydraStatus status) {
        ensureExistingMenuItemsGetAnEmptyUpdateIfGone(status);
        HydraRegistry.status = status;


        DOMChanges menuUpdates = DOMChanges.empty();
        for(Map.Entry<String, Set<HydraMenuItem>> menuItemEntry : status.getMenuItems().entrySet()) {
            menuUpdates.and(menuItemEntry.getKey(), menuItemEntry.getValue(), DOMChangeType.HYDRA_MENU);
        }
        //TODO need an option for 'completely empty', because right now a [] gets returned which gets ignored since it contains no type

        ActiveSessionRegistry.getInstance().sendToAll(menuUpdates.build());
    }

    private static void ensureExistingMenuItemsGetAnEmptyUpdateIfGone(HydraStatus status) {
        if(HydraRegistry.status == null) return;
        Set<String> menusThatNeedAnUpdate = new HashSet<>(HydraRegistry.status.getMenuItems().keySet());
        menusThatNeedAnUpdate.removeAll(status.getMenuItems().keySet());
        for(String menuThatNeedsAnUpdate : menusThatNeedAnUpdate) {
            status.getMenuItems().put(menuThatNeedsAnUpdate, new HashSet<>());
        }
    }
}
