package com.matas.liteconstruct.db.models.collections.model;

import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionAbstract;
import com.matas.liteconstruct.db.models.collections.abstractmodel.StructureCollectionNodeFieldsAbstract;

public class StructureCollectionNode extends StructureCollectionNodeFieldsAbstract{

	public StructureCollectionNode(StructureCollectionNodeFieldsAbstract parentNode, StructureCollectionAbstract node) {
		super(parentNode, node);
	}

}
