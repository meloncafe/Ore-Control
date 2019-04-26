package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorEmeraldOverrider_v1_13_R2 extends WorldGenDecoratorEmerald {

    @NonNull
    private final Biome biome;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureDecoratorEmptyConfiguration worldGenFeatureDecoratorEmptyConfiguration, final WorldGenerator<C> worldGenerator, final C c) {
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);
        final Optional<WorldOreConfig> config = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        int var1 = 3, var2 = 6, var3 = 28, var4 = 4;

        if (config.isPresent()) {
            if (!service.isActivated(Ore.EMERALD, config.get(), biome))
                return true;

            var1 = service.getValue(Ore.EMERALD, Setting.MINIMUM_ORES_PER_CHUNK, config.get(), biome);
            var2 = service.getValue(Ore.EMERALD, Setting.ORES_PER_CHUNK_RANGE, config.get(), biome);
            var3 = service.getValue(Ore.EMERALD, Setting.HEIGHT_RANGE, config.get(), biome);
            var4 = service.getValue(Ore.EMERALD, Setting.MINIMUM_HEIGHT, config.get(), biome);
        }

        final int var5 = var1 + random.nextInt(var2);

        for (int var6 = 0; var6 < var5; ++var6) {
            final int var7 = random.nextInt(16);
            final int var8 = random.nextInt(var3) + var4;
            final int var9 = random.nextInt(16);
            worldGenerator.generate(generatorAccess, chunkGenerator, random, blockPosition.a(var7, var8, var9), c);
        }

        return true;
    }
}