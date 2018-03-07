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

public class ViewNetworksCommand extends CommandBase
{
  public ViewNetworksCommand()
  {
    aliases = Lists.newArrayList(AutomatedStorage.modId, "AC_VN", "ac_viewnetworks");
  }

  private final List<String> aliases;

  @Override
  @Nonnull
  public String getName()
  {
    return "ac_viewnetworks";
  }

  @Override
  @Nonnull
  public String getUsage(@Nonnull ICommandSender sender)
  {
    return "ac_viewnetworks";
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

    /* view full registry */
    String netList = "";
    for (int network : networks)
    {
      List<BlockPos> blocks = autoChestRegistry.getAutoChests(network);
      if (blocks != null && !blocks.isEmpty())
        netList += network + "(" + blocks.size() +") ";
    }
    sender.sendMessage(new TextComponentString("Networks: " + netList));
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
