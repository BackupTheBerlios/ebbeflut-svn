/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * BoardButtonAL.java
 *
 * Created on 8. Oktober 2004, 13:10
 */

import java.awt.*;
import java.awt.event.*;

/** the actionListener for all buttons on the visible board
 * @author  peter karich
 */  
class BoardButtonAL implements ActionListener
{   Card firstCard;
    Move move;
    int a;
    int b;
     
   public void actionPerformed(ActionEvent ae)
   { if(EbbeFlut.chronical.isGameFinished() && !EbbeFlut.chronical.lastMoves()) return;
     if(!EbbeFlut.currentPlayer.getType().equals(Const.HUMAN_PLAYER)) return;
           
      String cl=((Button)ae.getSource()).getActionCommand();
      Punkt d=GraphicBoard.parseActionCommand(cl);
      a=d.getX();
      b=d.getY();
      
     if(EbbeFlut.chronical.lastMoves())
     { if(clickOppentsFinishButton()) return;
       else if(clickStartButtonToEnd()) return;
       else if(!EbbeFlut.chronical.clicked() && peekFrom5x5()) return;
       else if(EbbeFlut.chronical.clicked() && pushTo5x5()) return;
     }
     else if(EbbeFlut.chronical.fromBeginning())
     { if(clickOppentsFinishButton()) return;
       else if(EbbeFlut.chronical.fresh() && clickStartButtonToStart()) return;
       else if((EbbeFlut.chronical.placed() || EbbeFlut.chronical.clicked()) && clickStartButtonToEnd()) return;
       else if(EbbeFlut.chronical.placed() && peekFrom5x5()) return;
       else if((EbbeFlut.chronical.clicked() || EbbeFlut.chronical.popped()) && pushTo5x5()) return;
     }
     //if opponent found move(s)
     else
     { if(clickStartButtonToEnd()) return;
       else if(!EbbeFlut.chronical.clicked() && peekFrom5x5()) return;       
       else if(EbbeFlut.chronical.clicked() && pushTo5x5()) return;       
     }              
   }//public actionPerformed
   
    /** @return true if the methode identify the click as click on oppents finishbutton
    */
   private boolean clickOppentsFinishButton()
   {  if(!((EbbeFlut.currentPlayer.no==Const.NO_1 && a==Const.finish2) || 
           (EbbeFlut.currentPlayer.no==Const.NO_2 && a==Const.finish1)
          )) return false;
          
     //is the other player ready?
     if(!EbbeFlut.chronical.fresh()) return true;//true=> do not look for other possible buttons
      
     if(Move.getAllPossible(EbbeFlut.getOtherPlayer(EbbeFlut.currentPlayer)).size()>0)
           EbbeFlut.chronical.setTheOtherPlayerIsNotReady();
     
     return true;
   }
   
   /** @return true if the methode identify the click as a click on startbutton
    */
   private boolean clickStartButtonToStart()
   { //player wants to start
     OwnerField field;
     
     if(EbbeFlut.currentPlayer.no==Const.NO_1 && a==Const.start1)
       field=EbbeFlut.board.getStartStack1();     
     else if(EbbeFlut.currentPlayer.no==Const.NO_2 && a==Const.start2)
       field=EbbeFlut.board.getStartStack2();     
     else return false;
       
     firstCard=field.peek();
     field.setLabel("click "+firstCard+" to the board!");
     EbbeFlut.chronical.setStatus(Chronos.POPPED); 
     
     return true;
   }
   
   
   /** @return true if the methode identify the click as a click on startbutton
    */
   private boolean clickStartButtonToEnd()
   { //player is ready??:move-end action
     
     if(EbbeFlut.currentPlayer.no==Const.NO_1 && a==Const.start1)
      EbbeFlut.board.setStartStackLabel("Ready!",Const.NO_1);
     else if(EbbeFlut.currentPlayer.no==Const.NO_2 && a==Const.start2)
      EbbeFlut.board.setStartStackLabel("Ready!",Const.NO_1);
     else return false;
     
     EbbeFlut.chronical.setTurnIsFinished();
        
     return true;
   }
   
   /** @return true if the methode identify the click as 5x5 click,if EbbeFlut.chronical is popped or clicked
    */
   private boolean pushTo5x5()
   { if(!(a>=0 && a<5 && b>=0 && b<5)) return false;
     
     if(firstCard==null) return true;
     move=new Move(firstCard, a,b);//save (?) for possible takeback()
         
     if(move.isPossible())
     {  int i=move.doIt();
         
       if(i==Move.FINISH) System.out.println("a card is ins saeckl gehuepferlt");
       //a "wrong" edge move?
       else if(i==Move.PROMPT) System.out.println("\"wrong\" move!Your PLACED card was removed!");
         
       EbbeFlut.chronical.setStatus(Chronos.PLACED);
     }          
     /**do not setStatus to Placed if move was not possible and if the FIRST card 
       was POPPED-> this would cause that we could pop, and pop ,....
      from the startstack, 
      but set it, if we PEEKED a card from board -> so we can change the peeked card
     */
     if(EbbeFlut.chronical.clicked()) EbbeFlut.chronical.setStatus(Chronos.PLACED);       
       
     return true;     
   }
   
   /** @return true if the methode identify the click as 5x5 click,if chronical is placed
    */
   private boolean peekFrom5x5()
   { if(!(a>=0 && a<5 && b>=0 && b<5)) return false;
   
     firstCard=EbbeFlut.board.peek(a,b);       
     if(firstCard !=null && EbbeFlut.currentPlayer.isOwnerOf(firstCard))
     { EbbeFlut.chronical.setStatus(Chronos.CLICKED);
     } 
     return true;     
   }   
   
  
}//class BoardButton A L        
  
