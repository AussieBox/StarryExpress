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
import org.aussiebox.starexpress.client.guidebook.variable.VariableHandler;

import java.util.ArrayList;
import java.util.List;

public class ModResolvers {
    public static TagResolver guidebookEntryResolver() {
        List<TagResolver> resolvers = new ArrayList<>();

        /// Entry Resolver
        resolvers.add(TagResolver.resolver("entry", (args, ctx) -> {
            String entryId = args.popOr("Entry tag requires a guidebook entry's ID").value();
            ClickEvent clickHandler = ClickEvent.callback(audience -> {
                NewGuidebookScreen.clickHyperlink(Identifier.of(entryId));
            }, ClickCallback.Options.builder().uses(ClickCallback.UNLIMITED_USES).build());
            HoverEvent<Component> hoverHandler = HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("guidebook.tooltip.entry_link").color(TextColor.color(0x77DCFF)));
            return Tag.styling(TextColor.color(0x77DCFF), TextDecoration.UNDERLINED, clickHandler, hoverHandler);
        }));

        ///  Variable Resolver
        resolvers.add(TagResolver.resolver("var", (args, ctx) -> {
            Identifier varId = Identifier.of(args.popOr("Variable tag requires a namespace").value(), args.popOr("Variable tag requires a path").value());
            if (!VariableHandler.variables.containsKey(varId)) throw new IllegalArgumentException("Variable " + varId + " does not exist");
            return Tag.inserting(Component.text().content(String.valueOf(VariableHandler.variables.get(varId).get())));
        }));

        return resolvers.stream().collect(TagResolver.toTagResolver());
    }
}
