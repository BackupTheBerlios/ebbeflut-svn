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

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/** if you use netbeans then change the working directory to see the images under 
 *  tools-options-debuggingAndexecution-excecutiontype-externalexc-expert-workingdir;
 *  the same for debuggingAndExec-debuggertypes-defaultdebugging-expert--workingdir;
 * @author  Peter Karich
 */
public class EbbeFlut extends Frame 
{
  static private PDialog newGameDialog;
  
  //if you init the constructor with long value, than you can "play god"|CHEAT|
  static private Random rand=new Random(60);
  static private BoardButtonAL actionListener=new BoardButtonAL();
  
  static public GraphicBoard board;
  static public Chronos chronical;
  static public Player currentPlayer;
  static public Player player1, player2;  
        
  static public void main(String arg[])
  { new EbbeFlut();
  }

  public EbbeFlut()
  { //init the static values
    new Const();
    setMenuBar(new MenuInit());
    setTitle("Ebbe und Flut. Fuer Jule.");
    setSize(Const.windowSize);
    setLocation(Const.windowLocation);
    if(!Const.zaurusOS)
    { Image ii=Toolkit.getDefaultToolkit().getImage(Const.ebbeFlutPictureFile);
      setIconImage(ii);
    }      
    newGameDialog=new PDialog(this,"Would you like to start a new game?", true,"yes_no");    
    addWindowListener(new Close());
    start();       
  }     
  
  private void start()
  {  SettingsDialog settings;
     Player player;
    
    //------------a loop for new game option-------------
    while(true)
    { 
      player=SettingsDialog.readSettings(Const.NO_1);
      if(player==null)
      { //standard values to init SettingsDialog
        player=new HumanPlayer(Const.COLOR1, "peter1",1);
      
        settings=new SettingsDialog(this,player,true);
        settings.setVisible(true);
        player1=settings.getPlayer();
      }
      else player1=player;
      
      player=SettingsDialog.readSettings(Const.NO_2);
      if(player==null)
      { player=new AI(Const.COLOR2,"ai",2);

        settings=new SettingsDialog(this,player,true);
        settings.setVisible(true);
        player2=settings.getPlayer();
      }
      else player2=player;
      
      board=new GraphicBoard(new BoardButtonAL(),player1,player2);
      Move.setBoard(board);
      
      removeAll();
      add(board.getPanel());
    
       boolean swap=false;
      chronical=new Chronos();
      this.setVisible(true);      
      
      /*|CHEAT| THE GAME TO TEST IT
      board.push(1,3,new Card('e',3,player1));
      board.push(0,3,new Card('c',3,player1));
      swap=true;
     
      for(int i=0; i<24; i++)
      {    board.getStartStack1().pop();
           chronical.nextMove();
           chronical.nextMove();
      }
      board.getStartStack1().pop();
      chronical.nextMove();           
 
      board.push(0,3,new Card('c',5,player2));
      board.push(1,3,new Card('a',5,player2));
      board.push(1,4,new Card('b',5,player2));
      
 */     
      //--------!game loop!----------------------------------------------------
      while(true)
      { if(swap)  currentPlayer=player2;        
        else      currentPlayer=player1;
          
         //------show that the other player found a forgotten move => fromBeginning==false
        if(!chronical.fromBeginning())
        { board.setStartStackLabel("Do your work "+currentPlayer.getName()+"!",currentPlayer.no);
          board.getFinishStack(currentPlayer.no).pop();
        }
        else
        { board.setStartStackLabel("Click me "+currentPlayer.getName()
                                +"! turn no:"+chronical.getMoveNo(),currentPlayer.no);
        }
        
        currentPlayer.moves();
        
        if(chronical.fromBeginning())
        { chronical.nextMove();
          
          if(chronical.isGameFinished())
          { //if this player or his opponnent has forgotten moves
            if(Move.getAllPossible(getOtherPlayer(currentPlayer)).size()>0
               || Move.getAllPossible(currentPlayer).size()>0)
              chronical.setLastMoves(true);
            else
            {  String winner;
          
              if(board.getFinishStack1().getSize()==board.getFinishStack2().getSize())
              { board.setStartStack1Label("There is no winner!");
                board.setStartStack2Label("There is no winner!");              
              }
              else if(board.getFinishStack1().getSize()>board.getFinishStack2().getSize())                
                board.setStartStack1Label("The winner is player1: "+player1.getName());              
              else
                board.setStartStack2Label("The winner is player2: "+player2.getName());
               
              break;
            }
          }
        }
        //if this currentplayer found a move -> currentPlayers has not moved, so take back in Chronos
        else chronical.prevMove();
        
        swap=!swap;
      }              
      //--------!game loop!----------------------------------------------------
      
      //|CHEAT|
      System.exit(0);     
      newGameDialog.setVisible(true);
      if(!newGameDialog.getReturnStatus().equals(PDialog.yes))
      { System.exit(0);
      }            
      this.setVisible(false);
    }//while
  }       
  
