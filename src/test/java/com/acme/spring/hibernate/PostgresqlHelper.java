package com.acme.spring.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class PostgresqlHelper extends DatabaseHelper {

    public PostgresqlHelper(DataSource dataSource) {
        super(dataSource);
    }

    public void prepareDatabase(String datasetPath) throws Exception {
        super.prepareDatabase(datasetPath);
        fixSequences();
    }

    private void fixSequences() throws SQLException {
            try (Connection con = dataSource.getConnection() ) {
                try ( Statement seqStmt = con.createStatement() ) {
                  final String q = "SELECT c.relname FROM pg_class c WHERE c.relkind = 'S';";
                  try ( ResultSet rs = seqStmt.executeQuery(q) )  {

                      // ... and update the sequence to match max(id)+1.
                    while (rs.next()) {
                        String sequence = rs.getString("relname");
                        String table = sequence.substring(0, sequence.length()-7);
                        try ( Statement updStmt = con.createStatement() ) {
                            updStmt.executeQuery("SELECT SETVAL('" + sequence + "', (SELECT MAX(id) FROM " + table + "));");
                        }
                    }
                  }
                }
            }
    }

}
