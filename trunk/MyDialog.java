/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 *
 * MyDialog.java
 *
 * Created on 7. Oktober 2004, 21:20
 */

import java.awt.*;


/** see the BUG in methode pack() <- this class mainly avoids this
 *
 * @author  peter
 */
public class MyDialog extends Dialog
{
    static String ok="Ok";
    static String cancel="Cancel";
    static String yes="Yes";
    static String no="No";
    
    protected String returnStatus;
    
    /** Creates a new instance of MyDialog */
    public MyDialog(Frame owner,boolean modal) 
    { super(owner,modal);      
    }
    
    protected void doClose(String retStatus)
    {  returnStatus = retStatus;
       setVisible(false);
      // System.out.println(returnStatus);
       dispose();
    }    
    
    public String getReturnStatus()
    { return returnStatus;
    }
    
    public void pack()
    { super.pack();
      //like a BUG
      //work around to make the dialogs smaller for the zaurus
      Dimension d=getSize(),machine=getParent().getSize();//Toolkit.getDefaultToolkit().getScreenSize();
      if(d.height>machine.height) 
          d.setSize(d.width,   machine.height);
      if(d.width>machine.width) 
          d.setSize(machine.width, d.height);
      setSize(d);
      setLocation(getParent().getLocation());
    }
    
    
}
