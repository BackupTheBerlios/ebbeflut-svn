/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * Const.java
 *
 * Created on 1. Januar 2004, 12:57
 */

import java.awt.*;

/** some constants useful for the game
 * @author  peter karich
 */
public class Const
{ 
   public Const()
   { Dimension  d=Toolkit.getDefaultToolkit().getScreenSize();
     windowLocation=new Point();
     
    if(zaurusOS)
    { COLOR1=ZAURUS_COLOR1;
      COLOR2=ZAURUS_COLOR2;
    }
    else
    { //shitty getScreenSize, because it is often too big, cause of the panels
      Dimension old=d;
      d=new Dimension(Math.round(old.width*Const.SHORTER_FACTOR),
                      Math.round(old.height*Const.SHORTER_FACTOR));
      windowLocation.setLocation((old.width-d.width)/2, (old.height-d.height)/2);
    }
    windowSize=d;
  }
  
  static public Dimension windowSize;  
  static public Point windowLocation;  
  static public int NC=15; //Number of all Cards, for one! player //|CHEAT| NC=15;
  static public int NO_1=1,NO_2=2;
  static public Color COLOR1=Color.blue, COLOR2=Color.yellow;
  static public Color ZAURUS_COLOR1=Color.black, ZAURUS_COLOR2=Color.black;
  
  static public String player1File="dat/settingsPlayer1";
  static public String player2File="dat/settingsPlayer2";
  static public String ebbeFlutPictureFile="pics/ebbeflut.png";
  static public String germanInfoFile="doc/liesmich.txt";
  static public String englishInfoFile="doc/readme.txt";
  static public String gplFile="doc/gpl.txt";
  
  static public String PC_PLAYER="losy pc",AI_PLAYER="good pc",HUMAN_PLAYER="human";
  static public boolean zaurusOS=true;
  static public Color starterFieldsColor1=Color.gray, starterFieldsColor2=Color.gray;
  static public int CardFieldID=1,OwnerFieldID=2;
  static public float SHORTER_FACTOR=0.9f;
  
  //now only for identification (actionCmd) of the start and finishfiel(button);
  //you need other values then -1 until 5
  static public int start1=-5, start2=-6, finish1=-7, finish2=-8, remove1=-9,remove2=-10, yAll=-11;
}
