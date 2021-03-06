/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * HumanPlayer.java
 *
 * Created on 28. September 2004, 17:19
 */

package source.main;

import java.awt.Color;

/** for humanplayer 
 * @see Chronos for chronical-order-implementation
 *
 * @author  peter
 */
public class HumanPlayer extends Player
{
    
    public HumanPlayer(Color color, String namePlayer,int no,boolean directAccess)
    { super(HUMAN,color, namePlayer, no, directAccess);
    }    
    
    public HumanPlayer(Color color, String namePlayer,int no)
    { super(HUMAN,color, namePlayer, no, true);
    }
    
    public Path moves(Path opponentsPath)
    { while(!EbbeFlut.chronical.isTurnFinished() && (!EbbeFlut.chronical.isGameFinished() || EbbeFlut.chronical.lastMoves()) );
      return path;
    }
    
}
