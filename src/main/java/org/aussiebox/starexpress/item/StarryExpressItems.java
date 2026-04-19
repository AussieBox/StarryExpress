package org.aussiebox.starexpress.item;

import dev.doctor4t.ratatouille.util.registrar.ItemRegistrar;
import dev.doctor4t.wathe.index.WatheItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.item.custom.TapeItem;

public interface StarryExpressItems {

    ItemRegistrar registrar = new ItemRegistrar(StarryExpress.MOD_ID);

    Item TAPE = registrar.create("tape", new TapeItem((new Item.Settings()).maxCount(1)), WatheItems.EQUIPMENT_GROUP);

    static void init() {
        registrar.registerEntries();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(StarryExpressItems::addFunctionalEntries);
    }

    static void addFunctionalEntries(FabricItemGroupEntries fabricItemGroupEntries) {
        fabricItemGroupEntries.add(ModBlocks.CIRCUITWEAVER_PLUSH);
        fabricItemGroupEntries.add(ModBlocks.JADE_PLUSH);
    }
}
