/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 *
 * Chronos.java
 *
 * Created on 26. September 2004, 19:07
 */

/** chronical order to identify wether a card is popped, placed, moved
 *  and wether the turn is finished or the complete game
 * 
 * @author  peter
 */
public class Chronos 
{
    int moveNo=0;
    boolean finishedGame, finishedTurn, retFromBeginning,lastMoves;
	
    /** a whole turn is splitted into several steps
     */
    static int FRESH=-1, SEARCHED=0, POPPED=1, PLACED=2, CLICKED=3, MOVED=4, status;
   
    public Chronos() 
    { status=FRESH;
      finishedGame=false;
      finishedTurn=false;
      retFromBeginning=true;
      lastMoves=false;
    }
    
    public void newTurn(boolean fromBeg)
    { finishedTurn=false;          
      retFromBeginning=true;//!!important -> this var is false if this player will see forgotten moves of the other one. But if he had forgotten moves than he shouldn't able to see forgotten moves of the other player
      
      if(fromBeg) status=FRESH;      
      else        status=PLACED;            
    }    
    
    public void setStatus(int s)
    { status=s;
      if(getStatus().equals("undefined"))      
      { throw new Error("sth gowes wrong in chronos");      
      }    
    }
    
    public String getStatus()
    { String tmp="undefined";
      
      switch(status)
      { case -1: tmp="fresh"; break;
        case 0: tmp="searched"; break;
        case 1: tmp="popped"; break;
        case 2: tmp="placed"; break;
        case 3: tmp="clicked"; break;
        case 4: tmp="moved"; break;
      }
      return tmp;
    }
    
    public void setGameIsFinished()
    { finishedGame=true;
    }

    public void setTurnIsFinished()
    { finishedTurn=true;      
    }    
    
    public boolean isGameFinished()
    { return finishedGame;
    }
     
    public boolean fresh()
    { return status==FRESH;
    }
    
    /**no need!
     */
    public boolean searched()
    { return status==SEARCHED;
    }
    
    public boolean popped()
    { return status==POPPED;
    }
    
    public boolean placed()
    { return status==PLACED;
    }
    
    public boolean clicked()
    { return status==CLICKED;
    }

    /**no need until yet
     */
    public boolean moved()
    { return status==MOVED;
    }
    
    public boolean isTurnFinished()
    { return finishedTurn;
    }
    
    public void setLastMoves(boolean b)
    { lastMoves=b;      
    } 
    
    public boolean lastMoves()
    { return lastMoves;
    }
    
    public void setTheOtherPlayerIsNotReady()
    { retFromBeginning=false;
      finishedTurn=true;      
    }
    
    public boolean fromBeginning() 
    { return retFromBeginning;    
    }
    
    public void nextMove()    
    { moveNo++;      
      if(moveNo>=Const.NC*2) finishedGame=true;
    }
    
    public void prevMove()    
    { moveNo--;           
      if(moveNo<Const.NC*2) finishedGame=false;
    }          
    
    public int getMoveNo()
    { return moveNo/2+1;
    }
}
