package com.matas.liteconstruct.db.models.classes.model;

import java.util.UUID;
import com.matas.liteconstruct.db.models.classes.abstractmodel.CustomerClassModelAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerClassModel implements CustomerClassModelAbstract {

  private UUID id;
  private String name;
  private byte type;
  private int permission;
}
