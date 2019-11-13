package com.matas.liteconstruct.db.models.collections.abstractmodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import lombok.Data;

@Data
public abstract class StructureCollectionNodeFieldsAbstract {

	private StructureCollectionNodeFieldsAbstract parentNode;
	private ArrayList<StructureCollectionNodeFieldsAbstract> childs;

	private StructureCollectionAbstract currentData;

	public StructureCollectionNodeFieldsAbstract(StructureCollectionNodeFieldsAbstract parentNode,
			StructureCollectionAbstract node) {
		this.parentNode = parentNode;
		this.currentData = node;
		this.childs = new ArrayList<>(20);
	}

	public void addChilds(ArrayList<StructureCollectionNodeFieldsAbstract> newChilds) {
		childs = newChilds;
	}
	
//	public void addChild(StructureCollectionNodeFieldsAbstract newChild) {
//		childs.put(newChild.getId(), newChild);
//	}

	public UUID getId() {
		return currentData.getId();
	}

}
