package com.matas.liteconstruct.db.models.collections.model;

import java.util.UUID;

import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionAbstract;
import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StructureCollectionImplemented implements StructureCollectionAbstract{

	private UUID id;
	private UUID classId;
	private UUID fieldId;
	
	private int turn;
	
	private boolean useful;
	private boolean visible;
	private boolean edit;
	private boolean delete;
	private boolean insert;

	transient private StructureFieldAbstract structureField;

}
