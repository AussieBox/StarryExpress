package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponentRegistry;
import org.aussiebox.starexpress.client.minimessage.ModResolvers;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;
import org.aussiebox.starexpress.util.StarryExpressUtil;

public class TextWithBlockComponent extends DescriptionComponent {
    public String text;
    public BlockState block;
    public Alignment textAlignment;
    public int blockSizing;

    public TextWithBlockComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("text"))
            throw new MissingJsonFieldException("JSON Object did not contain text parameter");
        if (!object.has("block"))
            throw new MissingJsonFieldException("JSON Object did not contain block parameter");
        text = object.get("text").getAsString();
        block = StarryExpressUtil.parseBlockStateFromJson(object.get("block"));
        try {
            textAlignment = Alignment.valueOf(object.get("text_alignment").getAsString());
        } catch (Exception e) {
            textAlignment = Alignment.LEFT;
        }
        try {
            blockSizing = object.get("sizing").getAsInt();
        } catch (Exception e) {
            blockSizing = 16;
        }
    }

    @Override
    public GridLayout build() {
        Text translated = DescriptionComponentRegistry.parseStringToContent(text, true);
        Component textComponent = MiniMessage.miniMessage().deserialize(translated.getString(), ModResolvers.guidebookEntryResolver());
        String textJSON = GsonComponentSerializer.gson().serialize(textComponent);
        Text parsedText = TextCodecs.CODEC
                .decode(JsonOps.INSTANCE, new Gson().fromJson(textJSON, JsonElement.class))
                .getOrThrow()
                .getFirst();


        GridLayout grid = Containers.grid(Sizing.expand(), Sizing.content(), 1, 2);
        grid.alignment(HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        LabelComponent label = Components.label(parsedText).shadow(true).horizontalTextAlignment(HorizontalAlignment.LEFT);
        label.sizing(Sizing.expand(39), Sizing.content());
        if (textAlignment == Alignment.RIGHT) {
            grid.child(Components.block(block).tooltip(block.getBlock().asItem().getDefaultStack().getTooltip(Item.TooltipContext.DEFAULT, null, TooltipType.BASIC)).sizing(Sizing.fixed(blockSizing)), 0, 0);
            grid.child(label, 0, 1);
        } else {
            grid.child(label, 0, 0);
            grid.child(Components.block(block).tooltip(block.getBlock().asItem().getDefaultStack().getTooltip(Item.TooltipContext.DEFAULT, null, TooltipType.BASIC)).sizing(Sizing.fixed(blockSizing)), 0, 1);
        }
        return grid;
    }
}

