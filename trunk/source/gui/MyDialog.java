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

package source.gui;

import java.awt.*;


/** see the BUG in methode pack() <- this class mainly avoids this
 *
 * @author  peter
 */
public class MyDialog extends Dialog
{
    static public String ok="Ok";
    static public String cancel="Cancel";
    static public String yes="Yes";
    static public String no="No";
    static public String save="Save";    
    protected String returnStatus;
    
     /**
     * Constructs an initially invisible <code>Dialog</code> with an empty title,
     * the specified owner frame and modality.
     *
     * @param owner the owner of the dialog
     * @param modal if <code>true</code>, dialog blocks input to other
     *     app windows when shown
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *     is <code>null</code>; this exception is always thrown
     *     when <code>GraphicsEnvironment.isHeadless</code>
     *     returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public MyDialog(Frame owner,boolean modal) 
    { super(owner,modal);      
    }
    
    /** change location before making it visible
     */
    public void setVisible(boolean t)
    { setLocation(getParent().getLocation());
      super.setVisible(t);
    }
    
    protected void doClose(String retStatus)
    {  returnStatus = retStatus;
       setVisible(false);
      //without dispose(); it is quicker, but also secure???
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
    
    public boolean canceled()
    { return returnStatus.equalsIgnoreCase(cancel);
    }
    
    public boolean okay()
    { return returnStatus.equalsIgnoreCase(ok);
    }
    
    public boolean yes()
    { return returnStatus.equalsIgnoreCase(yes);
    }
    public boolean no()
    { return returnStatus.equalsIgnoreCase(no);
    }
    public boolean saved()
    { return returnStatus.equalsIgnoreCase(save);
    }
    
}
