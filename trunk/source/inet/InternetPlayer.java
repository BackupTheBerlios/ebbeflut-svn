/*
 * InternetHumanPlayer.java
 *
 * Created on 29. Januar 2005, 19:41
 */

package source.inet;

import source.main.*;
import java.awt.Color;

/** InternetPlayer could be an human or an computer opponent in the net
 *
 * @author  peter
 */
public class InternetPlayer extends Player
{
    private boolean ready;
      
    public InternetPlayer(Color color, String namePlayer, int no)
    { super(INTERNET,color, namePlayer, no, false);
    }    
    
    public Path moves(Path opponentsPath)
    { //Path path=new Path();
      ready=false;
      
      System.out.println("InternetPlayer.start");
      while(!ready && !EbbeFlut.chronical.isGameFinished());
      System.out.println("InternetPlayer.ready");
          
      return path;
    }
    
    public void nextTurn(String pathAsString)
    { path=Path.stringToPath(pathAsString, this);
      ready=true;
    }    
}
