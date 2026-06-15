package org.aussiebox.starexpress.client.gui.screen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.doctor4t.wathe.api.Role;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.client.gui.owo.TextureTitleCollapsibleContainer;
import org.aussiebox.starexpress.client.guidebook.ElementRegistry;
import org.aussiebox.starexpress.client.guidebook.GuidebookEntry;
import org.aussiebox.starexpress.client.guidebook.GuidebookEntryCollector;
import org.aussiebox.starexpress.client.guidebook.component.GuidebookElement;
import org.jetbrains.annotations.NotNull;

public class NewGuidebookScreen extends BaseOwoScreen<FlowLayout> {
    public ScrollContainer<FlowLayout> displayedEntry;
    public FlowLayout entryBrowser;
    public FlowLayout root;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }

    @Override
    protected void build(FlowLayout root) {
        displayedEntry = Containers.verticalScroll(Sizing.expand(), Sizing.expand(), Containers.verticalFlow(Sizing.expand(), Sizing.expand())).scrollbar(ScrollContainer.Scrollbar.flat(Color.WHITE)).scrollbarThiccness(1).scrollStep(12);
        entryBrowser = Containers.verticalFlow(Sizing.expand(40), Sizing.expand());

        root.child(updateEntryBrowser(root));
        root.child(Components.box(Sizing.fixed(1), Sizing.expand()).color(Color.ofArgb(0x33FFFFFF)));
        root.child(displayedEntry.id("displayed_entry"));
        root.surface(Surface.VANILLA_TRANSLUCENT);

        this.root = root;
    }

    public FlowLayout updateEntryBrowser(FlowLayout root) {
        entryBrowser.clearChildren();
        FlowLayout flow = Containers.verticalFlow(Sizing.expand(), Sizing.content());

        TextureTitleCollapsibleContainer rolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles"), StarryExpress.id("textures/gui/sprites/hud/starstruck/ability_happy.png"), 14, 17, true);
            TextureTitleCollapsibleContainer goodRolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles.good"), Identifier.of("wathe", "textures/gui/sprites/hud/mood_happy.png"), 14, 17, false);
            TextureTitleCollapsibleContainer neutralRolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles.neutral"), Identifier.of("wathe", "textures/gui/sprites/hud/mood_mid.png"), 14, 17, false);
            TextureTitleCollapsibleContainer evilRolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles.evil"), Identifier.of("wathe", "textures/gui/sprites/hud/mood_killer.png"), 14, 17, false);
        TextureTitleCollapsibleContainer modifiersCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.modifiers"), Identifier.of("wathe", "textures/gui/sprites/hud/arrow_up.png"), 10, 13, false);
        TextureTitleCollapsibleContainer itemsCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.items"), Identifier.of("wathe", "textures/item/bat.png"), 16, 16, false);
        CollapsibleContainer miscCategory = Containers.collapsible(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.misc"), false);

        rolesCategory.onToggled().subscribe((nowExpanded -> this.uiAdapter.inflateAndMount()));
        goodRolesCategory.onToggled().subscribe((nowExpanded -> this.uiAdapter.inflateAndMount()));
        neutralRolesCategory.onToggled().subscribe((nowExpanded -> this.uiAdapter.inflateAndMount()));
        evilRolesCategory.onToggled().subscribe((nowExpanded -> this.uiAdapter.inflateAndMount()));
        modifiersCategory.onToggled().subscribe((nowExpanded -> this.uiAdapter.inflateAndMount()));
        itemsCategory.onToggled().subscribe((nowExpanded -> this.uiAdapter.inflateAndMount()));
        miscCategory.onToggled().subscribe((nowExpanded -> this.uiAdapter.inflateAndMount()));

        for (GuidebookEntry entry : GuidebookEntryCollector.guidebookEntries) {
            Text translated = ElementRegistry.parseStringToContent(entry.title, false);
            Component textComponent = MiniMessage.miniMessage().deserialize(translated.getString()); // No <guidebook> tag for this one
            String textJSON = GsonComponentSerializer.gson().serialize(textComponent);
            MutableText parsedText = TextCodecs.CODEC
                    .decode(JsonOps.INSTANCE, new Gson().fromJson(textJSON, JsonElement.class))
                    .getOrThrow()
                    .getFirst()
                    .copy();

            ButtonComponent button = Components.button(
                    parsedText.withColor(0xFFFFFF).append("                                                                                         "),
                    buttonComponent -> {
                        setDisplayedEntry(entry, root);
                    }
            ).renderer(ButtonComponent.Renderer.texture(StarryExpress.id("textures/empty.png"), 0, 0, 1, 1));

            switch (entry.parentType) {
                case ROLE -> {
                    if (entry.parentRole.isEmpty()) continue;
                    Role role = entry.parentRole.get();
                    if (role.isInnocent() && !role.canUseKiller()) goodRolesCategory.child(button.id(role.identifier().toString()));
                    if (!role.isInnocent() && !role.canUseKiller()) neutralRolesCategory.child(button.id(role.identifier().toString()));
                    if (!role.isInnocent() && role.canUseKiller()) evilRolesCategory.child(button.id(role.identifier().toString()));
                }
                case MODIFIER -> {
                    if (entry.parentModifier.isEmpty()) continue;
                    Modifier modifier = entry.parentModifier.get();
                    modifiersCategory.child(button.id(modifier.identifier().toString()));
                }
                case ITEM -> {
                    if (entry.parentItem.isEmpty()) continue;
                    Item item = entry.parentItem.get();
                    itemsCategory.child(button.id(item.toString()));
                }
                case MISC -> {
                    if (entry.parentMisc.isEmpty()) continue;
                    String misc = entry.parentMisc.get();
                    miscCategory.child(button.id(misc));
                }
            }
        }

        rolesCategory.child(goodRolesCategory);
        rolesCategory.child(neutralRolesCategory);
        rolesCategory.child(evilRolesCategory);
        flow.child(rolesCategory);
        flow.child(modifiersCategory);
        flow.child(itemsCategory);
        flow.child(miscCategory);
        flow.padding(Insets.bottom(5));

        TextBoxComponent search = Components.textBox(Sizing.expand(90));
        search.margins(Insets.top(5));
        search.setMaxLength(1000);
        search.setPlaceholder(Text.translatable("tip.starexpress.search").withColor(0xAAAAAA));

        ScrollContainer<FlowLayout> categories = Containers.verticalScroll(Sizing.expand(), Sizing.expand(85), flow)
                .scrollbar(ScrollContainer.Scrollbar.flat(Color.WHITE))
                .scrollbarThiccness(1)
                .scrollStep(12);

        entryBrowser.child(search.id("search"));
        entryBrowser.child(categories.id("entry_categories"));
        entryBrowser.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

        return entryBrowser;
    }

    public void setDisplayedEntry(GuidebookEntry entry, FlowLayout root) {
        buildDescription(displayedEntry, entry);
        entry.parentRole.ifPresent(role -> updateRoleFlags(displayedEntry, role));
        entry.parentModifier.ifPresent(modifier -> updateModifierFlags(displayedEntry, modifier));
    }

    public void updateRoleFlags(ScrollContainer<FlowLayout> container, Role parent) {
        int index = container.child().children().indexOf(container.child().childById(FlowLayout.class, "flags"));
        container.child().removeChild(container.childById(FlowLayout.class, "flags"));
        FlowLayout flags = Containers.horizontalFlow(Sizing.expand(), Sizing.content());
        flags.padding(Insets.of(10, 10, 0, 0));

        LabelComponent spacer = Components.label(Text.literal(" • ").withColor(0xAAAAAA));

        boolean disabled = HarpyModLoaderConfig.HANDLER.instance().disabled.contains(parent.identifier().toString());
        boolean namespaceExists = Language.getInstance().hasTranslation("guidebook.namespace." + parent.identifier().getNamespace());

        if (!disabled && !namespaceExists) return;

        if (disabled) flags.child(Components.label(Text.translatable("guidebook.disabled").withColor(0xFF5555)).shadow(true));
        if (namespaceExists) {
            if (!flags.children().isEmpty()) flags.child(spacer);
            flags.child(Components.label(Text.translatable("guidebook.role.credits").append(Text.translatable("guidebook.namespace." + parent.identifier().getNamespace()).setStyle(Style.EMPTY.withItalic(true))).withColor(0xAAAAAA)).shadow(true));
        }

        if (index == -1) index = container.child().children().indexOf(container.child().childById(LabelComponent.class, "title_spacing"));
        container.child().removeChild(container.child().childById(LabelComponent.class, "title_spacing"));
        container.child().child(index, flags.id("flags"));
    }

    public void updateModifierFlags(ScrollContainer<FlowLayout> container, Modifier parent) {
        int index = container.child().children().indexOf(container.child().childById(FlowLayout.class, "flags"));
        container.child().removeChild(container.childById(FlowLayout.class, "flags"));
        FlowLayout flags = Containers.horizontalFlow(Sizing.expand(), Sizing.content());
        flags.padding(Insets.of(10, 10, 0, 0));

        LabelComponent spacer = Components.label(Text.literal(" • ").withColor(0xAAAAAA));

        boolean disabled = HarpyModLoaderConfig.HANDLER.instance().disabledModifiers.contains(parent.identifier().toString());
        boolean namespaceExists = Language.getInstance().hasTranslation("guidebook.namespace." + parent.identifier().getNamespace());

        if (!disabled && !namespaceExists) return;

        if (disabled) flags.child(Components.label(Text.translatable("guidebook.disabled").withColor(0xFF5555)).shadow(true));
        if (namespaceExists) {
            if (!flags.children().isEmpty()) flags.child(spacer);
            flags.child(Components.label(Text.translatable("guidebook.role.credits").append(Text.translatable("guidebook.namespace." + parent.identifier().getNamespace()).setStyle(Style.EMPTY.withItalic(true))).withColor(0xAAAAAA)).shadow(true));
        }

        if (index == -1) index = container.child().children().indexOf(container.child().childById(LabelComponent.class, "title_spacing"));
        container.child().removeChild(container.child().childById(LabelComponent.class, "title_spacing"));
        container.child().child(index, flags.id("flags"));
    }

    public void buildDescription(ScrollContainer<FlowLayout> modify, GuidebookEntry entry) {
        modify.child().clearChildren();
        modify.child().alignment(HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        modify.child().margins(Insets.of(5, 5, 8, 8));

        Text translated = ElementRegistry.parseStringToContent(entry.title, false);
        Component textComponent = MiniMessage.miniMessage().deserialize(translated.getString());
        String textJSON = GsonComponentSerializer.gson().serialize(textComponent);
        MutableText parsedText = TextCodecs.CODEC
                .decode(JsonOps.INSTANCE, new Gson().fromJson(textJSON, JsonElement.class))
                .getOrThrow()
                .getFirst()
                .copy();

        parsedText.setStyle(parsedText.getStyle().withFont(StarryExpress.id("guidebook_heading")));
        entry.parentRole.ifPresent(role -> parsedText.withColor(role.color()));
        modify.child().child(Components.label(parsedText).shadow(true).lineHeight(16).id("title"));

        if (entry.subtitle.isPresent()) {
            translated = ElementRegistry.parseStringToContent(entry.subtitle.get(), false);
            textComponent = MiniMessage.miniMessage().deserialize(translated.getString());
            textJSON = GsonComponentSerializer.gson().serialize(textComponent);
            MutableText parsedText2 = TextCodecs.CODEC
                    .decode(JsonOps.INSTANCE, new Gson().fromJson(textJSON, JsonElement.class))
                    .getOrThrow()
                    .getFirst()
                    .copy();

            entry.parentRole.ifPresent(role -> parsedText2.withColor(role.color()));
            modify.child().child(Components.label(parsedText2).shadow(true).id("subtitle"));
        }

        modify.child().child(Components.label(Text.of(" ")).id("title_spacing"));

        for (GuidebookElement component : entry.description)
            if (component.build() != null) modify.child().child(component.build().id(component.id));

        modify.alignment(HorizontalAlignment.LEFT, VerticalAlignment.TOP);
    }

    public void clickHyperlink(Identifier targetEntry) {
        for (GuidebookEntry entry : GuidebookEntryCollector.guidebookEntries) {
            if (entry.getId().equals(targetEntry)) {
                setDisplayedEntry(entry, root);
            }
        }
    }
}
