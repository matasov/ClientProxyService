package com.matas.liteconstruct.db.models.structure.abstractmodel;

import java.util.UUID;
import com.matas.liteconstruct.service.management.structure.PrimitiveCustomClass;

/**
 * Abstract structure of a custom class data field.
 * 
 * @author engineer
 *
 */
public interface StructureFieldAbstract {

  /**
   * Returns uuid of the structure field.
   * 
   * @return uuid of the structure field
   */
  public UUID getId();

  /**
   * Returns information about the class for which the entry was made.
   * 
   * @return uuid type of the class
   */
  public UUID getClassId();

  /**
   * Returns the name of the field that is used for internal operations. Must be unique within this
   * class.
   * 
   * @return string special name
   */
  public String getFieldName();

  /**
   * Returns information about the type of nested data. There may be primitives, or custom classes.
   * 
   * @return
   */
  public UUID getDataClass();

  /**
   * Returns the type of association with the nested class. 0 - Basic (For primitives, as example) 1
   * - Unique (for data classes) 2 - Plural (for data classes) 3 - SubCollection (for data classes)
   * 
   * @return integer type of association
   */
  public byte getInnerType();

  /**
   * Returns the visible field name of the data class.
   * 
   * @return string name
   */
  public String getFieldShowName();
}
