package org.aussiebox.starexpress.guidebook.description;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.guidebook.DescriptionComponent;

public class TextureComponent extends DescriptionComponent {
    public Identifier texture;
    public int width;
    public int height;

    public TextureComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("texture"))
            throw new IllegalArgumentException("JSON Object did not contain texture parameter");
        if (!object.has("width"))
            throw new IllegalArgumentException("JSON Object did not contain width parameter");
        if (!object.has("height"))
            throw new IllegalArgumentException("JSON Object did not contain height parameter");
        texture = Identifier.of(object.get("texture").getAsString());
        width = object.get("width").getAsInt();
        height = object.get("height").getAsInt();
    }
}
