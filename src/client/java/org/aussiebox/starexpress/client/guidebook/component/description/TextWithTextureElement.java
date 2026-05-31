package org.aussiebox.starexpress.client.guidebook.component.description;

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
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.client.guidebook.ElementRegistry;
import org.aussiebox.starexpress.client.guidebook.component.DescriptionElement;
import org.aussiebox.starexpress.client.minimessage.ModResolvers;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

public class TextWithTextureElement extends DescriptionElement {
    public String text;
    public Alignment textAlignment;
    public Identifier texture;
    public int width;
    public int height;
    public int regionWidth;
    public int regionHeight;

    public TextWithTextureElement(String id, JsonObject object) {
        super(id, object);
        if (!object.has("text"))
            throw new MissingJsonFieldException("JSON Object did not contain text parameter");
        if (!object.has("texture"))
            throw new MissingJsonFieldException("JSON Object did not contain texture parameter");
        if (!object.has("width"))
            throw new MissingJsonFieldException("JSON Object did not contain width parameter");
        if (!object.has("height"))
            throw new MissingJsonFieldException("JSON Object did not contain height parameter");
        text = object.get("text").getAsString();
        texture = Identifier.of(object.get("texture").getAsString());
        width = object.get("width").getAsInt();
        height = object.get("height").getAsInt();
        try {
            textAlignment = Alignment.valueOf(object.get("text_alignment").getAsString());
        } catch (Exception e) {
            textAlignment = Alignment.LEFT;
        }
        try {
            regionWidth = object.get("region_width").getAsInt();
        } catch (Exception e) {
            regionWidth = width;
        }
        try {
            regionHeight = object.get("region_height").getAsInt();
        } catch (Exception e) {
            regionHeight = height;
        }
    }

    @Override
    public GridLayout build() {
        Text translated = ElementRegistry.parseStringToContent(text, true);
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
            grid.child(Components.texture(texture, 0, 0, regionWidth, regionHeight, width, height), 0, 0);
            grid.child(label, 0, 1);
        } else {
            grid.child(label, 0, 0);
            grid.child(Components.texture(texture, 0, 0, regionWidth, regionHeight, width, height), 0, 1);
        }
        return grid;
    }
}
