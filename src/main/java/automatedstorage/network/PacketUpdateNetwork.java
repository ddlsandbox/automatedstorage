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
package automatedstorage.network;

import automatedstorage.block.ModBlocks;
import automatedstorage.container.ContainerAutoChestConfig;
import automatedstorage.tileentity.TileEntityAutoChest;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateNetwork implements IMessage
{
  private BlockPos pos;
  private int newNetwork;
  private int type;
  private boolean updateRegistry;

  public PacketUpdateNetwork()
  {
  }

  public PacketUpdateNetwork(BlockPos pos, int newNetwork, int type, boolean updateRegistry)
  {
    this.pos = pos;
    this.newNetwork = newNetwork;
    this.type = type;
    this.updateRegistry = updateRegistry;
  }

  public PacketUpdateNetwork(TileEntityAutoChest te)
  {
    this(te.getPos(), te.getNetworkId(), te.getBlockType() == ModBlocks.autoChestSink?1:0, te.getBlockType() == ModBlocks.autoChest);
  }

  @Override
  public void toBytes(ByteBuf buf)
  {
    buf.writeLong(this.pos.toLong());
    buf.writeInt(this.newNetwork);
    buf.writeInt(this.type);
    buf.writeBoolean(this.updateRegistry);
  }

  @Override
  public void fromBytes(ByteBuf buf)
  {
    this.pos = BlockPos.fromLong(buf.readLong());
    this.newNetwork = buf.readInt();
    this.type = buf.readInt();
    this.updateRegistry = buf.readBoolean();
  }

  public static class Handler implements IMessageHandler<PacketUpdateNetwork, IMessage>
  {

    @Override
    public IMessage onMessage(PacketUpdateNetwork message, MessageContext ctx)
    {

      EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

      AutoChestRegistry autoChestRegistry = AutoChestRegistry
          .get(ctx.getServerHandler().player.world);
      
      if (serverPlayer.openContainer instanceof ContainerAutoChestConfig)
      {
        try
        {
          if (message.updateRegistry)
          {
            autoChestRegistry.addAutoChest(message.newNetwork, message.pos, message.type);
          }
          
          ((ContainerAutoChestConfig) serverPlayer.openContainer).updateNetwork(message.newNetwork);
          serverPlayer.openContainer.detectAndSendChanges();
        } catch (Exception e)
        {
          serverPlayer.sendMessage(new TextComponentString(e.getMessage()));
        }
      }

      // No response packet
      return null;
    }
  }
}