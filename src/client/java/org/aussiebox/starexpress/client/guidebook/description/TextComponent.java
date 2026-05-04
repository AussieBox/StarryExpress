package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponentRegistry;
import org.aussiebox.starexpress.client.minimessage.ModResolvers;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

public class TextComponent extends DescriptionComponent {
    public String text;

    public TextComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("text"))
            throw new MissingJsonFieldException("JSON Object did not contain text parameter");
        text = object.get("text").getAsString();
    }

    @Override
    public FlowLayout build() {
        Text translated = DescriptionComponentRegistry.parseStringToContent(text, true);
        Component textComponent = MiniMessage.miniMessage().deserialize(translated.getString(), ModResolvers.guidebookEntryResolver());
        String textJSON = GsonComponentSerializer.gson().serialize(textComponent);
        Text parsedText = TextCodecs.CODEC
                .decode(JsonOps.INSTANCE, new Gson().fromJson(textJSON, JsonElement.class))
                .getOrThrow()
                .getFirst();

        FlowLayout flow = Containers.horizontalFlow(Sizing.expand(), Sizing.content());
        flow.alignment(HorizontalAlignment.LEFT, VerticalAlignment.TOP);

        LabelComponent label = Components.label(parsedText).shadow(true).horizontalTextAlignment(HorizontalAlignment.LEFT);
        label.sizing(Sizing.expand(89), Sizing.content());

        flow.child(label);
        return flow;
    }
}
