package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.JsonObject;
import dev.doctor4t.wathe.Wathe;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.util.StarryExpressUtil;

public class HotbarComponent extends DescriptionComponent {
    public DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public HotbarComponent(String id, JsonObject object) {
        super(id, object);
        for (int i = 0; i < 9; i++) {
            if (!object.has("item" + i)) continue;
            ItemStack stack = StarryExpressUtil.parseItemStackFromJson(object.get("item" + i));
            items.set(i, stack);
        }
    }

    @Override
    public GridLayout build() {
        GridLayout grid = Containers.grid(Sizing.fixed(182), Sizing.fixed(22), 1, 9);
        grid.surface(Surface.tiled(Wathe.id("textures/gui/sprites/hud/hotbar.png"), 182, 22));
        grid.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        int i = 0;
        for (ItemStack item : items) {
            grid.child(Components.item(item).setTooltipFromStack(true).showOverlay(true).margins(Insets.of(0, 0, 1, 0)), 0, i);
            i++;
        }

        return grid;
    }
}
