package automatedstorage.network;

import automatedstorage.block.ModBlocks;
import automatedstorage.block.chest.AutoChestConfigContainer;
import automatedstorage.block.chest.AutoChestRegistry;
import automatedstorage.block.chest.AutoChestTileEntity;
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

  public PacketUpdateNetwork(AutoChestTileEntity te)
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
      
      if (serverPlayer.openContainer instanceof AutoChestConfigContainer)
      {
        try
        {
          if (message.updateRegistry)
          {
            autoChestRegistry.addAutoChest(message.newNetwork, message.pos, message.type);
          }
          
          ((AutoChestConfigContainer) serverPlayer.openContainer).updateNetwork(message.newNetwork);
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