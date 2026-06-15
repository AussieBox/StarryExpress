package org.aussiebox.starexpress.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.client.StarryExpressClient;
import org.jetbrains.annotations.NotNull;

public class GuidebookButtonWidget extends ButtonWidget {
    public GuidebookButtonWidget(int x, int y) {
        super(x, y, 16, 16, Text.empty(), button -> MinecraftClient.getInstance().setScreen(StarryExpressClient.SCREEN_INSTANCE), DEFAULT_NARRATION_SUPPLIER);
    }

    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderWidget(context, mouseX, mouseY, delta);
            context.drawGuiTexture(StarryExpress.id("gui/guidebook_slot"), this.getX() - 7, this.getY() - 7, 30, 30);
            context.drawItem(Items.KNOWLEDGE_BOOK.getDefaultStack(), this.getX(), this.getY());
            if (this.isHovered()) {
                this.drawShopSlotHighlight(context, this.getX(), this.getY(), 0);
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("guidebook.tooltip.open"), MinecraftClient.getInstance().textRenderer.getWidth(Text.translatable("guidebook.tooltip.open")) / 2 - 10, this.getY() + 16);
            }
    }

    private void drawShopSlotHighlight(DrawContext context, int x, int y, int z) {
        int color = 0x495CFA86;
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 16, color, color, z);
    }
}
