package source.main;
import source.util.Stack;

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * Path.java
 *
 * Created on 30. December 2004, 14:15
 */

 /** one turn may consist of many moves, so save them in path
    */
public class Path
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

     public void clear()
     { nextMoves.removeAllElements();
     }  
 
     
     static private char defaultDelim=';';
     
     public String toString()
     { return toString(defaultDelim);
     } 
     
     public String toString(char delim)
     { StringBuffer sb=new StringBuffer();
       
        for(int i=0; i<nextMoves.size(); i++)
        { sb.append(((Move)nextMoves.elementAt(i)).toString()+delim);
        }
       return sb.toString();
     }
     
     static public Path stringToPath(String str,char delim,Player player)
     {  java.util.StringTokenizer st=new java.util.StringTokenizer(str, delim+"");
        Path p=new Path();
        
       while(st.hasMoreTokens())
       { p.push(Move.stringToMove(st.nextToken(), player));
       }
        
       return p;
     }
     
     static public Path stringToPath(String str,Player player)
     { return stringToPath(str,defaultDelim,player);
     }
}