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

package de.derfrzocker.ore.control.impl.dao;

import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.spigot.utils.dao.yaml.BasicYamlDao;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.util.Optional;

public class WorldOreConfigYamlDao extends BasicYamlDao<String, WorldOreConfig> implements WorldOreConfigDao {

    public WorldOreConfigYamlDao(final @NonNull File file) {
        super(file);
    }

    @Override
    public Optional<WorldOreConfig> get(final @NonNull String name) {
        return getFromStringKey(name);
    }

    @Override
    public void remove(final @NonNull WorldOreConfig config) {
        saveFromStringKey(config.getName(), null);
    }

    @Override
    public void save(@NonNull WorldOreConfig config) {
        if (!(config instanceof ConfigurationSerializable))
            config = new WorldOreConfigYamlImpl(config.getName(), config.isTemplate(), config.getOreSettings(), config.getBiomeOreSettings());

        saveFromStringKey(config.getName(), config);
    }

}
