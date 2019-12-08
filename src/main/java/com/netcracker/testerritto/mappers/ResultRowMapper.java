package com.netcracker.testerritto.mappers;

import com.netcracker.testerritto.models.Result;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultRowMapper implements RowMapper<Result> {
    @Override
    public Result mapRow(ResultSet resultSet, int i) throws SQLException {
        Result result = new Result();
        result.setId(BigInteger.valueOf(resultSet.getInt("id")));
        result.setTestId(BigInteger.valueOf(resultSet.getInt("test_id")));
        result.setDate(resultSet.getDate("result_date"));
        result.setScore(resultSet.getInt("result_score"));
        result.setStatus(resultSet.getString("result_status"));
        result.setUserId(resultSet.getBigDecimal("user_id").toBigInteger());
        return result;
    }
}
