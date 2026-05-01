package org.aussiebox.starexpress.client.gui.owo;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TextureTitleCollapsibleContainer extends CollapsibleContainer {
    public TextureTitleCollapsibleContainer(Sizing horizontalSizing, Sizing verticalSizing, Text title, Identifier texture, int width, int height, boolean expanded) {
        super(horizontalSizing, verticalSizing, title, expanded);
        titleLayout.child(0, Components.texture(texture, 0, 0,  width, height, width, height).margins(Insets.of(0, 0, 0, 5)).cursorStyle(CursorStyle.HAND));
        titleLayout.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
    }

    public TextureTitleCollapsibleContainer(Sizing horizontalSizing, Sizing verticalSizing, Text title, Identifier texture, int regionWidth, int regionHeight, int textureWidth, int textureHeight, boolean expanded) {
        super(horizontalSizing, verticalSizing, title, expanded);
        titleLayout.child(0, Components.texture(texture, 0, 0,  regionWidth, regionHeight, textureWidth, textureHeight).margins(Insets.of(0, 0, 0, 5)).cursorStyle(CursorStyle.HAND));
        titleLayout.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
    }
}
