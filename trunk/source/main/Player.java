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

package source.main;
import source.util.Const;
import java.awt.Color;

/** if you want to make a new player, may be an computerthinking one than use this class
 */
public abstract class Player
{ private String name;  
  private Color color;
  private String type;
  private long waitMillis=500;
  private int playBack;
  protected boolean directAccess;//if the player is local or has direct acces to the "real" board

  protected Path path=new Path();
  public int no;   
  static public String AI="computer",HUMAN="human"
                       ,INTERNET="internetPlayer";
  
  
  /** @param type "computer","human","internetPlayer"
   *  @param namePlayer the human readable name of each player
   *  @param no Const.NO_1 or Const.NO_2
   *  @param directAccessToTheBoard if this player sends paths(=false) or moves (=true)
   */
  public Player(String type, Color colour, String namePlayer,int no, boolean directAccessToTheBoard)
  { this.type=type; 
     directAccess=directAccessToTheBoard;
     this.color=colour;
     name=namePlayer;
     this.no=no;    
     if(no==Const.NO_1) playBack=Const.PLAYBACK1;
     else playBack=Const.PLAYBACK2;
  }  
  
  public Path nextTurn(Path oppenentsPath)  
  { System.out.println("\n\n"+getName());
    path.clear();//this is very (and only) important for the directAccess Player like HumanPlayer
    
    path=moves(oppenentsPath);
    show_set_Path(path);
    
    return path;
  }
  
  private void show_set_Path(Path path)
  {     
    for(int i=0; i<path.getSize(); i++)
    {  System.out.println(path.getElement(i).toString());
    }
    if(path.getSize()==1 && path.getElement(0)==Const.doYourWorkMove)
      return;
    //pc hasn't move directly == no direct Access
    if(directAccess) 
    { if(playBack==0) return;//skip play back
       for(int i=path.getSize()-1; i>=0; i--)
       { path.getElement(i).takeBack();
       }
    } 

    for(int i=0; i<path.getSize(); i++)
     { loop(playBack,i);
        path.getElement(i).doIt();
        if(playBack>0) myWait(waitMillis);        
      }        
  }

  public boolean hasDirectAccess()
  { return directAccess;
  }
  
  public int playBackLoop()
  { return playBack;
  }
   private void myWait(long millis)
   { try
     { Thread.sleep(millis);
     }
     catch(InterruptedException ie)
     { System.out.println("sth goes wrong with the sleep operation");
     }
   }
    
    private void loop(int playBack,int i)
    { for(int counter=0; counter<playBack; counter++)
      { path.getElement(i).doIt();
        myWait(waitMillis);
        path.getElement(i).takeBack();        
        myWait(waitMillis);                
      }
    }
   
  abstract public Path moves(Path opponentsPath);
  
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
  
  public String toString()
  { return name+" "+type+" "+source.gui.SettingsDialog.colorToString(color)+" "+no;
  }
  
  public Player getClone()
  { return cloneWithNewSettings(no,type,name,color);
  }
  
  public void pushMove(Move move)
  { path.push(move);
  }
  
  static public Player cloneWithNewSettings(int newNo, String newKind, String newName,Color newColor)
  { Player player;
    
    if(newKind.equals(HUMAN)) 
      player=new HumanPlayer(newColor,newName,newNo);
    else if(newKind.equals(AI)) 
      player=new AI(newColor,newName,newNo);
    else if(newKind.equals(INTERNET)) 
      player=new source.inet.InternetPlayer(newColor,newName,newNo);
    else return null;       
    
    return player;
  }
}