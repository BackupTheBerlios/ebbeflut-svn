/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * Move.java
 *
 * Created on 26. September 2004, 19:01
 */

/**
 *
 * @author  peter karich
 */
public class Move 
{   private Card card;
    public int fromY,fromX;
    Player owner;
    public int toX,toY;
    static private Board board;
    static public int NOTHING=0, FINISH=1, PROMPT=2, FIRST=3;
    
    /** Creates a new instance of Move;
     * you have to ensure that this coord are possible and there 
     * is a card in the right color
     */
    public Move(Card ca,int xTo,int yTo)
    { card=ca;
      fromX=card.getX();
      fromY=card.getY();
      this.owner=card.getOwner();
      toX=xTo;
      toY=yTo;
    }
    
    /** to set another board for ai -> to make calculating nonvisible
     */
    static public void setBoard(Board b)
    { board=b;      
    }
    
    public Card getCard()
    { return card;
    }
    
    static private boolean isStart1(int x,int y)
    { return x==4 && y==4 || x==4 && y==3 || x==3 && y==4;
    }
    
    static private boolean isStart2(int x,int y)
    { return x==0 && y==0 || x==0 && y==1 || x==1 && y==0;
    }
    
    public boolean isPossible()
    {  boolean possible=false;
       Card tmp;
      
      if(owner.no==Const.NO_1)
      { if(fromX==Const.start1 && fromY==Const.yAll) return isStart1(toX,toY);
        else if(toX==fromX && toY==fromY-1)
        { for(int m=0; m<5; m++)
          { if(m==fromX) continue;
            
            tmp=board.peek(m,fromY);
            if(tmp==null) continue;
            if(tmp.equals(getCard())) 
            { possible=true;
              break;
            }
          }  
        }
        else if(toX==fromX-1 && toY==fromY)
        { for(int n=0; n<5; n++)
          { if(n==fromY) continue;
            
            tmp=board.peek(fromX,n);
            if(tmp==null) continue;
            if(tmp.equals(getCard())) 
            { possible=true;
              break;
            }
          }
        }
      }
      else//no== NO_2
      { if(fromX==Const.start2 && fromY==Const.yAll) return isStart2(toX,toY);
        else if(toX==fromX && toY==fromY+1)
        { for(int m=0; m<5; m++)
          { if(m==fromX) continue;
            
            tmp=board.peek(m,fromY);
            if(tmp==null) continue;
            if(tmp.equals(getCard())) 
            { possible=true;
              break;
            }
          }
        }
        else if(toX==fromX+1 && toY==fromY)
        { for(int n=0; n<5; n++)
          { if(n==fromY) continue;
            
            tmp=board.peek(fromX,n);
            if(tmp==null) continue;
            if(tmp.equals(getCard())) 
            { possible=true;
              break;
            }
          }
        }
      }
       
      return possible;
    }
    
    int returnStatus;
    
    /** after calling this methode you can call takeBack,
      * but its your work to prevent clashs
     **/
    public int doIt()
    { Card tmp;      
      returnStatus=NOTHING;
      
      if(owner.no==Const.NO_1)
      { //start move, push only for pc moves!!! humans start move is handle in BoardButtonAl push5x5
        if(fromX==Const.start1)
        { board.push(toX,toY,board.getStartStack1().pop());         
          returnStatus=FIRST;
          return FIRST;          
        }
        tmp=board.pop(fromX,fromY);
        
        //move into finish
        if(this.isStart2(toX,toY))
        { board.getFinishStack1().push(tmp);
          returnStatus=FINISH;          
          return FINISH;
        }        
        //"wrong" move to the horizontal edge
        else if(toX==-1)
        { board.getRemovedStack1().push(tmp);
          returnStatus=PROMPT;          
          return PROMPT;
        }
        else if(toY==-1)
        { board.getRemovedStack1().push(tmp);
          returnStatus=PROMPT;          
          return PROMPT;          
        }
        //regular move        
        else board.push(toX, toY, tmp);        
      }
      else//if no==2
      { if(fromX==Const.start2)
        { board.push(toX,toY,board.getStartStack2().pop());
          returnStatus=FIRST;          
          return FIRST;          
        }             
        tmp=board.pop(fromX,fromY);
        
        if(isStart1(toX,toY))
        { board.getFinishStack2().push(tmp);
          returnStatus=FINISH;          
          return FINISH;
        }
        else if(toX==5)
        { board.getRemovedStack2().push(tmp);
          returnStatus=PROMPT;          
          return PROMPT;          
        }
        else if(toY==5)
        { board.getRemovedStack2().push(tmp);
          returnStatus=PROMPT;          
          return PROMPT;          
        }
        else board.push(toX, toY, tmp);        
      }
      return NOTHING;
    }
    
