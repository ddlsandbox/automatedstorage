package automatedstorage.block.chest;

import net.minecraft.util.IStringSerializable;

public enum AutoChestColors implements IStringSerializable
{

  WHITE("White", "white"),
  ORANGE("Orange", "orange"),
  MAGENTA("Magenta", "magenta"),
  LIGHT_BLUE("LightBlue", "lightblue"),
  YELLOW("Yellow", "yellow"),
  LIME("Lime", "lime"),
  PINK("Pink", "pink"),
  GRAY("Gray", "gray"),
  LIGHT_GRAY("LightGray", "silver"),
  CYAN("Cyan", "cyan"),
  PURPLE("Purple", "purple"),
  BLUE("Blue", "blue"),
  BROWN("Brown", "brown"),
  GREEN("Green", "green"),
  RED("Red", "red"),
  BLACK("Black", "black");

  public final String regName;
  public final String oreName;

  AutoChestColors(String oreName, String regName)
  {
    this.oreName = oreName;
    this.regName = regName;
  }

  public static AutoChestColors getColorFromDyeName(String color)
  {
    if (color.substring(0, 3).equals("dye"))
    {
      String actualName = color.substring(3);
      for (int i = 0; i < values().length; i++)
      {
        String aName = values()[i].oreName;
        if (aName != null)
        {
          if (aName.equalsIgnoreCase(actualName))
          {
            return values()[i];
          }
        }
      }
    }
    return null;
  }

  @Override
  public String getName()
  {
    return this.regName;
  }

}