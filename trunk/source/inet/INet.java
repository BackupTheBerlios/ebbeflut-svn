/*
 * INet.java
 *
 * Created on 12. Februar 2005, 15:31
 */

package source.inet;

import source.gui.*;
import source.main.*;
import source.util.Const;

import java.awt.event.*;
import java.awt.*;

import java.net.*;
import java.io.*;

/** the menu for choosing your opponent <br> 
 *  the connection between Client and EbbeFlut
 *
 * @author  peter
 */
public class INet extends MyDialog
{  private Button ask=new Button("Ask"), play=new Button("Play"), 
           cancelButton=new Button(MyDialog.cancel),
           settings=new Button("Player Settings");
           
   private List   all=new List(), wannaPlay=new List();
   private TextArea chatWindow=new TextArea();
   private TextField textIn=new TextField(80);
   private Client client;
   private String labelText="My Name Is ";
   private Label label=new Label();
   private Player player;
   private InternetPlayer foreignPlayer;
   private INetAction action=new INetAction();
   private EbbeFlut ebbe;
   
   public INet(EbbeFlut owner)
   { //do not block the main frame EbbeFlut
     super(owner, false);
     this.ebbe=owner;
     player=EbbeFlut.player1;
     
     setLocation(ebbe.getLocation());
     
     //--------------------------------------------------
     settings.addActionListener(action);
     ask.addActionListener(action);
     cancelButton.addActionListener(new INetAction());
     play.addActionListener(new INetAction());     
      Panel panelR=new Panel(), panelL=new Panel();
     panelL.setLayout(new BorderLayout());
     panelR.setLayout(new BorderLayout());     
     panelL.add(all, BorderLayout.CENTER);
     panelL.add(ask, BorderLayout.SOUTH);     
     panelR.add(wannaPlay,BorderLayout.CENTER);
     panelR.add(play, BorderLayout.SOUTH);     
     
      Panel mainPanel=new Panel();     
     mainPanel.setLayout(new BorderLayout());//new GridLayout(1,2));
     mainPanel.add(label, BorderLayout.NORTH);
     mainPanel.add(panelL,BorderLayout.CENTER);
     mainPanel.add(panelR,BorderLayout.EAST);
     mainPanel.add(settings,BorderLayout.SOUTH);
     
     //----------------------------------------------------
     textIn.addKeyListener(new KeyL());
      Panel panelS=new Panel();
     panelS.setLayout(new BorderLayout());
     panelS.add(cancelButton, BorderLayout.NORTH);
     chatWindow.setEditable(false);
     panelS.add(chatWindow, BorderLayout.CENTER);
     panelS.add(textIn, BorderLayout.SOUTH);
     //----------------------------------------------------
     
     setLayout(new GridLayout(2,1));
     add(mainPanel,BorderLayout.CENTER);
     add(panelS,BorderLayout.SOUTH);
     addWindowListener(new Close());
     pack();
     //setVisible(true);         
   }
 
   private void doHide()
   { setVisible(false);
   }      
   
   private PDialog newGameDialog; 
  
   public void startGame(InternetPlayer foreign, String idFrom)
   { if(newGameDialog==null) newGameDialog=new PDialog(getThis()
                            ,"Start new game over internet!", true,"ok");
     foreignPlayer=foreign;
     
     //START new Game
     if(player.no==Const.NO_1)
     {   EbbeFlut.player1=player;
         EbbeFlut.player2=foreignPlayer;
       
        newGameDialog.setVisible(true);
        if(!newGameDialog.getReturnStatus().equals(PDialog.ok))
        { if(Const.DEBUG) throw new Error("error in INet startGame");
        }
        this.setVisible(false);
        randomLong=Math.abs(EbbeFlut.rand.nextLong());
        System.out.println("send startGame");
        client.startGame(randomLong);
        finalStart();
     }
     //WAITING for start
     else
     { EbbeFlut.player1=foreignPlayer;
       EbbeFlut.player2=player;
       toChatWindow(client.getID(), "waiting");
       //gets the random value from the foreign player, go back to reading
     }     
   }
   
   private void finalStart()
   { EbbeFlut.rand=new java.util.Random(randomLong);
     System.out.println("INet.startnew game");
     ebbe.setPlayerButtonsEnabled(false);
     //finish the old game
     EbbeFlut.chronical.setGameIsFinished();
   }
    
   private long randomLong;
   
   public void startGameAfterWaiting(long longValue)
   { randomLong=longValue;
     finalStart();
   }
   
   public void toChatWindow(String idFrom,String msg)
   { chatWindow.append("(client "+idFrom+"):"+msg+"\n");
   }
   
   public void sendTurn(Path path)
   { client.sendTurn(path);
   }
   
   /** get turn from foreignplayer*/
   public void getTurn(String cmd)
   { foreignPlayer.nextTurn(cmd);
   }
      
   /** be sure you are connected with a server, before you call this methode
    */
   public void refreshLayout()
   { if(client==null) return;
     label.setText(labelText+player.getName()+" with id:"+client.getID());
   }
 
   public void addClientToAll(String id)
   { all.add(id);
   }
   
   public boolean rmClientFromAll(String id)
   { try
     { all.remove(id);
       return true;
     }
     catch(IllegalArgumentException iae)
     { return false;
     }
   }
   
   public void addClientToAsking(String id)
   { wannaPlay.add(id);
   }
   
   public boolean rmClientFromAsking(String id)
   { try
     { wannaPlay.remove(id);     
       return true;
     }
     catch(IllegalArgumentException iae)
     { return false;
     }
   }
   
   /** get actions cmds from MenuInit class
    */
   class INetAction implements ActionListener
   {  public void actionPerformed(ActionEvent ae)
      {  Button b=(Button)ae.getSource();
      
        if(b.getLabel().equals(MyDialog.cancel))
        { doHide();
        }
        else if(b.getLabel().equals("Ask"))
        { //System.out.println("item:"+all.getSelectedItem()+",iam:"+client.getID());
          client.askClient(all.getSelectedItem());
        }
        else if(b.getLabel().equals("Play"))
        { player.no=1;
          client.sendHandshake(wannaPlay.getSelectedItem(), player);          
        }
        else if(b.getLabel().equals("Player Settings"))
        { ebbe.getSettingsDialog(Const.NO_1);
          refreshLayout();
        }        
      }
   }
   
   private Frame getThis()
   { return ebbe;     
   }
   
   public void setClient(Client client)
   { this.client=client;
   }
   
   class Close extends WindowAdapter
   { public void windowClosing(WindowEvent e)
     { doHide();
     }
   }
   
   public Player getPlayer()
   { return player;
   }      
   
   public InternetPlayer getForeignPlayer()
   { return foreignPlayer;
   }      
   
   class KeyL extends KeyAdapter
   {  
       /**all these events cant call consume() anywhere else*/
       public void keyPressed(KeyEvent e)
       {    int code=e.getKeyCode();
         if(code==KeyEvent.VK_ENTER) 
         { String test=textIn.getText();
           client.sendChatText(test);
           textIn.setText("");
           System.out.println("sendchat:"+test+":");
         }
       }
   }
}
