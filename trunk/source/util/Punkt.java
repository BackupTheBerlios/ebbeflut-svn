/*
 * Punkt.java
 *
 * Created on 20. Februar 2005, 12:36
 */

package source.util;

/**
 *
 * @author  peter
 */
public class Punkt 
{ public int x,y;
  public Punkt(int x,int y)
  { this.x=x;
    this.y=y;
  }
  public int getX()
  { return x;
  }
  public int getY()
  { return y;
  }
  
  public String toString()
  { return "x="+x+" | y="+y;
  }  
}