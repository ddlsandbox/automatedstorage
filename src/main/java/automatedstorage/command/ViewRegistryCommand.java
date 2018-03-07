package automatedstorage.command;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import automatedstorage.AutomatedStorage;
import automatedstorage.network.AutoChestRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ViewRegistryCommand extends CommandBase
{
  public ViewRegistryCommand()
  {
    aliases = Lists.newArrayList(AutomatedStorage.modId, "AC_VR", "ac_viewregistry");
  }

  private final List<String> aliases;

  @Override
  @Nonnull
  public String getName()
  {
    return "ac_viewregistry";
  }

  @Override
  @Nonnull
  public String getUsage(@Nonnull ICommandSender sender)
  {
    return "ac_viewregistry [network_id | \"lookup\"]";
  }

  @Override
  @Nonnull
  public List<String> getAliases()
  {
    return aliases;
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException
  {
    AutoChestRegistry autoChestRegistry = AutoChestRegistry.get(server.getEntityWorld());
    Set<Integer> networks = autoChestRegistry.getNetworks();
    
    if (args.length < 1)
    {
      /* view full registry */
      for (int network : networks)
      {
        Set<BlockPos> blocks = autoChestRegistry.getAutoChests(network);
        if (blocks != null && !blocks.isEmpty())
          sender.sendMessage(new TextComponentString("Network " + network + " : " + blocks));
      }
    } else
    {
      String s = args[0];
      if (s.equalsIgnoreCase("lookup"))
      {
        String posList = "";
        Set<BlockPos> lookupKeys = autoChestRegistry.getLookupKeys();
        for (BlockPos pos : lookupKeys)
        {
          if (pos != null)
            posList += "(" + pos.getX() + "," + pos.getY() + "," + pos.getZ() +")[" + autoChestRegistry.getNetworkFor(pos) + "] ";
        }
        sender.sendMessage(new TextComponentString("Lookup: " + posList));
      }
      else
      {
        int network;
        try
        {
          network = Integer.parseInt(s);
        } catch (NumberFormatException e)
        {
          sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error parsing network ID!"));
          return;
        }
        sender.sendMessage(
            new TextComponentString("Network " + network + " : " + autoChestRegistry.getAutoChests(network)));
      }
    }
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender)
  {
    return true;
  }

  @Override
  @Nonnull
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
      @Nullable BlockPos targetPos)
  {
    return Collections.emptyList();
  }
}