    /**be sure that you call doIt before this methode
     * is it better to give takeBack Move as argument??
     * @return the Chronos.status ! NOT the Move.doIt return values!!
     */
    public int takeBack()
    { Card tmp;
      
      if(owner.no==Const.NO_1)
      { if(isStart2(toX,toY))
         tmp=board.getFinishStack1().pop();
        else if(returnStatus==PROMPT)
         tmp=board.getRemovedStack1().pop();
        else
        { tmp=board.pop(toX,toY);
          if(fromX==Const.start1)
          { board.getStartStack1().push(tmp);
            return Chronos.POPPED;
          }
        }         
      }
      else //no ==2
      { if(isStart1(toX,toY))
         tmp=board.getFinishStack2().pop();
        else if(returnStatus==PROMPT)
         tmp=board.getRemovedStack2().pop();        
        else
        { tmp=board.pop(toX,toY);        
          if(fromX==Const.start2)
          { board.getStartStack2().push(tmp);
            return Chronos.POPPED;
          }
        }
      }

      if(owner.no==Const.NO_1 && fromX == Const.start1)      board.getStartStack1().push(tmp);
      else if(owner.no==Const.NO_2 && fromX == Const.start2) board.getStartStack2().push(tmp);
      else                                             board.push(fromX, fromY, tmp);
      
      return Chronos.PLACED;
    }
    
    static private Stack newMoves=new Stack(10,5);
    static private Move moveTmp;
    
    /** call move.doIt before calling this routine!
     */
    static public Stack getNewPossible(Stack old, Player player,Move move)
    {  boolean uncoveredH=false, uncoveredV=false, movedH=false,movedV=false;
       int moveDir=+1; // move direction for player two       
      if(player.no==Const.NO_1) moveDir=-1;
      
       Card uncoveredCard=null, tmpCard;       
       //is this move vertical?
       boolean verticalMove=move.fromY == move.toY; 
       
      //re use newMoves
      newMoves.clear();
      //System.out.println("sth goes wrong in Move.getNewPossible");
      //remove all impossible moves now, do Move.doIt before!!!
      for(int i=0; i < old.size(); i++)
      { moveTmp=(Move)old.elementAt(i);
        if(!moveTmp.isPossible()) newMoves.add(moveTmp);        
        //System.out.println(i+" "+moveTmp.toString());
      }
      
      //if move is a start move
      if(   (isStart1(move.toX,move.toY) && player.no==Const.NO_1)
         || (isStart2(move.toX,move.toY) && player.no==Const.NO_2))
      { //let both uncovered == false
        movedH=true;
        movedV=true;
      }
      //if move.to are "off board" values, or finish moves
      else
      { tmpCard=board.peek(move.fromX,move.fromY);
        if(player.isOwnerOf(tmpCard)) uncoveredCard=tmpCard;
      
        if( (isStart1(move.toX,move.toY) && player.no==Const.NO_2)
         || (isStart2(move.toX,move.toY) && player.no==Const.NO_1)
         || move.toX>=5 || move.toY>=5 || move.toX <0 || move.toY<0)
        { if(uncoveredCard!=null) { uncoveredH=true;  uncoveredV=true;  }
        }
        else
        { if(uncoveredCard!=null) 
          { uncoveredH=true;  uncoveredV=true;  
            if(verticalMove) movedV=true;
            else movedH=true;
          }
          else 
          { movedH=true; movedV=true;          
          }
        }
      }
      //push covered Card themself only for one time in the newMoves stack
       boolean firstHorizontal=true, firstVertical=true;       
           
      //calculate the moves for the uncovered card with location move.fromX,fromY
      if(uncoveredH || uncoveredV)
      { for(int m=0; m<5; m++)
        { if(uncoveredH)
          { //the real board "horizontal"
            if(move.fromX != m) // don't pick up the uncovered themself
            { tmpCard=board.peek(m, move.fromY);
              if(tmpCard!=null &&tmpCard.equals(uncoveredCard))
              { newMoves.push(new Move(tmpCard, m,move.fromY+moveDir));
                if(firstHorizontal) 
                    newMoves.push(new Move(uncoveredCard, move.fromX, move.fromY+moveDir)); //push it only one time
                firstHorizontal=false;
              }
            }
          }
          if(uncoveredV)
          { //vertical
            if(move.fromY != m)
            { tmpCard=board.peek(move.fromX, m);              
              if(tmpCard!=null &&tmpCard.equals(uncoveredCard))
              { newMoves.push(new Move(tmpCard, move.fromX+moveDir, m));              
                if(firstVertical) 
                    newMoves.push(new Move(uncoveredCard, move.fromX+moveDir, move.fromY));
                firstVertical=false;
              }
            }
          }
        }//for
      }
       
      if(movedH || movedV)
      { firstHorizontal=true;
        firstVertical=true;
        Card movedCard=move.getCard();
        
        //calculate the moves for the moved card with actual location move.toX,toY
        for(int m=0; m<5; m++)
        { //the real board "horizontal"
          if(movedH)
          { if(move.toX != m) // don't pick up the uncovered themself
            { tmpCard=board.peek(m, move.toY);          
              if(tmpCard!=null &&tmpCard.equals(movedCard))
              { newMoves.push(new Move(tmpCard, m,move.toY+moveDir));
                if(firstHorizontal) 
                    newMoves.push(new Move(movedCard, move.toX, move.toY+moveDir)); //push it only one time
                firstHorizontal=false;
              }
            }
          }
          if(movedV)
          { //vertical
            if(move.toY != m)
            { tmpCard=board.peek(move.toX, m);
              if(tmpCard!=null &&tmpCard.equals(movedCard))
              { newMoves.push(new Move(tmpCard, move.toX+moveDir, m));              
                if(firstVertical) 
                    newMoves.push(new Move(movedCard, move.toX+moveDir, move.toY));
                firstVertical=false;
              }
            }
          }
        }//for
      }
       
      return newMoves;
    }
    
