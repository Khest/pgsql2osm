package no.hbv.pgsql2osm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

/**
 * Created by Dakh on 2016-03-14.
 */
public class Schemas {

    Stack<String> tableList;

    public Schemas(Connection conn, String schemaName) throws SQLException {
        this.tableList = getListOfTableNames(conn, schemaName);
    }

    public Stack<String> getListOfTableNames() {
        return this.tableList;
    }

    private Stack<String> getListOfTableNames(Connection conn, String schemaName) throws SQLException {
        String sql = "SELECT table_name" +
                " FROM information_schema.tables" +
                " WHERE table_schema='" + schemaName + "'" +
                " AND table_type='BASE TABLE';";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet tablesRS = stmt.executeQuery();
        Stack<String> tablesStack = new Stack<>();
        while (tablesRS.next()) {
            tablesStack.push(tablesRS.getString("table_name"));
        }
        if (tablesStack.size() == 0) {
            throw new SQLException("Nothing found for scehma " + schemaName);
        }
        return tablesStack;
    }
}
