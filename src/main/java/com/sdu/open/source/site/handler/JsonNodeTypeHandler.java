package com.sdu.open.source.site.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 声明该处理器映射的 Java 类型（JsonNode）和 JDBC 类型（VARCHAR/JSON，根据数据库字段类型选择）
@MappedTypes(JsonNode.class)
@MappedJdbcTypes(JdbcType.VARCHAR) // 若数据库字段是 JSON 类型，可改为 JdbcType.JSON（需数据库支持，如 MySQL 5.7+）
public class JsonNodeTypeHandler extends BaseTypeHandler<JsonNode> {

    // Jackson 的 ObjectMapper，用于 JSON 字符串与 JsonNode 的转换
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 设置 SQL 参数（Java 类型 -> JDBC 类型）
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType) throws SQLException {
        // 将 JsonNode 转为 JSON 字符串，存入数据库
        ps.setString(i, parameter.toString());
    }

    /**
     * 从 ResultSet 读取字段（JDBC 类型 -> Java 类型）
     */
    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonStr = rs.getString(columnName);
        return parseJsonStrToJsonNode(jsonStr);
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonStr = rs.getString(columnIndex);
        return parseJsonStrToJsonNode(jsonStr);
    }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonStr = cs.getString(columnIndex);
        return parseJsonStrToJsonNode(jsonStr);
    }

    /**
     * 辅助方法：将 JSON 字符串转为 JsonNode
     */
    private JsonNode parseJsonStrToJsonNode(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON string to JsonNode: " + jsonStr, e);
        }
    }
}