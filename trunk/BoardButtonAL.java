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
    boolean peekCard=false;
      
   /** Is this a bad implementation with usage of EbbeFlut.chronical ? <p>
    *  (1) normal move <p>
    *      if(chronical==fresh) then possible use of: 
    *      a) startbutton to show startCard 
    *      b) finishButton to make a contest to the opponent <p>
    *      
    *      if(chronical==showed) then click and place a special card from/to 
    *      5x5 board and startstacks  <p>
    *
    *      if(chronical==placed) then
    *      a) click and place a special card from/to 5x5 board and startstacks
    *      b) click startField to end move <p>
    *
    *  (3) last moves <p>    
    *      always allowed to
    *      a) click and place a special card from/to 5x5 board and startstacks
    *      b) click startField to end move <p>
    *      if(!peekCard) then possible use of finishButton to make a 
    *      contest to the opponent <p>
    *       
    *
    */
   public void actionPerformed(ActionEvent ae)
   { if(menuAL!=null) menuAL.actionPerformed(ae);
     
     if(EbbeFlut.chronical.isGameFinished() && !EbbeFlut.chronical.lastMoves()) return;
     if(!EbbeFlut.currentPlayer.getType().equals(Const.HUMAN_PLAYER)) return;
           
      String cl=((Button)ae.getSource()).getActionCommand();
      Punkt d=GraphicBoard.parseActionCommand(cl);
      a=d.getX();
      b=d.getY();      
      
     
     if(EbbeFlut.chronical.lastMoves())
     { if(!EbbeFlut.chronical.sthWasClicked() && clickOppentsFinishButton()) return;
       //now we know sth was clicked!!
       EbbeFlut.chronical.setSthWasClicked();
       if(clickStartButtonToEnd()) return;
       else if(!peekCard) peekCard();
       else pushTo5x5();
       
       return;
     }
     //if this player didnt do his work or if he places the startCard while actual move
     if(EbbeFlut.chronical.placedStartCard())
     { if(clickStartButtonToEnd()) return;
       else if(!peekCard) peekCard();
       else pushTo5x5();
       
       return;
     }
     else
     { if(EbbeFlut.chronical.fresh())
       { if(clickOppentsFinishButton()) return;
         else if(clickStartButtonToShow()) return;
       }
       else if(EbbeFlut.chronical.showed())
       { if(!peekCard) peekCard();
         else pushTo5x5();
         return;
       }
       else if(EbbeFlut.chronical.placedStartCard())
       { if(clickStartButtonToEnd()) return;
         else if(!peekCard) peekCard();
         else pushTo5x5();
         return;
       }
     }                        
   }//public actionPerformed
   
    /** @return true if the methode identify the click as click on oppents finishbutton
    */
   private boolean clickOppentsFinishButton()
   {  if(!((EbbeFlut.currentPlayer.no==Const.NO_1 && a==Const.finish2) || 
           (EbbeFlut.currentPlayer.no==Const.NO_2 && a==Const.finish1)
          )) return false;
          
     if(Move.getAllPossible(EbbeFlut.getOtherPlayer(EbbeFlut.currentPlayer)).size()>0)
     { EbbeFlut.chronical.setTheOtherPlayerIsNotReady();
       EbbeFlut.board.getStartStack(EbbeFlut.currentPlayer.no).setLabel("Ready!");
     }
     
     return true;
   }
   
   /** @return true if the methode identify the click as a click on startbutton
    */
   private boolean clickStartButtonToShow()
   { //player wants to see the card=> now opponent isn't contestable
     StartField field;
     
     if(EbbeFlut.currentPlayer.no==Const.NO_1 && a==Const.start1)
       field=EbbeFlut.board.getStartStack1();     
     else if(EbbeFlut.currentPlayer.no==Const.NO_2 && a==Const.start2)
       field=EbbeFlut.board.getStartStack2();     
     else return false;
     
     field.setLabel("click me again and "+field.peek()+" to the board!");
     EbbeFlut.chronical.setStatus(Chronos.SHOWED);
     return true;
   }
   
   
   /** @return true if the methode identify the click as a click on startbutton
    */
   private boolean clickStartButtonToEnd()
   { //player is ready??:move-end action
     if(EbbeFlut.board.getStartStack(EbbeFlut.currentPlayer.no).peek()!=null) return false;
     if((EbbeFlut.currentPlayer.no==Const.NO_1 && a==Const.start1) ||
        (EbbeFlut.currentPlayer.no==Const.NO_2 && a==Const.start2))
     { EbbeFlut.board.getStartStack(EbbeFlut.currentPlayer.no).setLabel("Ready!");
       EbbeFlut.chronical.setTurnIsFinished();
        
       return true;
     }
     
     return false;     
   }
   
   /** @return true if the methode identify the click as 5x5 click
    */
   private boolean pushTo5x5()
   { if(!(a>=0 && a<5 && b>=0 && b<5)) return false;
     
     if(firstCard==null) return true;
     move=new Move(firstCard, a,b);
     if(Move.isStart1(move.fromX,move.fromY))
       EbbeFlut.board.setBackground(move.fromX, move.fromY, Const.starterFieldsColor1);     
     else if(Move.isStart2(move.fromX,move.fromY))
       EbbeFlut.board.setBackground(move.fromX, move.fromY, Const.starterFieldsColor2);     
     else EbbeFlut.board.setBackground(move.fromX, move.fromY, Const.NORMAL_BACKGROUND);     
     
     peekCard=false;      
     if(move.isPossible())
     {  int i=move.doIt();
        
       if(i==Move.FINISH) System.out.println("a card is ins saeckl gehuepferlt");
       //a "wrong" edge move? is not longer possible to move directly
       //else if(i==Move.PROMPT) System.out.println("\"wrong\" move!Your PLACED card was removed!");
         
       if(move.fromX==Const.start1 || move.fromX==Const.start2)
         EbbeFlut.chronical.setStatus(Chronos.STARTCARD_PLACED);       
     }
       
     return true;     
   }
   
   /** @return true if the methode identify the click as 5x5 click,if chronical is placed
    */
   private boolean peekCard()
   { 
     if(a>=0 && a<5 && b>=0 && b<5)
     { firstCard=EbbeFlut.board.peek(a,b);       
       if(firstCard !=null && EbbeFlut.currentPlayer.isOwnerOf(firstCard))
       { peekCard=true;
         EbbeFlut.board.setBackground(a,b, Const.MARKE_COLOR);
         return true;
       }  
       firstCard=null;
     }
     else if((a==Const.start1 && b==Const.yAll && EbbeFlut.currentPlayer.no==Const.NO_1)
          || (a==Const.start2 && b==Const.yAll && EbbeFlut.currentPlayer.no==Const.NO_2))
     { firstCard=EbbeFlut.board.getStartStack(EbbeFlut.currentPlayer.no).peek();
       if(firstCard !=null && EbbeFlut.currentPlayer.isOwnerOf(firstCard))
       { EbbeFlut.board.setBackground(a,b, Const.MARKE_COLOR);
         peekCard=true;
         
         return true;
       }         
     }
     return false;     
   }   
   
  static private ActionListener menuAL;
   
  /** for communication between this class and EbbeFlut.MenuActionListener;
   *  if (al != null ) this class will fire to menuAL else it will make moves etc.
   */
  static public void fireToMenuAL(ActionListener al)
  { menuAL=al;
  }
  
}//class BoardButton A L        
  
