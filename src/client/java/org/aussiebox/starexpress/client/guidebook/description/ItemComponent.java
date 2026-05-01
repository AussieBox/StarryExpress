package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

import java.util.Optional;

public class ItemComponent extends DescriptionComponent {
    public Item item;
    public int itemSizing;

    public ItemComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("item"))
            throw new MissingJsonFieldException("JSON Object did not contain item parameter");
        Optional<Item> optionalItem = Registries.ITEM.getOrEmpty(Identifier.of(object.get("item").getAsString()));
        if (optionalItem.isPresent()) item = optionalItem.get();
        else throw new NullPointerException("Item returned null after parsing");
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
        flow.child(Components.item(new ItemStack(item)).sizing(Sizing.fixed(itemSizing)));
        return flow;
    }
}
