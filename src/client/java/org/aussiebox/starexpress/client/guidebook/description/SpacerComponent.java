package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.BoxComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;

public class SpacerComponent extends DescriptionComponent {
    public SpacerComponent(String id, JsonObject object) {
        super(id, object);
    }

    @Override
    public FlowLayout build() {
        BoxComponent box = Components.box(Sizing.expand(), Sizing.fixed(1)).color(Color.ofArgb(0x33FFFFFF));

        FlowLayout flow = Containers.verticalFlow(Sizing.expand(), Sizing.content()).child(box);
        flow.padding(Insets.of(10, 10, 0, 0));
        flow.margins(Insets.of(0, 0, 0, 8));
        flow.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
        return flow;
    }
}
