package org.aussiebox.starexpress.client.guidebook.component.description;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import org.aussiebox.starexpress.client.guidebook.component.DescriptionElement;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;
import org.aussiebox.starexpress.util.StarryExpressUtil;

public class BlockElement extends DescriptionElement {
    public BlockState block;
    public int blockSizing;

    public BlockElement(String id, JsonObject object) {
        super(id, object);
        if (!object.has("block"))
            throw new MissingJsonFieldException("JSON Object did not contain block parameter");
        block = StarryExpressUtil.parseBlockStateFromJson(object.get("block"));
        try {
            blockSizing = object.get("sizing").getAsInt();
        } catch (Exception e) {
            blockSizing = 16;
        }
    }

    @Override
    public FlowLayout build() {
        FlowLayout flow = Containers.horizontalFlow(Sizing.expand(), Sizing.content());
        flow.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);
        flow.child(Components.block(block).tooltip(block.getBlock().asItem().getDefaultStack().getTooltip(Item.TooltipContext.DEFAULT, null, TooltipType.BASIC)).sizing(Sizing.fixed(blockSizing)));
        return flow;
    }
}
