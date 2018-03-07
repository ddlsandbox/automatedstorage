/* Automated Chests Minecraft Mod
 * Copyright (C) 2018 Diego Darriba
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package automatedstorage.tileentity;

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
    final int METADATA = 0;
    return new SPacketUpdateTileEntity(this.pos, METADATA, compound);
  }

  @Override
  public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
  {
    this.readSyncableNBT(pkt.getNbtCompound(), NBTType.SYNC);
    NBTTagCompound updateTagDescribingTileEntityState = pkt.getNbtCompound();
    handleUpdateTag(updateTagDescribingTileEntityState);
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
  }

  public final void sendUpdate()
  {
    if (this.world != null && !this.world.isRemote)
    {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeSyncableNBT(compound, NBTType.SYNC);
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
