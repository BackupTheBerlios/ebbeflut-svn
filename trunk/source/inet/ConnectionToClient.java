/*
 * ConnectionToClient.java
 *
 * Created on 23. Januar 2005, 22:00
 */

package source.inet;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * ConnectionToClient(ctc) sends the following commands to its client:<p>
 * rm [id]   =removes the specific id from Servers<p>
 * add [id]  =adds the spec. id to Server<p>
 * YourId [id]  =sends ctc's own client its id<p>
 * @author  peter
 */
public class ConnectionToClient implements Runnable
{ private Socket socket;
  private Server server;
  private String myClientID;
  private BufferedReader in;
  private PrintWriter out;
  private ConnectionToClient ctc=null;
  private String idStr;
  
    /** Creates a new instance of ConnectionToClient */
    public ConnectionToClient(Server serv, String idClient, Socket sock) 
    { socket=sock;
      server=serv;
      myClientID=idClient;
      idStr="(ctc "+myClientID+") ";
      try
      { in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out=new PrintWriter(sock.getOutputStream(),true);
      }
      catch(IOException exc)
      { System.err.println(idStr+"cant get streams from client socket,i want to communicate with the client");
      }
      //send the specific client its id
      cmdToOwnClient(new InterChange(InterChange.ctcID,myClientID,"YourId",""));
   
      //make me independend
      new Thread(this).start();
   
    }    
    
    public void run()
    {  String str;
       
      while((str=readLine())!=null)
      { InterChange inter=new InterChange(str);
        System.out.println("(ctc "+myClientID+"):"+inter.toString());
        if(inter.cmd.equals("rm"))
        { 
            //if ctc's client exit from server
          if(inter.idFrom.equals(myClientID))
          { server.broadcast(inter);
            break;
          }
          //if another client exit from server, to remove it from the client lists etc.
          else cmdToOwnClient(inter);
          
          continue;
        }
        
        if(inter.idTo.equals(myClientID))
        { cmdToOwnClient(inter);
        }
        else if(inter.idTo.equals(InterChange.allClientsID))
        { server.broadcast(inter);            
        }          
        else cmdToForeign(inter);        
      }
      myClose();
    }
    
    /** send a msg to client
     */
    public void cmdToForeign(InterChange inter)
    { ConnectionToClient tmp=server.getClient(inter.idTo);
      
      if(tmp==null) 
      { cmdToOwnClient(new InterChange(InterChange.ctcID,myClientID
                    ,"chat","sorry cant find a client with id "+inter.idTo));
      }
      else tmp.cmdToOwnClient(inter);
    }        
    
    /** get a command from server or more cmds seperated by a "\n"
     */
    public void cmdToOwnClient(InterChange inter)
    { System.out.println(inter.toString());
      out(inter.toString());
    }
    
    private void myClose()
    { server.broadcast(new InterChange(myClientID, InterChange.allClientsID
                      ,"rm",""));
      server.rmClient(this);
      System.err.println(idStr+"okay bye my client with id:"+myClientID+","
                        +Thread.currentThread().toString());
    }
    
    public String getID()
    { return myClientID;
    }
    
    /** the raw output, dont use out.println() for your self!
     */
    private void out(String str)
    { out.println(str);
    }
    
    private String readLine()
    { try
      { return in.readLine();
      }
      catch(IOException exc)
      { System.err.println(idStr+"readLine was interrupted");
        return null;
      }
    }
}
