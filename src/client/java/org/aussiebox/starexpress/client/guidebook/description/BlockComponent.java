package org.aussiebox.starexpress.client.guidebook.description;

import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.client.guidebook.DescriptionComponent;
import org.aussiebox.starexpress.exception.MissingJsonFieldException;

import java.util.Optional;

public class BlockComponent extends DescriptionComponent {
    public Block block;
    public int blockSizing;

    public BlockComponent(String id, JsonObject object) {
        super(id, object);
        if (!object.has("block"))
            throw new MissingJsonFieldException("JSON Object did not contain block parameter");
        Optional<Block> optionalBlock = Registries.BLOCK.getOrEmpty(Identifier.of(object.get("block").getAsString()));
        if (optionalBlock.isPresent()) block = optionalBlock.get();
        else throw new NullPointerException("Block returned null after parsing");
        try {
            blockSizing = object.get("sizing").getAsInt();
        } catch (Exception e) {
            blockSizing = 16;
        }
    }

    @Override
    public FlowLayout build() {
        FlowLayout flow = Containers.horizontalFlow(Sizing.expand(), Sizing.content());
        flow.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);
        flow.child(Components.block(block.getDefaultState()).sizing(Sizing.fixed(blockSizing)));
        return flow;
    }
}
