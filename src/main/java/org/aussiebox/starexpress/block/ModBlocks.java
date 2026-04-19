package org.aussiebox.starexpress.block;

import dev.doctor4t.ratatouille.util.registrar.BlockRegistrar;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.block.custom.PlushBlock;

public interface ModBlocks {

    BlockRegistrar registrar = new BlockRegistrar(StarryExpress.MOD_ID);

    Block CIRCUITWEAVER_PLUSH = registrar.createWithItem("circuitweaver_plush", new PlushBlock(AbstractBlock.Settings.copy(Blocks.BLACK_WOOL).nonOpaque()));
    Block JADE_PLUSH = registrar.createWithItem("jade_plush", new PlushBlock(AbstractBlock.Settings.copy(Blocks.LIME_WOOL).nonOpaque()));

    static void init() {
        registrar.registerEntries();
    }
}
