/***************************************************************************
 * Created on 20. September 2004, 12:57                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 */

package source.main;

import source.inet.*;
import source.gui.*;
import source.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.*;
import java.net.*;

/** if you use netbeans then change the working directory to see the images under  *  tools-options-debuggingAndexecution-excecutiontype-externalexc-expert-workingdir;
 *  the same for debuggingAndExec-debuggertypes-defaultdebugging-expert--workingdir;
 *  but 
 * @author  Peter Karich
 */
public class EbbeFlut extends Frame 
{ private Client client;
  private Server server;
  private INet inet=null;
  private MainMenuBar mainMenu;
  static public Random rand=new Random();// |CHEAT|   
  static public GraphicBoard board;
  static public Chronos chronical;
  static public Player currentPlayer;
  static public Player player1, player2;  
        
  static public void main(String arg[])
  { try
    { new EbbeFlut();
    }
    catch(Exception exc)
    { exc.printStackTrace();
      System.out.println(EbbeFlut.board.toString());
    }
  }

  public EbbeFlut()
  { Const.initSettings();
    Const.printSettings();
    if(Const.VISUALISATION)
    { setMenuBar(mainMenu=new MainMenuBar(this));
      setTitle("Ebbe und Flut. Fuer Jule.");
      setSize(Const.windowSize);
      setLocation(Const.windowLocation);
      if(!Const.OS_IS_ZAURUS)
      { Image ii=Toolkit.getDefaultToolkit().getImage(Const.ebbeFlutPictureFile);
        setIconImage(ii);
      }       
      addWindowListener(new Close());
    }
    start();
  }     
  
  
  
  public void start()
  { SettingsDialog settings;
    
    for(int i=0; i!=Const.EXIT_NUMBER; i++)
    { //init player information if no connection to the internet
      if(inet==null)
      { //saved settings?
        player1=SettingsDialog.readSettings(Const.NO_1);
        //if not
        if(player1==null)
        { //standard values to init SettingsDialog
          player1=new HumanPlayer(Const.COLOR1, "peter1",1);
          getSettingsDialog(Const.NO_1);
        }
        
        player2=SettingsDialog.readSettings(Const.NO_2);
        if(player2==null)
        { player2=new AI(Const.COLOR2,"ai",2);
          getSettingsDialog(Const.NO_2);
        }        
      }
      board=new GraphicBoard(new BoardButtonAL(this),player1,player2);
      Move.setBoard(board);

      if(Const.VISUALISATION) 
      {  //refresh visualisation
         removeAll();
         add(board.getPanel());
      } 

      boolean swap=false;
      chronical=new Chronos();

      if(Const.VISUALISATION) this.setVisible(true);      
      System.out.println("new game");
      
      //|CHEAT| THE GAME TO TEST IT
      /*
      
      for(int i=0; i<14; i++)
      {    board.getStartStack1().nextCard();
           chronical.nextMove();
           board.getStartStack2().nextCard();
           chronical.nextMove();
      }
      swap=true;
      board.getStartStack2().pushInitial(new Card('a',5,player2));
      board.push(0,0,new Card('a',4,player2));
      board.push(1,0,new Card('b',5,player2));
      board.push(4,0,new Card('b',2,player1));
      board.push(3,1,new Card('a',2,player2));
      board.push(2,1,new Card('b',4,player2));
      board.push(2,1,new Card('b',4,player1));
      board.push(2,1,new Card('a',3,player2));
      board.push(1,1,new Card('a',1,player1));
      board.push(2,2,new Card('a',5,player1));
      board.push(4,3,new Card('a',3,player1));
      board.push(3,3,new Card('b',5,player1));
      board.push(1,4,new Card('b',3,player1));
     
      */
      //--------MAIN LOOP----------------------------------------------------
      Path path=new Path();
      
      while(!chronical.isGameFinished())
      { if(swap)  currentPlayer=player2;        
        else      currentPlayer=player1;                  
        showPath(path,currentPlayer);
    
        chronical.newTurn(chronical.fromBeginning());
        
        path=currentPlayer.nextTurn(path);
        
        //FIXME??: 13.02.05 add following line:
        if(chronical.isGameFinished()) break;
        
        board.setStartStackLabel("Ready!",currentPlayer.no);
    
        //send turn to foreignplayer
        if(inet!=null && !(currentPlayer instanceof InternetPlayer))
        { System.out.println("EbbeFlut.loop.sendTurn");
          inet.sendTurn(path);
        }
            
        if(chronical.fromBeginning())
        { chronical.nextMove();
          if(gameIsFinished(currentPlayer)) break;
        }
        //if this currentplayer found a move -> currentPlayers has not moved, so take back in Chronos
        else chronical.prevMove();        
        swap=!swap;
      }
      this.setVisible(false);      
    }//forloop
  }
  
