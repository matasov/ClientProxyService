package com.matas.liteconstruct.db.models.collections.abstractmodel;

import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import java.util.UUID;

/**
 * Abstract class of a specific data collection for a particular class.
 * 
 * @author engineer
 *
 */
public interface StructureCollectionAbstract {

	/**
	 * Unique identifier of the record
	 * 
	 * @return uuid of the record
	 */
	UUID getId();

	/**
	 * Returns information about the class for which the entry was made.
	 * 
	 * @return uuid type of the class
	 */
	UUID getClassId();

	/**
	 * Returns uuid of the type of nested data.
	 * 
	 * @return uuid of the nested field
	 */
	UUID getFieldId();

	/**
	 * The index in the structure's field queue that is displayed to the user.
	 * 
	 * @return position index in the collection
	 */
	int getTurn();
	
	void setTurn(int turn);

	/**
	 * Permission to use the field in the collection.
	 * 
	 * @return boolean value. if true - access is allowed.
	 */
	boolean isUseful();

	/**
	 * Permission to show this field in the collection.
	 * 
	 * @return boolean value. if true - access is allowed.
	 */
	boolean isVisible();

	/**
	 * Permission to edit this field in the collection.
	 * 
	 * @return boolean value. if true - access is allowed.
	 */
	boolean isEdit();

	/**
	 * Permission to delete this field in the collection.
	 * 
	 * @return boolean value. if true - access is allowed.
	 */
	boolean isDelete();

	/**
	 * Permission to insert in this field of the collection.
	 * 
	 * @return boolean value. if true - access is allowed.
	 */
	boolean isInsert();

	/**
	 * Description of the user field structure from the table of a specific data
	 * class.
	 * 
	 * @return StructureFieldAbstract
	 */
	StructureFieldAbstract getStructureField();
}
