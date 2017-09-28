package com.serdar.comodo.db;

import com.serdar.comodo.model.ConnectionSummary;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionSummaryMapper implements ResultSetMapper<ConnectionSummary> {

    @Override
    public ConnectionSummary map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new ConnectionSummary(rs.getInt("id"), rs.getLong("hour_range"), rs.getLong("src_ip"), rs.getLong("dest_ip"), rs.getInt("protocol"), rs.getInt("number_of_events"), rs.getString("source_file"), rs.getString("upload_code"));
    }
}