  private boolean gameIsFinished(Player currentPlayer)
  { 
    //if this player or his opponnent has moves
    if( Move.getAllPossible(getOtherPlayer(currentPlayer)).size()==0  
        && Move.getAllPossible(currentPlayer).size()==0 
        && board.getStartStack1().getSize()==0 
        && board.getStartStack2().getSize()==0 
        && board.getStartStack1().peek()==null 
        && board.getStartStack2().peek()==null)
     {  String tmpString;
         
       if(board.getFinishStack1().getSize()==board.getFinishStack2().getSize()) 
         tmpString="There is no winner!";
             
       else if(board.getFinishStack1().getSize()
                >board.getFinishStack2().getSize()) 
         tmpString="The winner is player1: "+player1.getName();
       else  tmpString="The winner is player2: "+player2.getName();

       System.err.println(tmpString+" player one="
           +board.getFinishStack1().getSize()
           +"; player two="+board.getFinishStack2().getSize());
       PDialog winnerDialog=new PDialog(this, tmpString, true, "ok");
       winnerDialog.setVisible(true);
       return true;
     }
    return false;
  }
  
  private void showPath(Path path,Player pl)
  { //------show that the other player found a forgotten move => fromBeginning==false
    if(path.getSize()==1 && path.getElement(0)==Const.doYourWorkMove)
    { chronical.setTheOtherPlayerIsNotReady();
      board.setStartStackLabel("Do your work "+pl.getName()+"!",pl.no);
      board.getFinishStack(pl.no).pop();
    }
    else
    { board.getStartStack(pl.no).nextCard();
      //overwrite the counter with a good message
      board.setStartStackLabel("Click me "+pl.getName()
                                +"! turn no:"+chronical.getMoveNo(), pl.no);
    }
  }
   
  /** if(internet==null) no interpartner needed
   */
  public void setINet(INet internet)
  { inet=internet;    
  }
  
  static public Player getOtherPlayer(Player player)
  { if(player.no==Const.NO_1) return player2;
    else return player1;
  }
  
  public void exit()
  { if(Const.REMOVE_TMP_FILES && Const.errFile!=null && Const.logFile!=null)
    { Const.errFile.delete();
      Const.logFile.delete();
    }   
    System.exit(0);
  }
 
  private SettingsDialog playerSettings;    
  /** fires an action event to the main menu bar
   */
  public void getSettingsDialog(int noOfPlayer)
  { if(Const.NO_1 == noOfPlayer)
    { playerSettings=new SettingsDialog(this,player1);
    }
    else if(Const.NO_2 == noOfPlayer)
    { playerSettings=new SettingsDialog(this,player2);
    }
    else if(Const.DEBUG) throw new Error("Illegel Argument in EbbeFlut.getSettingsDialog");
    
    playerSettings.setVisible(true);
    if(playerSettings.saved())
    { if(Const.NO_1 == noOfPlayer)
      { player1=playerSettings.getPlayer();
      }
      else if(Const.NO_2 == noOfPlayer)
      { player2=playerSettings.getPlayer();
      }
    
      board.setPlayer(player1,player2);
    } 
  }
  
  /** how to call "this" in a sub class? like EbbeFlut.Action
   */
  //private EbbeFlut getThis()  { return this;  }
  
  static public int nextRandomInt()
  { return Math.abs(rand.nextInt());
  }
  
  public void setPlayerButtonsEnabled(boolean t)
  { mainMenu.setPlayerButtonsEnabled(t);
  }
  
  class Close extends WindowAdapter
  { public void windowClosing(WindowEvent e)
    { exit();
    }
  }
 }
