package org.aussiebox.starexpress.client.guidebook.component;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.core.Component;

public abstract class GuidebookElement {
    public final String id;

    public GuidebookElement(String id, JsonObject object) {
        this.id = id;
    }

    public abstract Component build();
}
