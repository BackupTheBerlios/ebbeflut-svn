/*
 * InterChange.java
 *
 * Created on 12. Februar 2005, 19:31
 */

package source.inet;

import source.main.*;
import source.util.Const;

import java.util.*;

/** use ctcID only for communication ctc <-> its client!!
 *
 * @author  peter
 */
public class InterChange
{  /**clients cmd list     */
    static private String cmdList[]={"ask","chat","handshake","turn","disconnect","handshakeBack"
            ,"add","rm","startGame","ServerExit","YourId"};
            
    public String cmd, msg, idFrom, idTo;
    static public String allClientsID="allClientsID", serverID="serverID",
    /**use ctcID only for communication ctc <-> its client!!*/
                        ctcID="ctcID";         
    
    /** Creates a new instance of InterChange */
    public InterChange(String interChangeFormat) 
    { StringTokenizer st=new StringTokenizer(interChangeFormat," ");
      try
      { idFrom=st.nextToken();
        idTo=st.nextToken();
        cmd=st.nextToken();
        if(cmd.equals("null")) cmd=null;
        
        if(st.hasMoreTokens())  
          msg=interChangeFormat.substring(interChangeFormat.indexOf(cmd)
                            +cmd.length()+1);        
        else msg="";
        //FIXME: if a player make ready although he "don't do his work" then a problem occurs:
        //if Path has no elements: Client.out(inter.toString()) causes
        //a string like: (fromId toId turn null) although 
        //Path.toString==(fromId toId turn )???????
        if(msg.equals("null")) msg="";
      }
      catch(NoSuchElementException nsee)
      { System.out.println("sorry interchangeformat string needs a special format:\n"
                            +"idFrom idTo cmd [message]\nnot:"+interChangeFormat);        
      }
    }
    
    /** only the messagestring should contain more elements,with the space as
     * special delimiter! Other delimiters are allowed!<br>
     * it is very important to use ctcID ONLY for communication ctc <-> its client!!
     */
    public InterChange(String idFrom, String idTo, String cmd, String message)
    { this.idFrom=idFrom;
      this.idTo=idTo;
      this.cmd=cmd;
      this.msg=message;
      if(!isCmd(cmd) && Const.DEBUG) 
          throw new Error("<"+cmd+"> is not a command for InterChange class"); 
    }
    
    public InterChange(String idFrom, String idTo, String cmd, Player player)
    { this(idFrom,idTo,cmd, player.toString());      
    }
  
    public InterChange(String idFrom, String idTo, String cmd, long longValue)
    { this(idFrom,idTo,cmd, ""+longValue);
    }
    
    public InterChange(String idFrom, String idTo, String cmd, Path p)
    { this(idFrom,idTo,cmd, p.toString());
    }
    
    static public boolean isCmd(String cmd)
    { for(int i=0; i<cmdList.length; i++)
      { if(cmdList[i].equals(cmd)) return true;
      }      
      return false;
    }
    
    public String toString()
    { return idFrom+" "+idTo+" "+cmd+" "+msg;
    }
    
    /** work on msg String     */
    public InternetPlayer getPlayerInfos()
    { StringTokenizer st=new StringTokenizer(msg," ");
      
      String name=st.nextToken(), kind=st.nextToken();
      java.awt.Color color=source.gui.SettingsDialog.stringToColor(st.nextToken());
      int no=Integer.parseInt(st.nextToken());     
      //create always internetplayer, no differences between ai and human
      return new InternetPlayer(color, name, no);
    }
        
    /* public String getMessage()
    { return msg;
    }
    
    public String getIdFrom()
    { return idFrom;
    }
    
    public String getIdTo()
    { return idTo;
    }
    
    public String getCmd()
    { return cmd;
    }
    */
}
