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
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponentRegistry;
import org.aussiebox.starexpress.client.minimessage.ModResolvers;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

import java.util.Optional;

public class TextWithBlockComponent extends DescriptionComponent {
    public String text;
    public Block block;
    public Alignment textAlignment;
    public int blockSizing;

    public TextWithBlockComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("text"))
            throw new MissingJsonFieldException("JSON Object did not contain text parameter");
        if (!object.has("block"))
            throw new MissingJsonFieldException("JSON Object did not contain block parameter");
        text = object.get("text").getAsString();
        Optional<Block> optionalBlock = Registries.BLOCK.getOrEmpty(Identifier.of(object.get("block").getAsString()));
        if (optionalBlock.isPresent()) block = optionalBlock.get();
        else throw new NullPointerException("Block returned null after parsing");
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
        Text translated = DescriptionComponentRegistry.parseStringToContent(text);
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
            grid.child(Components.block(block.getDefaultState()).sizing(Sizing.fixed(blockSizing)), 0, 0);
            grid.child(label, 0, 1);
        } else {
            grid.child(label, 0, 0);
            grid.child(Components.block(block.getDefaultState()).sizing(Sizing.fixed(blockSizing)), 0, 1);
        }
        return grid;
    }
}

