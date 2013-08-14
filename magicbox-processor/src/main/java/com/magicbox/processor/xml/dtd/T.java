package com.magicbox.processor.xml.dtd;
import java.util.Hashtable;
public class T
{
  public static final String[] ID_TO_NAME = {"contexts", "beans", "bean", "alias", "property"};
  public static final int[] _keys = {-1, A.Output, A.Id, A.Id, A.Name};
  public static final int[] _attCount = {0, 1, 3, 2, 5};
  public static final boolean[] _hasPCDATA = {false, false, false, false, false};
  public static final boolean[] _hasArbitraryAttributes = {false, false, false, false, false};
  public static final int Contexts = 0;
  public static final int Beans = 1;
  public static final int Bean = 2;
  public static final int Alias = 3;
  public static final int Property = 4;
  public static final int COUNT = 5;
  public static final Hashtable NAME_TO_ID = new Hashtable();
  static {
           NAME_TO_ID.put("contexts", new Integer(Contexts));
           NAME_TO_ID.put("beans", new Integer(Beans));
           NAME_TO_ID.put("bean", new Integer(Bean));
           NAME_TO_ID.put("alias", new Integer(Alias));
           NAME_TO_ID.put("property", new Integer(Property));
         }
}