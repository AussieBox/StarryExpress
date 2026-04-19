package org.aussiebox.starexpress.block.entity;

import dev.doctor4t.ratatouille.util.registrar.BlockEntityTypeRegistrar;
import net.minecraft.block.entity.BlockEntityType;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.custom.PlushBlockEntity;

public interface ModBlockEntities {

    BlockEntityTypeRegistrar registrar = new BlockEntityTypeRegistrar(StarryExpress.MOD_ID);

    BlockEntityType<PlushBlockEntity> PLUSH = registrar.create("plush", BlockEntityType.Builder.create(PlushBlockEntity::new,
            ModBlocks.CIRCUITWEAVER_PLUSH,
            ModBlocks.JADE_PLUSH
    ));

    static void init() {
        registrar.registerEntries();
    }

}
