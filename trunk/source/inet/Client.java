/*
 * Client.java
 *
 * Created on 23. Januar 2005, 21:59
 */

package source.inet;

import source.util.Const;
import source.main.*;

import java.net.*;
import java.io.*;
import java.util.*;//StringTokenizer
import java.awt.event.*; //ActionListener

/** This client class is constructed only for the purposes of a server-client
 * software for EbbeFlut. So we only need two chat partners(players). <p>
 * Client sends the following commands to the server (or its ConnectionToClient):<p>
 * <b>connect</b> idTo idFrom  =<i>ask sb. if he wants to play</i><br>
 * <b>handshake</b> idWith playerInfos randomLongValue=<i>send the player 
                                                  information after connect-cmd</i><br>
 * <b>turn infos</b> move  =<i>send the special move to the other player</i><br>
 * <b>chat msg</b> idTo idFrom sendMsg  =<i>send a message to special id</i><br>
 * <b>closeGame</b>  =<i>close the current game, close the client-client connection</i><br>
 * <b>exit</b>   =<i>exit from the server connection<i><br>
 * <b>rm all id</b>   =<i>exit from the server connection, send this info to ALL clients<i><br>
 * cl0                           | cl1
 * ------------------------------|--------
 * "ask"                         |   "play"=>playWithID, sendhandshake
 * send handshakeBack,           | startGame
 *     playwithID,waitForStarting|
 *
 *
 * @author  peter
 */
public class Client implements Runnable
{   
  private Socket mySocket;
  static private int port=6789;
  private BufferedReader in;
  private PrintWriter out;
  private String id=null, playWithID=null;
  private String idStr="(client) ";
  private INet inet;      
  
    public Client(String host, INet inetz)
    { inet=inetz;
      if(!Const.DEBUG) throw new Error("change this here in Client constructor etc...");
      
      try
      { mySocket=new Socket(host,port);
      }
      catch(ConnectException exc)
      { System.err.println(idStr+"may be server is down\n");
        exc.printStackTrace();
      }
      catch(SocketException exc)
      { System.err.println(idStr+"cannot find servernetwork-is your internet connection okay?\n");
        exc.printStackTrace();       
      }
      catch(UnknownHostException exc)
      { System.err.println(idStr+"unknown host\n");
        exc.printStackTrace();
      }
      catch(IOException exc)
      { exc.printStackTrace();
      }
        try
      { in=new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        out=new PrintWriter(mySocket.getOutputStream(),true);
      }
      catch(IOException exc)
      { System.err.println(idStr+"cant get streams from socket, want to communicate with the server");
      }
      inet.setClient(this);
      System.err.println(idStr+"ip:"+mySocket.getInetAddress().getHostAddress());      
      System.err.println(idStr+"ip name:"+mySocket.getInetAddress().getHostName());      
      //create a thread to allow the main program EbbeFlut leaving the constructor      
      new Thread(this).start();
    }
    
