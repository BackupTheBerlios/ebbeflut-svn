/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * PcPlayer.java
 *
 * Created on 28. September 2004, 16:47
 */

import java.awt.*;

/** a losy player moves randomly and without any assessment and recursion
 *
 * @author  peter
 */
public class PcPlayer extends Player
{   
    /** Creates a new instance of PcPlayer */
    public PcPlayer(Color color,String name, int no) 
    { super(Const.PC_PLAYER, color, name, no);      
    }
    
    public void moves()
    { 
      //test wether all possible moves are done from the other player
      if(Move.getAllPossible(EbbeFlut.getOtherPlayer(this)).size()>0)
      { EbbeFlut.chronical.setTheOtherPlayerIsNotReady();        
      }      
      
      if(!EbbeFlut.chronical.lastMoves())
      {  Card startCard;      
        if(no==Const.NO_1)
        { startCard=EbbeFlut.board.getStartStack1().pop();        
          
          switch(EbbeFlut.nextRandomInt()%3)
          { case 0: EbbeFlut.board.push(4,4,startCard); break;
            case 1: EbbeFlut.board.push(4,3,startCard); break;
            case 2: EbbeFlut.board.push(3,4,startCard); break;
            default: System.out.println("default");
          }
        }
        else
        { startCard=EbbeFlut.board.getStartStack2().pop();
          switch(EbbeFlut.nextRandomInt()%3)
          { case 0: EbbeFlut.board.push(0,0,startCard); break;
            case 1: EbbeFlut.board.push(1,0,startCard); break;
            case 2: EbbeFlut.board.push(0,1,startCard); break;
            default: System.out.println("default");
          }
        }
      }    
      
       Stack all=Move.getAllPossible(this);
       Move move;
      
      System.out.println("startStack size"+all.size());
      for(int i=0; i<all.size(); i++)//Iterator iter=all.iterator(); iter.hasNext();)
      { move=(Move)all.elementAt(i);
        System.out.println(move.toString());       
      }
      System.out.println("endStack");    
      
      calc(all);      
    }
    
    public void calc(Stack all)
    { if(all.empty()) return;
      
      if(no==2) ((Move)all.pop()).doIt();
      else 
      { ((Move)all.remove(0)).doIt();
      }
      all=Move.getAllPossible(this);      
      calc(all);
    }
    
}
