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

package de.derfrzocker.ore.control.gui.config;

import de.derfrzocker.ore.control.gui.settings.LanguageGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.InventoryUtil;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public class LanguageGui extends BasicGui {

    private static LanguageGuiSettings languageGuiSettings;

    @NotNull
    private final OreControlValues oreControlValues;

    LanguageGui(@NotNull final OreControlValues oreControlValues) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        this.oreControlValues = oreControlValues;

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Language[] languages = Language.values();

        addDecorations();

        for (int i = 0; i < languages.length; i++)
            addItem(InventoryUtil.calculateSlot(i, languageGuiSettings.getLanguageGap()), MessageUtil.replaceItemStack(javaPlugin, languageGuiSettings.getLanguageItemStack(languages[i])), new LanguageConsumer(languages[i]));

        addItem(languageGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, languageGuiSettings.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().getLanguage().getNames()[0]),
                new MessageValue("value", oreControlValues.getConfigValues().DEFAULT.defaultLanguage().getNames()[0])
        ));
    }

    private static LanguageGuiSettings checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (languageGuiSettings == null)
            languageGuiSettings = new LanguageGuiSettings(javaPlugin, "data/gui/language-gui.yml", true);

        return languageGuiSettings;
    }

    private final class LanguageConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Language language;

        private LanguageConsumer(@NotNull final Language language) {
            this.language = language;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            oreControlValues.getConfigValues().SET.setLanguage(language);
            new ConfigGui(oreControlValues).openSync(event.getWhoClicked());
        }

    }

}
