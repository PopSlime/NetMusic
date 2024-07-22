package com.github.tartaricacid.netmusic.tileentity;

import com.github.tartaricacid.netmusic.init.InitBlocks;
import com.github.tartaricacid.netmusic.item.ItemMusicCD;
import com.github.tartaricacid.netmusic.network.message.MusicToClientMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityAmplifiedMusicPlayer extends TileEntityMusicPlayer {
    @SuppressWarnings("DataFlowIssue")
    public static final BlockEntityType<TileEntityAmplifiedMusicPlayer> TYPE = BlockEntityType.Builder.of(TileEntityAmplifiedMusicPlayer::new, InitBlocks.AMPLIFIED_MUSIC_PLAYER.get()).build(null);

    public TileEntityAmplifiedMusicPlayer(BlockPos blockPos, BlockState blockState) {
        super(TYPE, blockPos, blockState);
    }

    @Override
    protected @NotNull MusicToClientMessage createMusicToClientMessage(ItemMusicCD.SongInfo info, int signal) {
        return new MusicToClientMessage(worldPosition, info.songUrl, info.songTime, info.songName, signal);
    }
}
