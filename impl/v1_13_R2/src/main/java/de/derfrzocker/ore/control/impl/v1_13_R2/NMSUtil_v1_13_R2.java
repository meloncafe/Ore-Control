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

package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSUtil;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;

import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class NMSUtil_v1_13_R2 implements NMSUtil {

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public void replaceNMS() {
        new NMSReplacer1_13_R2(serviceSupplier).replaceNMS();
    }

    @Override
    public Biome getBiome(final @NonNull World world, final @NonNull ChunkCoordIntPair chunkCoordIntPair) {
        final BiomeBase biomeBase = ((CraftWorld) world).getHandle().getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(new BlockPosition(chunkCoordIntPair.getX() << 4, 0, chunkCoordIntPair.getZ() << 4), null);

        return Biome.valueOf(IRegistry.BIOME.getKey(biomeBase).getKey().toUpperCase());
    }

    @Override
    public Object createFeatureConfiguration(final @NonNull Object defaultFeatureConfiguration, final int veinsSize) {
        return new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, ((WorldGenFeatureOreConfiguration) defaultFeatureConfiguration).d, veinsSize);
    }

    @Override
    public Object createCountConfiguration(int veinsPerChunk, int minimumHeight, int heightSubtractValue, int heightRange) {
        return new WorldGenFeatureChanceDecoratorCountConfiguration(veinsPerChunk, minimumHeight, heightSubtractValue, heightRange);
    }

    @Override
    public Object createHeightAverageConfiguration(int veinsPerChunk, int heightCenter, int heightRange) {
        return new WorldGenDecoratorHeightAverageConfiguration(veinsPerChunk, heightCenter, heightRange);
    }

    @Override
    public Ore getOre(final @NonNull Object object) {
        if (object == Blocks.DIAMOND_ORE)
            return Ore.DIAMOND;
        if (object == Blocks.COAL_ORE)
            return Ore.COAL;
        if (object == Blocks.IRON_ORE)
            return Ore.IRON;
        if (object == Blocks.REDSTONE_ORE)
            return Ore.REDSTONE;
        if (object == Blocks.GOLD_ORE)
            return Ore.GOLD;
        if (object == Blocks.DIRT)
            return Ore.DIRT;
        if (object == Blocks.GRAVEL)
            return Ore.GRAVEL;
        if (object == Blocks.GRANITE)
            return Ore.GRANITE;
        if (object == Blocks.DIORITE)
            return Ore.DIORITE;
        if (object == Blocks.ANDESITE)
            return Ore.ANDESITE;

        return null;
    }

}
