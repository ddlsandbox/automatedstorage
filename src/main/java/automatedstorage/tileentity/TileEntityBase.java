package automatedstorage.tileentity;

import automatedstorage.AutomatedStorage;
import automatedstorage.network.PacketHandler;
import automatedstorage.network.PacketServerToClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class TileEntityBase extends TileEntity implements ITickable
{
  public static final int TILE_ENTITY_UPDATE_INTERVAL = 10;

  public enum NBTType
  {
    SAVE_TILE, SYNC, SAVE_BLOCK
  }

  protected int ticksElapsed;
  protected TileEntity[] tilesAround = new TileEntity[6];
  protected boolean hasSavedDataOnChangeOrWorldStart;

  public TileEntityBase()
  {

  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound)
  {
    this.writeSyncableNBT(compound, NBTType.SAVE_TILE);
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound)
  {
    this.readSyncableNBT(compound, NBTType.SAVE_TILE);
  }

  @Override
  public final SPacketUpdateTileEntity getUpdatePacket()
  {
    NBTTagCompound compound = new NBTTagCompound();
    this.writeSyncableNBT(compound, NBTType.SYNC);
    return new SPacketUpdateTileEntity(this.pos, -1, compound);
  }

  @Override
  public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
  {
    this.readSyncableNBT(pkt.getNbtCompound(), NBTType.SYNC);
  }

  @Override
  public final NBTTagCompound getUpdateTag()
  {
    NBTTagCompound compound = new NBTTagCompound();
    this.writeSyncableNBT(compound, NBTType.SYNC);
    return compound;
  }

  @Override
  public final void handleUpdateTag(NBTTagCompound compound)
  {
    this.readFromNBT(compound);
    //this.readSyncableNBT(compound, NBTType.SYNC);
  }

  public final void sendUpdate()
  {
    if (this.world != null && !this.world.isRemote)
    {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeSyncableNBT(compound, NBTType.SYNC);

      NBTTagCompound data = new NBTTagCompound();
      data.setTag("Data", compound);
      data.setInteger("X", this.pos.getX());
      data.setInteger("Y", this.pos.getY());
      data.setInteger("Z", this.pos.getZ());
      AutomatedStorage.network.sendToAllAround(new PacketServerToClient(data, PacketHandler.TILE_ENTITY_HANDLER),
          new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.getPos().getX(),
              this.getPos().getY(), this.getPos().getZ(), 64));
    }
  }

  public void writeSyncableNBT(NBTTagCompound compound, NBTType type)
  {
    if (type != NBTType.SAVE_BLOCK)
    {
      super.writeToNBT(compound);
    }

    if (type == NBTType.SAVE_TILE)
    {
      compound.setInteger("TicksElapsed", this.ticksElapsed);
    }
  }

  public void readSyncableNBT(NBTTagCompound compound, NBTType type)
  {
    if (type != NBTType.SAVE_BLOCK)
    {
      super.readFromNBT(compound);
    }

    if (type == NBTType.SAVE_TILE)
    {
      this.ticksElapsed = compound.getInteger("TicksElapsed");
    }
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
  {
    return !oldState.getBlock().isAssociatedBlock(newState.getBlock());
  }

  @Override
  public final void update()
  {
    this.updateEntity();
  }

  public void updateEntity()
  {
    this.ticksElapsed++;

    // FIXME AVOID CONSTANT UPDATE
    if (this.shouldSaveDataOnChangeOrWorldStart())
      this.saveDataOnChangeOrWorldStart();
    // if (!this.hasSavedDataOnChangeOrWorldStart)
    // {
    // if (this.shouldSaveDataOnChangeOrWorldStart())
    // {
    // this.saveDataOnChangeOrWorldStart();
    // }
    //
    // this.hasSavedDataOnChangeOrWorldStart = true;
    // }
  }

  public void saveDataOnChangeOrWorldStart()
  {
    for (EnumFacing side : EnumFacing.values())
    {
      BlockPos pos = this.pos.offset(side);
      if (this.world.isBlockLoaded(pos))
      {
        this.tilesAround[side.ordinal()] = this.world.getTileEntity(pos);
      }
    }
  }

  public boolean shouldSaveDataOnChangeOrWorldStart()
  {
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing)
  {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
    {
      IItemHandler handler = this.getItemHandler(facing);
      if (handler != null)
      {
        return (T) handler;
      }
    } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
    {
      IFluidHandler tank = this.getFluidHandler(facing);
      if (tank != null)
      {
        return (T) tank;
      }
    } else if (capability == CapabilityEnergy.ENERGY)
    {
      IEnergyStorage storage = this.getEnergyStorage(facing);
      if (storage != null)
      {
        return (T) storage;
      }
    }

    return super.getCapability(capability, facing);
  }

  public IFluidHandler getFluidHandler(EnumFacing facing)
  {
    return null;
  }

  public IEnergyStorage getEnergyStorage(EnumFacing facing)
  {
    return null;
  }

  public IItemHandler getItemHandler(EnumFacing facing)
  {
    return null;
  }

  protected boolean sendUpdateWithInterval()
  {
    if (this.ticksElapsed % TILE_ENTITY_UPDATE_INTERVAL == 0)
    {
      this.sendUpdate();
      return true;
    } else
    {
      return false;
    }
  }

  protected static void doEnergyInteraction(TileEntity tileFrom, TileEntity tileTo, EnumFacing sideTo, int maxTransfer)
  {
    if (maxTransfer > 0)
    {
      EnumFacing opp = sideTo == null ? null : sideTo.getOpposite();
      if (tileFrom.hasCapability(CapabilityEnergy.ENERGY, sideTo) && tileTo.hasCapability(CapabilityEnergy.ENERGY, opp))
      {

        IEnergyStorage handlerFrom = tileFrom.getCapability(CapabilityEnergy.ENERGY, sideTo);
        IEnergyStorage handlerTo = tileTo.getCapability(CapabilityEnergy.ENERGY, opp);

        if (handlerFrom != null && handlerTo != null)
        {
          int drain = handlerFrom.extractEnergy(maxTransfer, true);
          if (drain > 0)
          {
            int filled = handlerTo.receiveEnergy(drain, false);
            handlerFrom.extractEnergy(filled, false);
            return;
          }
        }
      }
    }
  }

}
