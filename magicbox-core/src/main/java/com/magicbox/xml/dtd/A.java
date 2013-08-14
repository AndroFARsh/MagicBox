package com.magicbox.xml.dtd;
import java.util.Hashtable;
public class A
{
  public static final String[] ID_TO_NAME = {"class", "tag", "id", "value", "taggedBy", "refTag", "ref", "name"};
  public static final int Class = 0;
  public static final int Tag = 1;
  public static final int Id = 2;
  public static final int Value = 3;
  public static final int TaggedBy = 4;
  public static final int RefTag = 5;
  public static final int Ref = 6;
  public static final int Name = 7;
  public static final int COUNT = 8;
  public static final Hashtable NAME_TO_ID = new Hashtable();
  static {
           NAME_TO_ID.put("class", new Integer(Class));
           NAME_TO_ID.put("tag", new Integer(Tag));
           NAME_TO_ID.put("id", new Integer(Id));
           NAME_TO_ID.put("value", new Integer(Value));
           NAME_TO_ID.put("taggedBy", new Integer(TaggedBy));
           NAME_TO_ID.put("refTag", new Integer(RefTag));
           NAME_TO_ID.put("ref", new Integer(Ref));
           NAME_TO_ID.put("name", new Integer(Name));
         }
  public static final int[][] ID_TO_INDEX = {{-1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 2, -1, -1, -1, -1, -1}, {-1, 0, 1, -1, -1, -1, -1, -1}, {-1, -1, -1, 0, 1, 2, 3, 4}};
  public static final int[][] INDEX_TO_ID = {{}, {Class, Tag, Id}, {Tag, Id}, {Value, TaggedBy, RefTag, Ref, Name}};
}