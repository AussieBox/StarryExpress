package org.aussiebox.starexpress.client.guidebook.component;

import com.google.gson.JsonObject;
import net.minecraft.util.StringIdentifiable;

public abstract class DescriptionElement extends GuidebookElement {
    public DescriptionElement(String id, JsonObject object) {
        super(id, object);
    }

    public enum Alignment implements StringIdentifiable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        Alignment(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
