package org.aussiebox.starexpress.guidebook.description;

import com.google.gson.JsonObject;
import org.aussiebox.starexpress.guidebook.DescriptionComponent;

public class TextComponent extends DescriptionComponent {
    public String text;

    public TextComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("text"))
            throw new IllegalArgumentException("JSON Object did not contain text parameter");
        text = object.get("text").getAsString();
    }

    @Override
    public String getType() {
        return "text";
    }
}
