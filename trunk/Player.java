/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * Player.java
 *
 */

import java.awt.Color;

/** if you want to make a new player, may be an computerthinking one than use this class
 */
abstract class Player
{ private String name;  
  private Color color;
  private String type;
  
  public int no;   
  
  public Player(String type, Color colour, String namePlayer,int no)
  { this.type=type; 
    this.color=colour;
    name=namePlayer;
    this.no=no;    
  }  
  
  abstract public void moves();
  
  public Color getColor()
  { return color;
  }
  
  public String getName()
  { return name;
  }
  
  public String getType()
  { return type;
  }
  
  public void setColor(Color c)
  { color=c;
  }
  
  public void setName(String n)
  { name=n;
  } 
  
  public boolean equals(Object obj)
  { return this.no==((Player)obj).no;
  }
  
  public boolean isOwnerOf(Card card)
  { //if(card==null) return false;
    return no==card.getOwner().no;
  }
  
  public Player getClone()
  { return cloneWithNewSettings(no,type,name,color);
  }
  
  static public Player cloneWithNewSettings(int newNo, String newKind, String newName,Color newColor)
  { Player player;
    
    if(newKind.equals(Const.HUMAN_PLAYER)) 
      player=new HumanPlayer(newColor,newName,newNo);
    else if(newKind.equals(Const.PC_PLAYER)) 
      player=new PcPlayer(newColor,newName,newNo);
    else if(newKind.equals(Const.AI_PLAYER)) 
      player=new AI(newColor,newName,newNo);
    else return null;       
    
    return player;
  }
}