package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.gui.BiomeGui;
import de.derfrzocker.ore.control.gui.OreGui;
import de.derfrzocker.ore.control.gui.OreSettingsGui;
import de.derfrzocker.ore.control.gui.WorldConfigGui;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
@Setter
public class CopySettingAction implements CopyAction {

    private final boolean filterWorldOreConfig = false;

    private final WorldOreConfig worldOreConfigSource;

    private final Ore oreSource;

    private final Biome biomeSource;

    private final Setting settingSource;

    private Biome biomeTarget;

    private Ore oreTarget;

    private boolean chooseBiome = false;

    private WorldOreConfig worldOreConfigTarget;

    private Setting settingTarget;

    private int status = 0;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public void next(final @NonNull HumanEntity humanEntity, final @NonNull InventoryGui inventoryGui) {
        if (status == 0) {
            new WorldConfigGui(worldOreConfigTarget, humanEntity, this, serviceSupplier).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 1) {
            if (chooseBiome)
                new BiomeGui(worldOreConfigTarget, this, serviceSupplier).openSync(humanEntity);
            else
                new OreGui(worldOreConfigTarget, biomeTarget, this, serviceSupplier).openSync(humanEntity);

            status++;
            return;
        }

        if (status == 2 && chooseBiome) {
            new OreGui(worldOreConfigTarget, biomeTarget, this, serviceSupplier).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 2) {
            new OreSettingsGui(worldOreConfigTarget, oreTarget, biomeTarget, this, serviceSupplier).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 3 && chooseBiome) {
            new OreSettingsGui(worldOreConfigTarget, oreTarget, biomeTarget, this, serviceSupplier).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 3) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, settingSource, oreTarget, settingTarget);
                    serviceSupplier.get().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, settingSource, oreTarget, settingTarget);
                    serviceSupplier.get().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });

            status++;
            return;
        }

        if (status == 4) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, settingSource, oreTarget, biomeTarget, settingTarget);
                    serviceSupplier.get().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, settingSource, oreTarget, biomeTarget, settingTarget);
                    serviceSupplier.get().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });

            status++;
        }

    }

    @Override
    public boolean shouldSet(final @NonNull Biome biome) {
        return true;
    }

    @Override
    public boolean shouldSet(final @NonNull Ore ore) {
        return true;
    }

    @Override
    public boolean shouldSet(final @NonNull Ore ore, final @NonNull Biome biome) {
        return true;
    }

    @Override
    public boolean shouldSet(final @NonNull Setting setting) {
        if (biomeSource != biomeTarget)
            return true;

        if (oreSource != oreTarget)
            return true;

        if (worldOreConfigSource != worldOreConfigTarget || !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName()))
            return true;

        return settingSource != setting;
    }

    private void openVerifyIfNeeded(final @NonNull HumanEntity humanEntity, final @NonNull InventoryGui inventoryGui, final @NonNull Consumer<InventoryClickEvent> acceptAction) {
        if (OreControl.getInstance().getConfigValues().verifyCopyAction()) {
            new VerifyGui(OreControl.getInstance(), acceptAction, clickEvent1 -> inventoryGui.closeSync(humanEntity)).openSync(humanEntity);
            return;
        }

        acceptAction.accept(null);
    }

}