    public void run()
    {  String str;
       
      while((str=readLine())!=null)
      { InterChange inter=new InterChange(str);
        System.out.println("(client "+id+")"+inter.toString());
          //if the server sends an answer on "new Client(new Socket(args[0],port));"
        if(inter.cmd.equals("YourId"))
        { id=inter.idTo;
          idStr="(client "+id+") ";
          inet.refreshLayout();
        }
        else if(inter.cmd.equals("add"))
        { inet.addClientToAll(inter.idFrom);          
        }
        else if(inter.cmd.equals("rm"))
        { boolean ok=inet.rmClientFromAll(inter.idFrom);
          if(!ok) inet.rmClientFromAsking(inter.idFrom);
        }
        //if the connected partner sends a chatmsg
        else if(inter.cmd.equals("chat"))
        { toMyChat(inter);
        }
        
        //NOW this is an answer from clients "ask"-request
        else if(inter.cmd.equals("handshake"))
        {   InternetPlayer foreignPlayer=inter.getPlayerInfos();
            Player tmpPlayer=inet.getPlayer();
            playWithID=inter.idFrom;
            
          //foreignPlayer.no==Const.NO_1) 
            tmpPlayer.no=2;
            
          //now send your player info to foreignPlayer
          sendCmd(new InterChange(id,inter.idFrom,"handshakeBack",tmpPlayer));         
          
          inet.startGame(foreignPlayer, inter.idFrom);
          //after that waiting for startGame
        }
        else if(inter.cmd.equals("handshakeBack"))
        { System.out.println("handshakeback");
          inet.startGame(inter.getPlayerInfos(), inter.idFrom);
        }
        else if(inter.cmd.equals("startGame"))
        { System.out.println("startGame setNo Waiting");
          inet.startGameAfterWaiting(Long.parseLong(inter.msg));
        }
        
        //if somebody wants to connect
        else if(inter.cmd.equals("ask"))
        { inet.addClientToAsking(inter.idFrom);
          inet.rmClientFromAll(inter.idFrom);
        }
        
        //if your connected partner sends you the turn 
        else if(inter.cmd.equals("turn"))
        { inet.getTurn(inter.msg);
        }
        else if(inter.cmd.equals("ServerExit")) break;
        
      }
      myClose();
    }
    
    /** send all player informations to the idTo client
     */
    public void sendCmd(InterChange inter)
    { out(inter);//new InterChange(id,idTo,"handshake",infos)
    }
    
    /** try to connect, waiting for handshake after that
     */
    public void askClient(String idTmpTo)
    { if(idTmpTo.equals(id)) 
      { toMyChat(new InterChange(id,id,"chat"
                                ,"cant ask yourself.( you can but ...)"));
        return;
      }
      /** try to connect=ask sb., now waiting for handshake
       */
      out(new InterChange(id,idTmpTo,"ask",""));
    }
    
    public void startGame(long randomLong)
    { sendCmd(new InterChange(id, playWithID,"startGame",""+randomLong));
    }
    
    public void sendTurn(Path p)
    { sendCmd(new InterChange(id,playWithID,"turn",p));
    }
    
    public void sendHandshake(String opponentsID,Player player)
    { playWithID=opponentsID;
      //System.out.println("i:"+id+",oppoenent:"+playWithID);
      sendCmd(new InterChange(id,playWithID,"handshake",player));      
    }
    
    /** send chat msg to connected partner<br>
     *  called from INet
     */
    public void sendChatText(String sendString)
    { if(playWithID==null) 
      { System.err.println(idStr+"sorry you cant send, you are not connected");
        return;
      }
      InterChange inter=new InterChange(id,playWithID,"chat",sendString);
      
      out(inter);
      toMyChat(inter);
    }  
    
    /** send a string to your own chat "window"
     */
    private void toMyChat(InterChange inter)
    { //System.err.println();
      inet.toChatWindow(inter.idFrom, inter.msg);
    }
    
    private void myClose()
    { //remove ME!
      out(new InterChange(id,InterChange.allClientsID,"rm",""));
      System.err.println(idStr+"rm me");
      try
      { mySocket.close();
      }
      catch(IOException exc)
      { exc.printStackTrace();
      }
      //only system exit if client is a system process, not if it is a thread:System.exit(0);
    }
    
    /** the raw output to ConnectionToClient, dont use out.println() for your self!
     */
    private void out(InterChange inter)
    { System.err.println("client.out:"+inter.toString());
        
      out.println(inter.toString());
    }
    
    private String readLine()
    { //how to find out when the server brakes the connection?
      try
      { return in.readLine();
      }
      catch(IOException exc)
      { System.err.println(idStr+"readLine was interrupted");
        return null;
      }
    }
    
    public PrintWriter getPrintWriter()
    { try
      { out=new PrintWriter(mySocket.getOutputStream(), true);
      }
      catch(IOException exc)
      { System.err.println(idStr+"cant give my outputstream");
      }
      return out;
    }
    
    public String getID()
    { return id;
    }
}
