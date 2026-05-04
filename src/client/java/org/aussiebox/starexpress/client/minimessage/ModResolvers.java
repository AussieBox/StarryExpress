package org.aussiebox.starexpress.client.minimessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.client.gui.screen.NewGuidebookScreen;

public class ModResolvers {
    public static TagResolver guidebookEntryResolver() {
        return TagResolver.resolver("entry", (args, ctx) -> {
            String entryId = args.popOr("Entry tag requires a guidebook entry's ID").value();
            ClickEvent clickHandler = ClickEvent.callback(audience -> {
                NewGuidebookScreen.clickHyperlink(Identifier.of(entryId));
            }, ClickCallback.Options.builder().uses(ClickCallback.UNLIMITED_USES).build());
            HoverEvent<Component> hoverHandler = HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("guidebook.tooltip.entry_link").color(TextColor.color(0x77DCFF)));
            return Tag.styling(TextColor.color(0x77DCFF), TextDecoration.UNDERLINED, clickHandler, hoverHandler);
        });
    }
}
