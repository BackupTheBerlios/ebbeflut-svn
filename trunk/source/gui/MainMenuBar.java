/*
 * MenuAction.java
 *
 * Created on 29. Januar 2005, 00:16
 */

package source.gui;

import source.main.*;
import source.util.Const;
import source.inet.*;

import java.awt.event.*;
import java.awt.*;

/**
 *
 * @author  peter
 */
 public class MainMenuBar extends MenuBar
 { ActionListener al;
   MenuItem player1MenuI;
   MenuItem player2MenuI;
   
   public void actionPerformed(ActionEvent ae)
   { al.actionPerformed(ae);
   }
   
    public MainMenuBar(EbbeFlut ebe)
    {   Menu m;
      
      al=new MenuAction(ebe);        
      
      m=new Menu("Game");
      addItem(m, "New", al);
      m.addSeparator();
      addItem(m, "Exit", al);
      add(m);
      
      m=new Menu("Settings");
      player1MenuI=new MenuItem("player1");
      player2MenuI=new MenuItem("player2");
      addItem(m, "player2", al, player2MenuI);
      addItem(m, "player1", al, player1MenuI);
      add(m);
      
      m=new Menu("Card");
      addItem(m, "show stack", al);
      addItem(m, "remove", al);      
      add(m);
      
      m=new Menu("Files"); 
      addItem(m, "show log file", al); 
      addItem(m, "show error file", al); 
      add(m); 
    
      m=new Menu("Info"); 
      addItem(m, "gpl", al);       
      addItem(m, "author", al);       
      addItem(m, "program", al);       
      add(m); 
      
      m=new Menu("iNet"); 
      addItem(m, "create server", al);       
      addItem(m, "connect to server", al);
      addItem(m, "show INet window",al);
      add(m);      
    }
    
    public void addItem(Menu m, String name, ActionListener l)
    {  MenuItem mi=new MenuItem(name);
         
      mi.setActionCommand(name);
      mi.addActionListener(l);
      m.add(mi);
    }
    
    public void addItem(Menu m, String name, ActionListener l,MenuItem mi)
    { mi.setActionCommand(name);
      mi.addActionListener(l);
      m.add(mi);
    }
    
    public void setPlayerButtonsEnabled(boolean t)
    { player1MenuI.setEnabled(t);
      player2MenuI.setEnabled(t);      
    }
 }//MainMenuBar
 
 class MenuAction implements ActionListener
 {  private TextFileView authorText, programText, gpl, logText, errText;
    private PDialog showStack, removeCard, noConnectionWarning;
    private EbbeFlut ebbe;
    private Server server;
    private Client client;
    private String ip;
    private INet inet;
    private PDialog newGameDialog;
  
     public MenuAction(EbbeFlut ebe)
     { ebbe=ebe;
     }
     
     public void actionPerformed(ActionEvent ae)
     {  String cmd= ae.getActionCommand();
        
       if(cmd.equals("Exit")) ebbe.exit();
       else if(cmd.equals("New"))  
       { if(newGameDialog==null)
             newGameDialog=new PDialog(ebbe,"Would you like to start a new game?", true,"yes_no");    
         newGameDialog.setVisible(true);
         if(newGameDialog.getReturnStatus().equals(PDialog.yes))
          ebbe.chronical.setGameIsFinished();         
       }
       /*else if(cmd.equals("back")) //it is useful but so you can see whats under the cards!
       { if(chronical.placed() && currentPlayer.getColor()==move.getCard().getColor())
         { chronical.setStatus(move.takeBack());
           System.out.println("Take back the last move from current player!");
         }
       }*/
       else if(cmd.equals("author")) 
       { if(authorText==null) authorText=new TextFileView(ebbe,Const.authorInfoFile);
         authorText.setVisible(true);
       }
       else if(cmd.equals("program")) 
       { if(programText==null) programText=new TextFileView(ebbe,Const.programInfoFile);
         programText.setVisible(true);
       }
       else if(cmd.equals("gpl")) 
       { if(gpl==null) gpl=new TextFileView(ebbe,Const.gplFile);
         gpl.setVisible(true);
       }
       else if(cmd.equals("show log file")) 
       { if(Const.logFile!=null)
         { logText=new TextFileView(ebbe,Const.logFile.getPath());
           logText.setVisible(true);
         }
       }
       else if(cmd.equals("show error file"))  
       { if(Const.errFile!=null) 
         { errText=new TextFileView(ebbe,Const.errFile.getPath()); 
           errText.setVisible(true); 
         } 
       }
       else if(cmd.equals("player1")) 
       { ebbe.getSettingsDialog(Const.NO_1);
       }
       else if(cmd.equals("player2")) 
       { ebbe.getSettingsDialog(Const.NO_2);
       }
       else if(cmd.equals("show stack")) 
       { if(showStack==null) 
            showStack=new PDialog(ebbe,"Click the stack you want to see!",
                                  true,"ok_cancel");
         showStack.setVisible(true);
         if(showStack.getReturnStatus().equals(PDialog.ok))   BoardButtonAL.fireToShowStack();
       }
       else if(cmd.equals("remove")) 
       { if(removeCard==null) 
            removeCard=new PDialog(ebbe,"Click the card you want to move"
                                   +" out of board!",true,"ok_cancel");
         removeCard.setVisible(true);
         if(removeCard.getReturnStatus().equals(PDialog.ok)) BoardButtonAL.fireToRemove(); 
       }
       else if(cmd.equals("create server")) 
       { if(server!=null) return;
         //create a Server and than connect youself to this server
         server=new Server();
         ip=server.getHostName();
         new Thread(server).start();
         actionPerformed(new ActionEvent(this,0,"connect to server"));
         //client=server.createAndConnectAClient(ebbe);         
       }
       else if(cmd.equals("show INet window"))
       { if(client==null) 
         { if(noConnectionWarning==null)
              noConnectionWarning=new PDialog(ebbe,"sorry you are not connected"
                                        ,true,"ok");
           noConnectionWarning.setVisible(true);
           return;
         }
         inet.setVisible(true);         
       }
       else if(cmd.equals("connect to server")) 
       { if(client!=null) return;
         String ip="localhost";
         if(!Const.DEBUG) throw new Error("input ip instead of <locahost>");
         
         inet=new INet(ebbe);
         inet.setVisible(true);
         ebbe.setINet(inet);
         //ip=inet.getTypedIp(); instead of ip=localhost;         
         client=new Client(ip,inet);
         
         //lokale adresse bei kppp statistik 145.254.239.215
       }
       
       
     }     
  }//MenuAction
 
 