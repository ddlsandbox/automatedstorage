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

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateRegistry implements IMessage
{
  private BlockPos pos;
  private int newNetwork;
  private int type;

  public PacketUpdateRegistry()
  {
  }

  public PacketUpdateRegistry(BlockPos pos, int newNetwork, int type)
  {
    this.pos = pos;
    this.newNetwork = newNetwork;
    this.type = type;
  }

  @Override
  public void toBytes(ByteBuf buf)
  {
    buf.writeLong(this.pos.toLong());
    buf.writeInt(this.newNetwork);
    buf.writeInt(this.type);
  }

  @Override
  public void fromBytes(ByteBuf buf)
  {
    this.pos = BlockPos.fromLong(buf.readLong());
    this.newNetwork = buf.readInt();
    this.type = buf.readInt();
  }

  public static class Handler implements IMessageHandler<PacketUpdateRegistry, IMessage>
  {

    @Override
    public IMessage onMessage(PacketUpdateRegistry message, MessageContext ctx)
    {

      AutoChestRegistry autoChestRegistry = AutoChestRegistry
          .get(Minecraft.getMinecraft().world);

      if (message.type == -1)
      {
        /* remove */
        Minecraft.getMinecraft().addScheduledTask(() -> {
          autoChestRegistry.removeAutoChest(message.pos);
        });
      }
      else
      {
        /* add */
        Minecraft.getMinecraft().addScheduledTask(() -> {
          autoChestRegistry.addAutoChest(message.newNetwork, message.pos, message.type);
        });
      }

      // No response packet
      return null;
    }
  }
}