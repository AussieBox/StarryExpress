package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

public class TextureComponent extends DescriptionComponent {
    public Identifier texture;
    public int width;
    public int height;
    public int regionWidth;
    public int regionHeight;

    public TextureComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("texture"))
            throw new MissingJsonFieldException("JSON Object did not contain texture parameter");
        if (!object.has("width"))
            throw new MissingJsonFieldException("JSON Object did not contain width parameter");
        if (!object.has("height"))
            throw new MissingJsonFieldException("JSON Object did not contain height parameter");
        texture = Identifier.of(object.get("texture").getAsString());
        width = object.get("width").getAsInt();
        height = object.get("height").getAsInt();
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
    public FlowLayout build() {
        FlowLayout flow = Containers.horizontalFlow(Sizing.expand(), Sizing.content());
        flow.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);
        flow.child(Components.texture(texture, 0, 0, regionWidth, regionHeight, width, height));
        return flow;
    }
}
