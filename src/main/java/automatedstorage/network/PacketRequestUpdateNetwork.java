package automatedstorage.network;

import automatedstorage.block.chest.AutoChestTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestUpdateNetwork implements IMessage
{
  private BlockPos pos;
  private int dimension;

  public PacketRequestUpdateNetwork(BlockPos pos, int dimension)
  {
    this.pos = pos;
    this.dimension = dimension;
  }

  public PacketRequestUpdateNetwork(AutoChestTileEntity te)
  {
    this(te.getPos(), te.getWorld().provider.getDimension());
  }

  public PacketRequestUpdateNetwork()
  {
  }

  @Override
  public void toBytes(ByteBuf buf)
  {
    buf.writeLong(pos.toLong());
    buf.writeInt(dimension);
  }

  @Override
  public void fromBytes(ByteBuf buf)
  {
    pos = BlockPos.fromLong(buf.readLong());
    dimension = buf.readInt();
  }

  public static class Handler implements IMessageHandler<PacketRequestUpdateNetwork, PacketUpdateNetwork>
  {

    @Override
    public PacketUpdateNetwork onMessage(PacketRequestUpdateNetwork message, MessageContext ctx)
    {
      try
      {
        System.out.println("AUTOCHEST > On Request Message!");
        System.out.println("AUTOCHEST > Data: " + message.pos + " Dim: " + message.dimension);
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
        System.out.println("AUTOCHEST > Got the world!");
        AutoChestTileEntity te = (AutoChestTileEntity) world.getTileEntity(message.pos);
        System.out.println("AUTOCHEST > Got the entity! " + te);
        if (te != null)
        {
          return new PacketUpdateNetwork(te);
        }
      } catch (Exception e)
      {
        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
        serverPlayer.sendMessage(new TextComponentString(e.getMessage()));
      }
      return null;
    }

  }
}
