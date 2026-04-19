package org.aussiebox.starexpress.guidebook.description;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.guidebook.DescriptionComponent;

public class TextWithTextureComponent extends DescriptionComponent {
    public String text;
    public Identifier texture;
    public int width;
    public int height;
    public Alignment texture_alignment;

    public TextWithTextureComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("text"))
            throw new IllegalArgumentException("JSON Object did not contain text parameter");
        if (!object.has("texture"))
            throw new IllegalArgumentException("JSON Object did not contain texture parameter");
        if (!object.has("width"))
            throw new IllegalArgumentException("JSON Object did not contain width parameter");
        if (!object.has("height"))
            throw new IllegalArgumentException("JSON Object did not contain height parameter");
        text = object.get("text").getAsString();
        texture = Identifier.of(object.get("texture").getAsString());
        width = object.get("width").getAsInt();
        height = object.get("height").getAsInt();
        try {
            texture_alignment = Alignment.valueOf(object.get("texture_alignment").getAsString());
        } catch (Exception e) {
            texture_alignment = Alignment.RIGHT;
        }
    }

    @Override
    public String getType() {
        return "text_with_texture";
    }
}
