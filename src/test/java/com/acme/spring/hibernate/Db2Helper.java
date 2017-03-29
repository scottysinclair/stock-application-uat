package com.acme.spring.hibernate;

import javax.sql.DataSource;

/**
 * We can implement any custom behaviour for DB2 in this class
 * @author scott
 *
 */
public class Db2Helper extends DatabaseHelper {

    public Db2Helper(DataSource dataSource) {
        super(dataSource);
    }

}
