package org.aussiebox.starexpress.client.gui.screen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.doctor4t.wathe.api.Role;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.client.gui.owo.CollapsibleTextBoxComponent;
import org.aussiebox.starexpress.client.gui.owo.TextureTitleCollapsibleContainer;
import org.aussiebox.starexpress.client.guidebook.ElementRegistry;
import org.aussiebox.starexpress.client.guidebook.GuidebookEntry;
import org.aussiebox.starexpress.client.guidebook.GuidebookEntryCollector;
import org.aussiebox.starexpress.client.guidebook.component.GuidebookElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Note to Harpy modpack makers: please dont remake my entire guidebook system bro this took me so fucking long :/
// (friendly reminder that you need permission to redistribute my code)
public class NewGuidebookScreen extends BaseOwoScreen<FlowLayout> {
    public Identifier displayedEntryId;
    public ScrollContainer<FlowLayout> displayedEntry;
    public FlowLayout entryBrowser;
    public FlowLayout root;

    Object2ObjectOpenHashMap<String, ButtonComponent> buttons = new Object2ObjectOpenHashMap<>();
    List<String> lastButtons = new ArrayList<>();

    public String search = "";
    public boolean showDisabled = false;
    public boolean searchByNamespace = false;

    public TextureTitleCollapsibleContainer rolesCategory;
        public TextureTitleCollapsibleContainer goodRolesCategory;
        public TextureTitleCollapsibleContainer neutralRolesCategory;
        public TextureTitleCollapsibleContainer evilRolesCategory;
    public TextureTitleCollapsibleContainer modifiersCategory;
    public TextureTitleCollapsibleContainer itemsCategory;
    public CollapsibleContainer miscCategory;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }

    @Override
    protected void build(FlowLayout root) {
        displayedEntry = Containers.verticalScroll(Sizing.expand(60), Sizing.expand(), Containers.verticalFlow(Sizing.expand(), Sizing.expand())).scrollbar(ScrollContainer.Scrollbar.flat(Color.WHITE)).scrollbarThiccness(1).scrollStep(12);
        entryBrowser = Containers.verticalFlow(Sizing.expand(40), Sizing.expand());

        root.child(updateEntryBrowser());
        root.child(Components.box(Sizing.fixed(1), Sizing.expand()).color(Color.ofArgb(0x33FFFFFF)));
        root.child(displayedEntry.id("displayed_entry"));
        root.surface(Surface.VANILLA_TRANSLUCENT);

        updateSearch();

        this.root = root;
    }

    public FlowLayout updateEntryBrowser() {
        entryBrowser.clearChildren();
        FlowLayout flow = Containers.verticalFlow(Sizing.expand(), Sizing.content());

        rolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles"), StarryExpress.id("textures/gui/sprites/hud/starstruck/ability_happy.png"), 14, 17, true);
        goodRolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles.good"), Identifier.of("wathe", "textures/gui/sprites/hud/mood_happy.png"), 14, 17, false);
        neutralRolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles.neutral"), Identifier.of("wathe", "textures/gui/sprites/hud/mood_mid.png"), 14, 17, false);
        evilRolesCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.roles.evil"), Identifier.of("wathe", "textures/gui/sprites/hud/mood_killer.png"), 14, 17, false);
        modifiersCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.modifiers"), Identifier.of("wathe", "textures/gui/sprites/hud/arrow_up.png"), 10, 13, false);
        itemsCategory = new TextureTitleCollapsibleContainer(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.items"), Identifier.of("wathe", "textures/item/bat.png"), 16, 16, false);
        miscCategory = Containers.collapsible(Sizing.expand(), Sizing.content(), Text.translatable("guidebook.category.misc"), false);

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
                        setDisplayedEntry(entry);
                    }
            ).renderer(ButtonComponent.Renderer.texture(StarryExpress.id("textures/empty.png"), 0, 0, 1, 1));

            switch (entry.parentType) {
                case ROLE -> {
                    if (entry.parentRole.isEmpty()) continue;
                    Role role = entry.parentRole.get();
                    if (role.isInnocent() && !role.canUseKiller()) goodRolesCategory.child(button.id("role.good." + role.identifier().toString()));
                    if (!role.isInnocent() && !role.canUseKiller()) neutralRolesCategory.child(button.id("role.neutral." + role.identifier().toString()));
                    if (!role.isInnocent() && role.canUseKiller()) evilRolesCategory.child(button.id("role.evil." + role.identifier().toString()));
                }
                case MODIFIER -> {
                    if (entry.parentModifier.isEmpty()) continue;
                    Modifier modifier = entry.parentModifier.get();
                    modifiersCategory.child(button.id("modifier." + modifier.identifier().toString()));
                }
                case ITEM -> {
                    if (entry.parentItem.isEmpty()) continue;
                    Item item = entry.parentItem.get();
                    itemsCategory.child(button.id("item." + item));
                }
                case MISC -> {
                    if (entry.parentMisc.isEmpty()) continue;
                    String misc = entry.parentMisc.get();
                    miscCategory.child(button.id("misc." + misc));
                }
            }

            buttons.put(button.id(), button);
            if (!lastButtons.contains(button.id())) lastButtons.add(button.id());
        }

        rolesCategory.child(goodRolesCategory.id("roles.good"));
        rolesCategory.child(neutralRolesCategory.id("roles.neutral"));
        rolesCategory.child(evilRolesCategory.id("roles.evil"));
        flow.child(rolesCategory.id("roles"));
        flow.child(modifiersCategory.id("modifiers"));
        flow.child(itemsCategory.id("items"));
        flow.child(miscCategory.id("misc"));
        flow.padding(Insets.bottom(5));

        TextBoxComponent searchBox = Components.textBox(Sizing.expand());
        searchBox.margins(Insets.top(5));
        searchBox.setMaxLength(1000);
        searchBox.setPlaceholder(Text.translatable("tip.starexpress.search").withColor(0xAAAAAA));
        searchBox.onChanged().subscribe((value -> {
            this.search = value;
            updateSearch();
        }));

        CollapsibleTextBoxComponent search = new CollapsibleTextBoxComponent(Sizing.expand(90), Sizing.content(), searchBox, false);

        DropdownComponent dropdown = Components.dropdown(Sizing.expand()).text(Text.translatable("search.starexpress.options"));
        dropdown.checkbox(Text.translatable("search.starexpress.show_disabled"), showDisabled, (checked -> {
            this.showDisabled = checked;
            updateSearch();
        }));
        dropdown.checkbox(Text.translatable("search.starexpress.namespace"), searchByNamespace, (checked -> {
            this.searchByNamespace = checked;
            updateSearch();
        }));

        search.child(dropdown);

        ScrollContainer<FlowLayout> categories = new ScrollContainer<>(ScrollContainer.ScrollDirection.VERTICAL, Sizing.expand(), Sizing.expand(85), flow) {
            @Override
            public boolean isInBoundingBox(double x, double y) {
                if (search.isInBoundingBox(x, y)) return false;
                return super.isInBoundingBox(x, y);
            }
        }.scrollbar(ScrollContainer.Scrollbar.flat(Color.WHITE)).scrollbarThiccness(1).scrollStep(12);

        entryBrowser.child(search.id("search").zIndex(1000));
        entryBrowser.child(categories.id("entry_categories"));
        entryBrowser.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

        return entryBrowser;
    }

    public void setDisplayedEntry(GuidebookEntry entry) {
        displayedEntryId = entry.getId();
        buildDescription(displayedEntry, entry);
        entry.parentRole.ifPresent(role -> updateRoleFlags(displayedEntry, role));
        entry.parentModifier.ifPresent(modifier -> updateModifierFlags(displayedEntry, modifier));
    }

    public void updateSearch() {
        List<String> newButtons = new ArrayList<>();

        for (String id : buttons.keySet()) {
            ButtonComponent button = buttons.get(id);
            String label = button.getMessage().getString();

            List<GuidebookEntry> entries = GuidebookEntryCollector.guidebookEntries.stream()
                    .filter(entry -> entry.getId().toString().equals(trimButtonId(id)))
                    .toList();
            if (entries.isEmpty()) continue;
            GuidebookEntry entry = entries.getFirst();

            if (!showDisabled) {
                if (entry.parentRole.isPresent() && HarpyModLoaderConfig.HANDLER.instance().disabled.contains(entry.parentRole.get().identifier().toString())) continue;
                if (entry.parentModifier.isPresent() && HarpyModLoaderConfig.HANDLER.instance().disabledModifiers.contains(entry.parentModifier.get().identifier().toString())) continue;
            }

            if (searchByNamespace) {
                if (Identifier.of(trimButtonId(id)).getNamespace().toLowerCase().contains(search.toLowerCase())) newButtons.add(id);
            } else if (label != null && label.toLowerCase().contains(search.toLowerCase())) newButtons.add(id);
        }

        for (String id : buttons.keySet()) {
            ButtonComponent button = buttons.get(id);

            if (newButtons.contains(id) && lastButtons.contains(id)) continue;
            if (newButtons.contains(id) && !lastButtons.contains(id)) {
                addButton(id, button);
            }
            if (!newButtons.contains(id) && lastButtons.contains(id)) {
                removeButton(id, button);
            }
        }

        lastButtons = newButtons;
    }

    public String trimButtonId(String id) {
        return id
                .replace("role.good.", "")
                .replace("role.neutral.", "")
                .replace("role.evil.", "")
                .replace("modifier.", "")
                .replace("item.", "")
                .replace("misc.", "");
    }

    public void addButton(String id, ButtonComponent button) {
        switch (id) {
            case String s when s.startsWith("role.good.") -> goodRolesCategory.child(button);
            case String s when s.startsWith("role.neutral.") -> neutralRolesCategory.child(button);
            case String s when s.startsWith("role.evil.") -> evilRolesCategory.child(button);
            case String s when s.startsWith("modifier.") -> modifiersCategory.child(button);
            case String s when s.startsWith("item.") -> itemsCategory.child(button);
            case String s when s.startsWith("misc.") -> miscCategory.child(button);
            case null, default -> {}
        }
    }

    public void removeButton(String id, ButtonComponent button) {
        switch (id) {
            case String s when s.startsWith("role.good.") -> goodRolesCategory.removeChild(button);
            case String s when s.startsWith("role.neutral.") -> neutralRolesCategory.removeChild(button);
            case String s when s.startsWith("role.evil.") -> evilRolesCategory.removeChild(button);
            case String s when s.startsWith("modifier.") -> modifiersCategory.removeChild(button);
            case String s when s.startsWith("item.") -> itemsCategory.removeChild(button);
            case String s when s.startsWith("misc.") -> miscCategory.removeChild(button);
            case null, default -> {}
        }
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
                setDisplayedEntry(entry);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Object2ObjectOpenHashMap<String, ButtonComponent> newButtons = new Object2ObjectOpenHashMap<>();

        for (Map.Entry<String, ButtonComponent> buttonEntry : buttons.entrySet()) {
            ButtonComponent button = buttonEntry.getValue();
            Identifier id = Identifier.of(trimButtonId(buttonEntry.getKey()));

            List<GuidebookEntry> entries = GuidebookEntryCollector.guidebookEntries.stream()
                    .filter(entry -> entry.getId().equals(id))
                    .toList();
            if (entries.isEmpty()) continue;
            GuidebookEntry entry = entries.getFirst();

            if (entry.getId() == displayedEntryId) {
                if (entry.parentRole.isPresent()) {
                    Text text = Text.literal("» ").append(Text.translatable(entry.title)).withColor(entry.parentRole.get().color());
                    if (button.getMessage() != text) {
                        button.setMessage(text);
                        newButtons.put(buttonEntry.getKey(), button);
                    }
                } else if (entry.parentModifier.isPresent()) {
                    Text text = Text.literal("» ").append(Text.translatable(entry.title)).withColor(entry.parentModifier.get().color());
                    if (button.getMessage() != text) {
                        button.setMessage(text);
                        newButtons.put(buttonEntry.getKey(), button);
                    }
                } else {
                    Text text = Text.literal("» ").append(Text.translatable(entry.title));
                    if (button.getMessage() != text) {
                        button.setMessage(text);
                        newButtons.put(buttonEntry.getKey(), button);
                    }
                }
            } else {
                if (entry.parentRole.isPresent() && HarpyModLoaderConfig.HANDLER.instance().disabled.contains(entry.parentRole.get().identifier().toString())) {
                    Text text = Text.translatable(entry.title).withColor(Colors.LIGHT_GRAY);
                    if (button.getMessage() != text) {
                        button.setMessage(text);
                        newButtons.put(buttonEntry.getKey(), button);
                    }
                } else if (entry.parentModifier.isPresent() && HarpyModLoaderConfig.HANDLER.instance().disabledModifiers.contains(entry.parentModifier.get().identifier().toString())) {
                    Text text = Text.translatable(entry.title).withColor(Colors.LIGHT_GRAY);
                    if (button.getMessage() != text) {
                        button.setMessage(text);
                        newButtons.put(buttonEntry.getKey(), button);
                    }
                } else {
                    Text text = Text.translatable(entry.title);
                    if (button.getMessage() != text) {
                        button.setMessage(text);
                        newButtons.put(buttonEntry.getKey(), button);
                    }
                }
            }
        }

        buttons.putAll(newButtons);

        super.render(context, mouseX, mouseY, delta);
    }
}
