package com.acme.spring.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class PostgresqlHelper {

	public static void fixSequences(DataSource dataSource) throws SQLException {
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
