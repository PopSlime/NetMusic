package com.github.tartaricacid.netmusic.init;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.block.BlockAmplifiedMusicPlayer;
import com.github.tartaricacid.netmusic.block.BlockCDBurner;
import com.github.tartaricacid.netmusic.block.BlockComputer;
import com.github.tartaricacid.netmusic.block.BlockMusicPlayer;
import com.github.tartaricacid.netmusic.tileentity.TileEntityAmplifiedMusicPlayer;
import com.github.tartaricacid.netmusic.tileentity.TileEntityMusicPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetMusic.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NetMusic.MOD_ID);

    public static RegistryObject<Block> MUSIC_PLAYER = BLOCKS.register("music_player", BlockMusicPlayer::new);
    public static RegistryObject<Block> AMPLIFIED_MUSIC_PLAYER = BLOCKS.register("amplified_music_player", BlockAmplifiedMusicPlayer::new);
    public static RegistryObject<Block> CD_BURNER = BLOCKS.register("cd_burner", BlockCDBurner::new);
    public static RegistryObject<Block> COMPUTER = BLOCKS.register("computer", BlockComputer::new);

    public static RegistryObject<BlockEntityType<TileEntityMusicPlayer>> MUSIC_PLAYER_TE = TILE_ENTITIES.register("music_player", () -> TileEntityMusicPlayer.TYPE);
    public static RegistryObject<BlockEntityType<TileEntityAmplifiedMusicPlayer>> AMPLIFIED_MUSIC_PLAYER_TE = TILE_ENTITIES.register("amplified_music_player", () -> TileEntityAmplifiedMusicPlayer.TYPE);
}
