package org.aussiebox.starexpress.client.guidebook;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.core.Component;
import net.minecraft.util.StringIdentifiable;

public abstract class DescriptionComponent {
    public final String id;

    public DescriptionComponent(String id, JsonObject object) {
        this.id = id;
    }

    public abstract Component build();

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
