/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOreAction;
import de.derfrzocker.ore.control.gui.settings.BiomeGuiSettings;
import de.derfrzocker.ore.control.gui.settings.OreSettingsGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class OreSettingsGui extends PageGui<Setting> {

    private static OreSettingsGuiSettings oreSettingsGuiSettings;

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final Biome biome;
    @Nullable
    private final BiomeGroupGui.BiomeGroup biomeGroup;
    @NotNull
    private final Ore ore;
    @Nullable
    private final CopyAction copyAction;

    private boolean activated;
    private final int statusSlot;


    OreSettingsGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Biome biome, @NotNull final Ore ore) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(ore, "Ore can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.ore = ore;
        this.copyAction = null;
        this.statusSlot = oreSettingsGuiSettings.getStatusSlot();
        this.activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();
        final Setting[] settings = ore.getSettings();

        init(settings, Setting[]::new, this::getSettingItemStack, (setting, event) -> {
            new SettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biome, ore, setting).openSync(event.getWhoClicked());
        });

        addItem(oreSettingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getBackItemStack()),
                event -> new OreGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biome).openSync(event.getWhoClicked()));

        addItem(oreSettingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biome == null ? oreSettingsGuiSettings.getInfoItemStack() : oreSettingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(javaPlugin, activated ? oreSettingsGuiSettings.getDeactivateItemStack() : oreSettingsGuiSettings.getActivateItemStack()), event -> handleStatusUpdate());

        if (permissions.getValueResetPermission().hasPermission(permissible))
            addItem(oreSettingsGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getResetValueItemStack()), this::handleResetValues);

        if (permissions.getValueCopyPermission().hasPermission(permissible))
            addItem(oreSettingsGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getCopyValueItemStack()), event -> new WorldGui(oreControlValues, new CopyOreAction(oreControlValues, worldOreConfig, biome, ore)).openSync(event.getWhoClicked()));
    }

    public OreSettingsGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Biome biome, @NotNull final Ore ore, @NotNull final CopyAction copyAction) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(copyAction, "CopyAction can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.ore = ore;
        this.copyAction = copyAction;
        this.statusSlot = -1;

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Set<Setting> settingSet = new LinkedHashSet<>();

        for (final Setting setting : ore.getSettings())
            if (copyAction.shouldSet(setting))
                settingSet.add(setting);

        final Setting[] settings = settingSet.toArray(new Setting[0]);

        init(settings, Setting[]::new, this::getSettingItemStack, (setting, event) -> {
            copyAction.setSettingTarget(setting);

            copyAction.next(event.getWhoClicked(), OreSettingsGui.this);
        });

        addItem(oreSettingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biome == null ? oreSettingsGuiSettings.getInfoItemStack() : oreSettingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));
    }

    OreSettingsGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @NotNull final BiomeGroupGui.BiomeGroup biomeGroup, @NotNull final Ore ore, @NotNull final BiomeGuiSettings biomeGuiSettings) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(biomeGroup, "BiomeGroup can not be null");
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(biomeGuiSettings, "BiomeGuiSettings can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = null;
        this.biomeGroup = biomeGroup;
        this.ore = ore;
        this.copyAction = null;
        this.statusSlot = oreSettingsGuiSettings.getStatusSlot();
        this.activated = true;

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Setting[] settings = ore.getSettings();

        init(settings, Setting[]::new, this::getSettingItemStack, (setting, event) -> {
            new SettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biomeGroup, ore, setting, biomeGuiSettings).openSync(event.getWhoClicked());
        });

        addItem(oreSettingsGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));

        addItem(statusSlot, MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getDeactivateItemStack()), event -> handleBiomeGroupStatusUpdate());
        addItem(oreSettingsGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, oreSettingsGuiSettings.getBackItemStack()),
                event -> new OreGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biomeGroup, biomeGuiSettings).openSync(event.getWhoClicked()));
    }

    private static OreSettingsGuiSettings checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (oreSettingsGuiSettings == null)
            oreSettingsGuiSettings = new OreSettingsGuiSettings(javaPlugin, "data/gui/ore-settings-gui.yml", true);

        return oreSettingsGuiSettings;
    }

    private ItemStack getSettingItemStack(final @NonNull Setting setting) {
        final ItemStack itemStack;

        if (biome == null)
            itemStack = MessageUtil.replaceItemStack(getPlugin(), oreSettingsGuiSettings.getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig))));
        else
            itemStack = MessageUtil.replaceItemStack(getPlugin(), oreSettingsGuiSettings.getSettingsItemStack(setting), new MessageValue("amount", String.valueOf(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome))));

        return itemStack;
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString()),
                new MessageValue("ore", ore.toString())};
    }

    private void handleStatusUpdate() {
        activated = !activated;

        if (biome == null)
            OreControlUtil.setActivated(ore, worldOreConfig, activated);
        else
            OreControlUtil.setActivated(ore, worldOreConfig, activated, biome);

        addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? oreSettingsGuiSettings.getDeactivateItemStack() : oreSettingsGuiSettings.getActivateItemStack()));

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
    }

    private void handleBiomeGroupStatusUpdate() {
        activated = !activated;

        biomeGroup.getBiomes().forEach(biome -> OreControlUtil.setActivated(ore, worldOreConfig, activated, biome));

        addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? oreSettingsGuiSettings.getDeactivateItemStack() : oreSettingsGuiSettings.getActivateItemStack()));

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
    }

    private void handleResetValues(final @NonNull InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            new VerifyGui(getPlugin(), clickEvent -> {
                if (biome != null)
                    OreControlUtil.reset(worldOreConfig, ore, biome);
                else
                    OreControlUtil.reset(worldOreConfig, ore);

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
                addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? oreSettingsGuiSettings.getDeactivateItemStack() : oreSettingsGuiSettings.getActivateItemStack()));
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null)
            OreControlUtil.reset(worldOreConfig, ore, biome);
        else
            OreControlUtil.reset(worldOreConfig, ore);

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        activated = biome == null ? OreControlUtil.isActivated(ore, worldOreConfig) : OreControlUtil.isActivated(ore, worldOreConfig, biome);
        addItem(statusSlot, MessageUtil.replaceItemStack(getPlugin(), activated ? oreSettingsGuiSettings.getDeactivateItemStack() : oreSettingsGuiSettings.getActivateItemStack()));
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class SettingConsumer implements Consumer<InventoryClickEvent> {

        private final Setting setting;

        @Override
        public void accept(final @NonNull InventoryClickEvent event) {
            new SettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biome, ore, setting).openSync(event.getWhoClicked());
        }
    }

    private final class SettingCopyConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Setting setting;

        private SettingCopyConsumer(@NotNull final Setting setting) {
            this.setting = setting;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            copyAction.setSettingTarget(setting);

            copyAction.next(event.getWhoClicked(), OreSettingsGui.this);
        }

    }

    private final class SettingBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Setting setting;
        @NotNull
        private final BiomeGuiSettings biomeGuiSettings;

        private SettingBiomeGroupConsumer(@NotNull final Setting setting, @NotNull final BiomeGuiSettings biomeGuiSettings) {
            this.setting = setting;
            this.biomeGuiSettings = biomeGuiSettings;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            new SettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biomeGroup, ore, setting, biomeGuiSettings).openSync(event.getWhoClicked());
        }

    }

}
