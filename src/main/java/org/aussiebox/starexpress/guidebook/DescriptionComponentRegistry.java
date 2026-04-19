package org.aussiebox.starexpress.guidebook;

import org.aussiebox.starexpress.guidebook.description.SpacerComponent;
import org.aussiebox.starexpress.guidebook.description.TextComponent;
import org.aussiebox.starexpress.guidebook.description.TextWithTextureComponent;
import org.aussiebox.starexpress.guidebook.description.TextureComponent;

import java.util.LinkedHashMap;
import java.util.Map;

public class DescriptionComponentRegistry {
    private static final Map<String, Class<? extends DescriptionComponent>> registry = new LinkedHashMap<>();

    static {
        register("text", TextComponent.class);
        register("texture", TextureComponent.class);
        register("text_with_texture", TextWithTextureComponent.class);
        register("spacer", SpacerComponent.class);
    }

    public static void register(String id, Class<? extends DescriptionComponent> clazz) {
        registry.put(id, clazz);
    }

    public static Map<String, Class<? extends DescriptionComponent>> getMap() {
        return registry;
    }
}
