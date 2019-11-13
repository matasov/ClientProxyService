package com.matas.liteconstruct.db.models.innerrecords.abstractmodel;

import java.util.UUID;

public interface InnerRecord {

  UUID getClassId();
  
  UUID getImplementedId();

  UUID getRecordId();

  int getTurn();
  
  void setTurn(int turn);
}
