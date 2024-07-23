package com.github.tartaricacid.netmusic.tileentity;

import com.github.tartaricacid.netmusic.block.BlockMusicPlayer;
import com.github.tartaricacid.netmusic.init.InitBlocks;
import com.github.tartaricacid.netmusic.inventory.MusicPlayerInv;
import com.github.tartaricacid.netmusic.item.ItemMusicCD;
import com.github.tartaricacid.netmusic.network.NetworkHandler;
import com.github.tartaricacid.netmusic.network.message.MusicToClientMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileEntityMusicPlayer extends BlockEntity {
    public static final BlockEntityType<TileEntityMusicPlayer> TYPE = BlockEntityType.Builder.of(TileEntityMusicPlayer::new, InitBlocks.MUSIC_PLAYER.get()).build(null);
    private static final String CD_ITEM_TAG = "ItemStackCD";
    private static final String IS_PLAY_TAG = "IsPlay";
    private static final String CURRENT_TIME_TAG = "CurrentTime";
    private static final String SIGNAL_TAG = "RedStoneSignal";
    private final ItemStackHandler playerInv = new MusicPlayerInv(this);
    private LazyOptional<IItemHandler> playerInvHandler;
    private int currentTime;

    protected TileEntityMusicPlayer(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
    }

    public TileEntityMusicPlayer(BlockPos blockPos, BlockState blockState) {
        this(TYPE, blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        getPersistentData().put(CD_ITEM_TAG, playerInv.serializeNBT());
        getPersistentData().putInt(CURRENT_TIME_TAG, currentTime);
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        CompoundTag data = getPersistentData();
        playerInv.deserializeNBT(data.getCompound(CD_ITEM_TAG));
        setHasRecord(!playerInv.getStackInSlot(0).isEmpty());
        currentTime = data.getInt(CURRENT_TIME_TAG);

        // Compatibility
        if (data.contains(IS_PLAY_TAG)) {
            setPlay(data.getBoolean(IS_PLAY_TAG));
            data.remove(IS_PLAY_TAG);
        }
        if (data.contains(SIGNAL_TAG)) {
            setSignal(data.getBoolean(SIGNAL_TAG));
            data.remove(SIGNAL_TAG);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStackHandler getPlayerInv() {
        return playerInv;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (this.playerInvHandler == null) {
                this.playerInvHandler = LazyOptional.of(this::createHandler);
            }
            return this.playerInvHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setBlockState(BlockState blockState) {
        super.setBlockState(blockState);
        if (this.playerInvHandler != null) {
            LazyOptional<?> oldHandler = this.playerInvHandler;
            this.playerInvHandler = null;
            oldHandler.invalidate();
        }
    }

    private IItemHandler createHandler() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof BlockMusicPlayer) {
            return this.playerInv;
        }
        return null;
    }

    public void setPlayToClient(ItemMusicCD.SongInfo info) {
        setPlayToClient(info, 15);
    }

    public void setPlayToClient(ItemMusicCD.SongInfo info, int signal) {
        this.setCurrentTime(info.songTime * 20 + 64);
        setPlay(true);
        if (level != null && !level.isClientSide) {
            MusicToClientMessage msg = createMusicToClientMessage(info, signal);
            NetworkHandler.sendToNearby(level, worldPosition, msg.getVolume() * 16 + 32, msg);
        }
    }

    @NotNull
    protected MusicToClientMessage createMusicToClientMessage(ItemMusicCD.SongInfo info, int signal) {
        return new MusicToClientMessage(worldPosition, info.songUrl, info.songTime, info.songName);
    }

    public void markDirty() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (playerInvHandler != null) {
            playerInvHandler.invalidate();
            playerInvHandler = null;
        }
    }

    public void setCurrentTime(int time) {
        this.currentTime = time;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setHasRecord(boolean hasRecord) {
        setState(BlockMusicPlayer.HAS_RECORD, hasRecord);
    }

    public boolean isPlay() {
        return getState(BlockMusicPlayer.ENABLED);
    }

    public void setPlay(boolean play) {
        setState(BlockMusicPlayer.ENABLED, play);
    }

    public boolean hasSignal() {
        return getState(BlockMusicPlayer.POWERED);
    }

    public void setSignal(boolean signal) {
        setState(BlockMusicPlayer.POWERED, signal);
    }

    public <T extends Comparable<T>> T getState(Property<T> property) {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof BlockMusicPlayer && level != null) {
            return state.getValue(property);
        }
        return null;
    }

    public <T extends Comparable<T>,V extends T> void setState(Property<T> property, V value) {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof BlockMusicPlayer && level != null) {
            level.setBlock(worldPosition, state.setValue(property, value), Block.UPDATE_ALL);
        }
    }

    public void tickTime() {
        if (currentTime > 0) {
            currentTime--;
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TileEntityMusicPlayer te) {
        te.tickTime();
        if (0 < te.getCurrentTime() && te.getCurrentTime() < 16 && te.getCurrentTime() % 5 == 0) {
            te.setPlay(false);
            te.markDirty();
        }
    }
}
