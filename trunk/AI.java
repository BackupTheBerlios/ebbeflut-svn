/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * AI.java
 *
 * Created on 29. September 2004, 22:45
 */

/**
 * Artificial "Intelligence" :-)
 * @author  peter karich
 */
public class AI extends Player
{   private Board board;
    private MyTimer timer;
    private long waitMillis=500;
    private int playBackLoop=0;
    
    /** Creates a new instance of PcPlayer */
    public AI(java.awt.Color color,String name, int no) 
    { super(Const.AI_PLAYER, color, name, no);
    }
    
    /** @param fromBeginning hope the pc wont need the case fromBeginning==false :-)
     */
    public void moves()
    { 
      //test wether all possible moves are done from the other player
      if(Move.getAllPossible(EbbeFlut.getOtherPlayer(this)).size()>0) 
      { EbbeFlut.chronical.setTheOtherPlayerIsNotReady();        
        return;
      }
      
      System.out.println("\n"+getName()+"---------------");
      //interrupt the AI if the methode calc is recursing too long
      timer=new MyTimer(60000);      
      timer.start();
       
       Path path,retPath;            
       Card startCard;       
      
      board=EbbeFlut.board.getBoard();
      //before we can start => change the board from visible to an unvisible one:
      Move.setBoard(board);             
      startCard=board.getStartStack(this.no).peek();
        
      if(EbbeFlut.chronical.lastMoves() || !EbbeFlut.chronical.fromBeginning()) 
        retPath=calcWithNoPopping();
      else if(no==Const.NO_1)
      { retPath=calc(new Move(startCard,4,4),new Path());
        path=calc(new Move(startCard,4,3),new Path());
        if(path.getAssessment()>retPath.getAssessment())
        { retPath=path;
        }
        path=calc(new Move(startCard,3,4),new Path());
        if(path.getAssessment()>retPath.getAssessment())
        { retPath=path;
        }
      }
      else
      { retPath=calc(new Move(startCard,0,0),new Path());
        path=calc(new Move(startCard,0,1),new Path());
        if(path.getAssessment()>retPath.getAssessment())
        { retPath=path;
        }
        path=calc(new Move(startCard,1,0),new Path());
        if(path.getAssessment()>retPath.getAssessment())
        { retPath=path;
        }
      }
      if(!timer.isActive()) System.out.println("Wow! Move length is "+retPath.getSize());
      timer.deactivate();
       
      //System.out.println("his assessment is: "+retPath.getAssessment());
      for(int i=0; i<retPath.getSize(); i++)
        System.out.println(retPath.getElement(i).toString());
            
      //set/get back the real board 
      Move.setBoard(EbbeFlut.board);
      for(int i=0; i<retPath.getSize(); i++)
      { 
        loop(retPath,playBackLoop,i);
        retPath.getElement(i).doIt();
        if(playBackLoop>0) myWait(waitMillis);        
      }        
    }
    
    private void myWait(long millis)
    { try
      { Thread.sleep(millis);
      }
      catch(InterruptedException ie)
      { System.out.println("sth goes wrong with the sleep operation");
      }
    }
    
    private void loop(Path retPath,int playBack,int i)
    { for(int counter=0; counter<playBack; counter++)
      { retPath.getElement(i).doIt();
        myWait(waitMillis);
        retPath.getElement(i).takeBack();        
        myWait(waitMillis);                
      }
    }
    /** the assessment of a path
     */
    public int getAssess(int doItMoveRet,int oldPathSize)
    {  int assess=0;
       
      if(doItMoveRet==Move.PROMPT) assess=-60;
       
      assess+=3*(oldPathSize-1)+60*board.getFinishStack(this.no).getSize()              
              -60*board.getRemovedStack(this.no).getSize()              
              +2*Move.getCoveredAssessment(this)+Move.getFreeStartAssessment(this);
      
      return assess;
    }
    
    /*private Path calcWithPopping(Move move,Path p)
    {  Stack all=new Stack(); 
       
      all.push(move);
      return calc(p,all,0);
    }*/
    
    /** calculates the moves without popping at the beginning
     *  this methode will call calc()
     */
    public Path calcWithNoPopping()
    {  Path retPath=new Path(),newPath,oldPath=new Path();
       int ass=getAssess(Move.NOTHING, 0);
       Stack all=Move.getAllPossible(this);             
      
      oldPath.setAssessment(ass);
      retPath.setAssessment(ass);              
      
      for(int i=0; i < all.size() && timer.isActive(); i++)
      { newPath=calc((Move)all.elementAt(i),oldPath); 
        if(newPath.getAssessment()>retPath.getAssessment())      retPath=newPath;            
      }
      
      return retPath;    
    }
    
    /** recursive calculation of: the moves = one path
     */
    public Path calc(Move move,Path oP)
    {  Path oldPath=oP.getClone();
       int ret=move.doIt();
      
      Stack all=Move.getAllPossible(this);   
      oldPath.push(move);
      
      if(all.empty()) 
      { oldPath.setAssessment( getAssess(ret,oldPath.getSize()) ); 
        move.takeBack();
        return oldPath;
      }
      
       Path newPath,retPath;
      
      //iter has next cause all is not empty!
      retPath=calc((Move)all.elementAt(0),oldPath);
      for(int i=1; i < all.size() && timer.isActive(); i++)
      { newPath=calc((Move)all.elementAt(i),oldPath);
        if(newPath.getAssessment()>retPath.getAssessment())
        { retPath=newPath;
        }    
      }      
      
      move.takeBack();       
      return retPath;
    }//calc
    
     
  /** one turn may consist of many moves, so save them in path
    */
  class Path
  {  private int assessment; //bewertung? |GERMAN|
     private Stack nextMoves=new Stack(5,5);
   
    public Path()
    {
    }
   
     public int getAssessment()
     { return assessment;
     }
   
     public void setAssessment(int a)
     { assessment=a;
     }
   
     public void push(Move move)
     { nextMoves.push(move);
     }
   
     public Move elementAt(int i)
     { return (Move)nextMoves.elementAt(i);
     }
   
     public Move getElement(int i)
     {  return (Move)nextMoves.elementAt(i);
     }
   
     public int getSize()
     { return nextMoves.size();
     }
   
     public int size()
     { return nextMoves.size();
     }
   
     public Path getClone()
     { Path p=new Path();
       p.nextMoves=(Stack)nextMoves.clone();
       p.assessment=assessment;
       return p;
     }
   }
  
  public String getPlayerName()
  { return super.getName();
  }
 
   /** if calculation time of AI will increase too much
    */
   class MyTimer extends Thread
   {   long millis;
       boolean active;
       long realTime;
       
      public MyTimer(long milli)
      { super();
        millis=milli;
        active=true;
        realTime=System.currentTimeMillis();
        setPriority(Thread.MIN_PRIORITY);
      }
      
      public void run() 
      { int loopCounter=60;
        try
        { for(int a=0; a<loopCounter && active; a++)
           Thread.sleep(millis/loopCounter);         
        }
        catch(InterruptedException ie)
        { System.out.println("sth goes wrong with the sleep operation");
        }
        if(active) 
        { System.out.println(getName()+" was interrupted by myTimer! too long thinking :-) !");
          deactivate();
        }        
        //System.out.println(getName()+" ends with "+(double)(System.currentTimeMillis()-realTime)/1000+" sec");              
      }      
      
      public boolean isActive()
      { return active;
      }
      
      /** if there is no need to interrupt, because AI is already ready
       */
      public void deactivate()
      { active=false;        
        System.out.println(getName()+" needed "+(double)(System.currentTimeMillis()-realTime)/1000+" sec");
      }
   }
}