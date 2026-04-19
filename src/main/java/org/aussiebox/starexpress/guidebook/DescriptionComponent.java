package org.aussiebox.starexpress.guidebook;

import com.google.gson.JsonObject;
import net.minecraft.util.StringIdentifiable;

public abstract class DescriptionComponent {
    public final String id;

    public DescriptionComponent(String id, JsonObject object) {
        this.id = id;
    }

    public String getType() {
        return null;
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
