package com.github.tartaricacid.netmusic.block;

import com.github.tartaricacid.netmusic.tileentity.TileEntityAmplifiedMusicPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockAmplifiedMusicPlayer extends BlockMusicPlayer {
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAmplifiedMusicPlayer(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> entityType) {
        return !level.isClientSide ? createTickerHelper(entityType, TileEntityAmplifiedMusicPlayer.TYPE, TileEntityAmplifiedMusicPlayer::tick) : null;
    }
}
