package server.pome.user.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final TypeReference<List<String>> TYPE = new TypeReference<List<String>>() {};

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    try {
      if (attribute == null) {
        return "[]";
      }
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to serialize tags", e);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    try {
      if (dbData == null || dbData.isBlank()) {
        return new ArrayList<>();
      }
      return objectMapper.readValue(dbData, TYPE);
    } catch (Exception e) {
      // 깨진 데이터가 있으면 빈 데이터로 처리
      return new ArrayList<>();
    }
  }

}
