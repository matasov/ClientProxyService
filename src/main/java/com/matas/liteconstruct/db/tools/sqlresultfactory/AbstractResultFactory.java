package com.matas.liteconstruct.db.tools.sqlresultfactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import com.matas.liteconstruct.db.models.structure.abstractmodel.StructureFieldAbstract;
import com.matas.liteconstruct.db.models.structure.model.StructureFieldImplemented;
import com.matas.liteconstruct.db.tools.StructrueCollectionEnum;
import com.matas.liteconstruct.db.tools.sqlselectfactory.FieldsFactoryInterface;

public class AbstractResultFactory {

  @SuppressWarnings("finally")
  public Map<UUID, StructureFieldAbstract> getSelectResult(JdbcTemplate jdbcTemplate,
      FieldsFactoryInterface fieldsInterface) {
    try {
      return jdbcTemplate
          .query(fieldsInterface.getQuery(),
              (rs, rowNum) -> new StructureFieldImplemented(UUID.fromString(rs.getString("id")),
                  UUID.fromString(rs.getString("class")), rs.getString("name"),
                  UUID.fromString(rs.getString("fieldclass")), rs.getByte(StructrueCollectionEnum.INNER.toString()),
                  rs.getString("show_name")))
          .stream().collect(Collectors.toMap(element -> element.getId(), element -> element));
    } catch (SQLException sqlex) {
      sqlex.printStackTrace();
      return null;
    }
  }
}
