package Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

public class ColorSelectorListener implements ActionListener
{
  private ColorSelector color;
  @Override
  public void actionPerformed(ActionEvent e)
  {
    color = ColorSelector.getInstance();
    
    int fcolors[] = color.getForegroundColors();
    int bcolors[] = color.getBackgroundColors();
    
    try
    {
      FileWriter colorWriter = new FileWriter("src/app/config.txt");
      colorWriter.write(fcolors[0] + " " + fcolors[1] + " " + fcolors[2] + "\n");
      colorWriter.write(bcolors[0] + " " + bcolors[1] + " " + bcolors[2] + "\n");
      colorWriter.close();
    }
    catch (IOException e1)
    {
      System.out.println("Could not find config file.");
    }
  }

}