  static public Player getOtherPlayer(Player player)
  { if(player.no==Const.NO_1) return player2;
    else return player1;
  }
   
  class Action implements ActionListener
  {  SettingsDialog playerSettings;
     TextFileView germanText,englishText,gpl;
     
     public void actionPerformed(ActionEvent ae)
     {  String cmd= ae.getActionCommand();
       if(cmd.equals("Exit")) System.exit(0);
       else if(cmd.equals("New"))  chronical.setGameIsFinished();
       /*else if(cmd.equals("back")) //it is useful but so you can see whats under the cards!
       { if(chronical.placed() && currentPlayer.getColor()==move.getCard().getColor())
         { chronical.setStatus(move.takeBack());
           System.out.println("Take back the last move from current player!");
         }
       }*/
       else if(cmd.equals("german")) 
       { if(germanText==null) germanText=new TextFileView(getThis(),Const.germanInfoFile);
         germanText.setVisible(true);
       }
       else if(cmd.equals("english")) 
       { if(englishText==null) englishText=new TextFileView(getThis(),Const.englishInfoFile);
         englishText.setVisible(true);
       }
       else if(cmd.equals("gpl")) 
       { if(gpl==null) gpl=new TextFileView(getThis(),Const.gplFile);
         gpl.setVisible(true);
       }
       else if(cmd.equals("player1")) 
       { playerSettings=new SettingsDialog(getThis(),player1,true);
         
         playerSettings.setVisible(true);
         player1=playerSettings.getPlayer();
         board.setPlayer(player1,player2);
       }
       else if(cmd.equals("player2")) 
       { playerSettings=new SettingsDialog(getThis(),player2,true);
         
         playerSettings.setVisible(true);
         player2=playerSettings.getPlayer();
         board.setPlayer(player1,player2);
       }        
     }     
  }
  
  /** how to call this in a sub class? like EbbeFlut.Action
   */
  private EbbeFlut getThis()
  { return this;
  }
  
  static public int nextRandomInt()
  { return Math.abs(rand.nextInt());
  }
  
  class MenuInit extends MenuBar
  { ActionListener al=new Action();
    
    public MenuInit()
    {  Menu m;
      
      m = new Menu("Game");
      addItem(m, "New", al);
      m.addSeparator();
      addItem(m, "Exit", al);
      add(m);
      
      m = new Menu("Settings");
      addItem(m, "player2", al);
      addItem(m, "player1", al);      
      add(m);
      
      
      m = new Menu("Info");
      addItem(m, "german", al);
      addItem(m, "english", al);
      addItem(m, "gpl", al);      
      add(m);
    }
      
    public void addItem(Menu m, String name, ActionListener l)
    {  MenuItem mi=new MenuItem(name);
         
      mi.setActionCommand(name);
      mi.addActionListener(l);
      m.add(mi);
    }
  }//MenuInit
    
  class Close extends WindowAdapter
  { public void windowClosing(WindowEvent e)
    { System.exit(0);
    }
  }
  
}