  /** get all possible moves
   *  @return all possible moves as a stack of class Move
   */
    static public Stack getAllPossible(Player player)
    { Move move;
      Stack all=new Stack(10,5);
      int m,n;
      Card tmp;
      
      if(player.no==Const.NO_1)
      { //cancel all moves near the left and upper edge off board
        for(m=1; m<5; m++)
        { for(n=1; n<5; n++)
          { tmp=board.peek(m,n);
            if(tmp==null) continue;
            if(!player.isOwnerOf(tmp)) continue;
            move=new Move(tmp, m-1,n  );
            if(move.isPossible()) all.push(move);
            //move.toX=m; move.toY=n-1; //is the same as: 
            move=new Move(tmp, m,  n-1);          //but you have to "new Move" it
            if(move.isPossible()) all.push(move);
          }
        }
        //now look only for the possible edge moves
        //moves like card.withPos(0 1).equals(card.withPos(0 4)) are not possible
        m=0;
        for(n=2; n<5; n++)
        { tmp=board.peek(m,n);
          if(tmp==null) continue;
          if(!player.isOwnerOf(tmp)) continue;
          move=new Move(tmp, m,  n-1);
          if(move.isPossible()) all.push(move);
        }
        n=0;
        for(m=2; m<5; m++)
        { tmp=board.peek(m,n);
          if(tmp==null) continue;
          if(!player.isOwnerOf(tmp)) continue;
          move=new Move(tmp, m-1,n);
          if(move.isPossible()) all.push(move);           
        }
      }
      else //if no==2
      { for(m=0; m<4; m++)
        { for(n=0; n<4; n++)
          { tmp=board.peek(m,n);
            if(tmp==null) continue;
            if(!player.isOwnerOf(tmp)) continue;
            move=new Move(tmp, m+1,n);
            if(move.isPossible()) all.push(move);
            move=new Move(tmp, m,  n+1);
            if(move.isPossible()) all.push(move);
          }
        }
        m=4;
        for(n=0; n<3; n++)
        { tmp=board.peek(m,n);
          if(tmp==null) continue;
          if(!player.isOwnerOf(tmp)) continue;
          move=new Move(tmp, m,  n+1);
          if(move.isPossible()) all.push(move);           
        }
        n=4;
        for(m=0; m<3; m++)
        { tmp=board.peek(m,n);
          if(tmp==null) continue;
          if(!player.isOwnerOf(tmp)) continue;
          move=new Move(tmp, m+1,n);
          if(move.isPossible()) all.push(move);           
        }
      }       
      return all;
    }
    
    /** how much cards are covered by the opponent?
     *  count them and the covered cards of the oppenent
     *  @return opponent - own covered cards
     */
    static public int getCoveredAssessment(Player player)
    {  int own,foreign;
       Card tmp;
     
      if(player.no==Const.NO_1)
      { own=Const.NC-board.getFinishStack1().getSize()-
            board.getStartStack1().getSize()-board.getRemovedStack1().getSize();
        foreign=Const.NC-board.getFinishStack2().getSize()-
                board.getStartStack2().getSize()-board.getRemovedStack2().getSize();
      }
      else
      { foreign=Const.NC-board.getFinishStack1().getSize()-
                board.getStartStack1().getSize()-board.getRemovedStack1().getSize();
        own=Const.NC-board.getFinishStack2().getSize()-
            board.getStartStack2().getSize()-board.getRemovedStack2().getSize();
      }
       
      //subtract all visible cards => covered
      for(int m=0; m<5; m++)
      { for(int n=0; n<5; n++)
        { tmp=board.peek(m,n);
          if(tmp==null) continue;
          if(player.isOwnerOf(tmp)) own--;
          else foreign--;
        }
      }      
      
      //own covers are negative, foreign are good
      return foreign-own;
    }
    
    /** how much fields of the 3 starter fields are empty
     */ 
    static public int getFreeStartAssessment(Player player)
    {  int own=0;
       
      if(player.no==Const.NO_1)
      { if(board.peek(4,4)==null) own++;
        if(board.peek(4,3)==null) own++;
        if(board.peek(3,4)==null) own++;        
      }
      else
      { if(board.peek(0,0)==null) own++;
        if(board.peek(0,1)==null) own++;
        if(board.peek(1,0)==null) own++;        
      }
       
      return own;
    }
    
    public String toString()
    { return card.toString()+" from "+fromX+","+fromY+" to "+toX+","+toY;
    }
}
