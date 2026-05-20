package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.item.ItemStack;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;
import org.aussiebox.starexpress.util.StarryExpressUtil;

public class ItemComponent extends DescriptionComponent {
    public ItemStack item;
    public int itemSizing;

    public ItemComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("item"))
            throw new MissingJsonFieldException("JSON Object did not contain item parameter");
        item = StarryExpressUtil.parseItemStackFromJson(object.get("item"));
        try {
            itemSizing = object.get("sizing").getAsInt();
        } catch (Exception e) {
            itemSizing = 16;
        }
    }

    @Override
    public FlowLayout build() {
        FlowLayout flow = Containers.horizontalFlow(Sizing.expand(), Sizing.content());
        flow.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);
        flow.child(Components.item(item).setTooltipFromStack(true).showOverlay(true).sizing(Sizing.fixed(itemSizing)));
        return flow;
    }
}
