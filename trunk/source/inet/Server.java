/*
 * Server.java
 *
 * Created on 23. Januar 2005, 21:59
 */

package source.inet;
import source.util.Const;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  peter
 */
public class Server implements Runnable
{ 
  private int port=6789;
  /** test
   */
  private Hashtable allClients=new Hashtable(5);
  private int idCounter=0;
  
  private ServerSocket serverSocket;
    
    public static void main(String[] args) 
    { new Server().run();
      
    }
        
    public Server()
    { try
      { serverSocket= new ServerSocket(port);
        
        System.err.println("(server) Listening to port no:"+port);
        System.err.println("(server) hostname:"+serverSocket.getInetAddress().getHostName());
        System.err.println("(server) ip:"+serverSocket.getInetAddress().getHostAddress());
      }
      catch(IOException exc)
      { exc.printStackTrace();
        System.out.println("\n\nmay be you startet Server Process twice o your local machine?");
      }
    }
    
    boolean runNow=true;
    
    public void run()
    { 
      try
      { while(runNow)
        { addClient(serverSocket.accept());          
        }
      }
      catch(IOException exc)
      { exc.printStackTrace();
        return;
      }
      try
      { serverSocket.close();
      }
      catch(IOException exc)
      { exc.printStackTrace();        
      }
    }
    
    synchronized public String getHostName()
    { return serverSocket.getInetAddress().getHostName();
    }
    
    /** is synchronized neccessary?, make it synchronized cause many instances 
     * of ConnectionToClient have access to server! to avoid conflicts
     */
    synchronized public void addClient(Socket sock)
    { String id=""+idCounter;
      idCounter++;
      
      if(allClients.get(id)!=null)
      { System.err.println("(server) cant give client the id "+id+", some"
                            +"other has the same");
        return;
      }
      //create an extra connection(a thread of Server class) to each client
      ConnectionToClient ctc=new ConnectionToClient(this,id,sock);
      allClients.put(id, ctc);
      System.err.println("(server) client "+id+" added to the hashtable");
      
      InterChange inter=new InterChange(id,InterChange.allClientsID, "add","");      
     
      //add client to the lists of all other clients  except THIS client with "id"    
      broadcast(inter);
   
      //add all the existing clients to THISclients list
      inter.idFrom=InterChange.allClientsID;
      inter.idTo=id;
      cmdFromAll(inter,ctc);
    }
    
    /** sends a cmd to all except "this" client
     */
    synchronized public void broadcast(InterChange inter)
    { Enumeration enums=allClients.elements();
      
      if(!InterChange.allClientsID.equals(inter.idTo)) 
      { if(Const.DEBUG) throw new Error("broadcast idTo!=allClients");
        return;
      }
      
      while(enums.hasMoreElements())
      { ConnectionToClient ctc=((ConnectionToClient)enums.nextElement());
        if(inter.idFrom.equals(ctc.getID())) continue;
        //System.out.println("(client "+inter.idFrom+") sends cmd <"+inter.cmd+"> to client "+ctc.getID()+" broadcast");
        ctc.cmdToOwnClient(inter);
      }  
    }
    
    synchronized public void cmdFromAll(InterChange inter,ConnectionToClient toCtc)
    {  Enumeration enums=allClients.keys();
       
      if(!InterChange.allClientsID.equals(inter.idFrom)) 
      { if(Const.DEBUG) throw new Error("cmdFromAll idFrom!=allClients");
        return;
      }
      inter.idTo=toCtc.getID();      
      while(enums.hasMoreElements())
      { inter.idFrom=(String)enums.nextElement();
        if(inter.idTo.equals(inter.idFrom)) continue;
        //System.out.println("(client "+inter.idFrom+") sends cmd <"+inter.cmd+"> to client "+inter.idTo+" cmdGetter");
        toCtc.cmdToOwnClient(inter);
      }      
    }
    
    /*synchronized public Client createAndConnectAClient(EbbeFlut ebe)
    { throw new Error("not implemented yet");
      //Client c=new Client(serverSocket.getInetAddress().getHostAddress(),ebe);
      //new Thread(c).start();
      //return c;
    }
    */
    synchronized public ConnectionToClient getClient(String id)
    { if(id!=null) return (ConnectionToClient)allClients.get(id);
      return null;
    }
        
   /* synchronized public void sendToAll(InterChange inter)
    { Enumeration enums=allClients.elements();
      
      while(enums.hasMoreElements())
      { ConnectionToClient ctc=((ConnectionToClient)enums.nextElement());
        
        System.out.println("(server) send cmd:"+inter.cmd+" to "+ctc.getID());
        inter.idTo=ctc.getID();
        ctc.cmdToOwnClient(inter);
      }
    }*/
    
    synchronized public Enumeration getClients()
    { return allClients.elements();      
    }
    
    synchronized public void rmClient(ConnectionToClient ctc)
    { removeID(ctc.getID());
    }
    
    synchronized private void removeID(String id)
    { allClients.remove(id);
    }        
    
    synchronized public void exit()
    { //System.exit(0);
      runNow=false;
        Enumeration enums=allClients.elements();
      
      //send a fake clientconnection
      try
      {  new Socket(serverSocket.getInetAddress().getHostName(),port);
      }
      catch(IOException uhe)
      { System.out.println("cant close server!");
      }
        
      InterChange inter=new InterChange(InterChange.serverID,"","ServerExit","");
      System.out.println("(server) exit now!");
        
      //disconnect all clients first
      while(enums.hasMoreElements())
      { ConnectionToClient ctc=((ConnectionToClient)enums.nextElement());
        //if(fromId.equals(ctc.getID())) continue; 
        inter.idTo=ctc.getID();
        ctc.cmdToOwnClient(inter);
      }
        
      //return will cause ending the while(serverSocket..) loop
    }       
}
