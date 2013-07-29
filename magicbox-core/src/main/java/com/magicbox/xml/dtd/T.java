package com.magicbox.xml.dtd;
import java.util.Hashtable;
public class T
{
  public static final String[] ID_TO_NAME = {"beans", "bean", "alias", "property"};
  public static final int[] _keys = {-1, A.Id, A.Id, A.Name};
  public static final int[] _attCount = {0, 3, 2, 5};
  public static final boolean[] _hasPCDATA = {false, false, false, false};
  public static final boolean[] _hasArbitraryAttributes = {false, false, false, false};
  public static final int Beans = 0;
  public static final int Bean = 1;
  public static final int Alias = 2;
  public static final int Property = 3;
  public static final int COUNT = 4;
  public static final Hashtable NAME_TO_ID = new Hashtable();
  static {
           NAME_TO_ID.put("beans", new Integer(Beans));
           NAME_TO_ID.put("bean", new Integer(Bean));
           NAME_TO_ID.put("alias", new Integer(Alias));
           NAME_TO_ID.put("property", new Integer(Property));
         }
}