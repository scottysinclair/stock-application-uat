package com.acme.spring.hibernate;

import java.io.InputStream;
import java.util.Collections;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseDataSet;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.persistence.core.test.AssertionErrorCollector;
import org.jboss.arquillian.persistence.dbunit.DataSetComparator;

public class DatabaseHelper {

    public static void prepareDatabase(DataSource dataSource, String datasetPath) throws Exception {
        IDatabaseTester databaseTester = new DataSourceDatabaseTester(dataSource);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(readDataSetFromClasspath(datasetPath));
        databaseTester.onSetup();
    }

    public static void assertTestData(DataSource dataSource, String datasetPath, String excludedColumns[]) throws Exception {
        assertTestData(dataSource, datasetPath, null, excludedColumns);
    }

    public static void assertTestData(DataSource dataSource, String datasetPath, String orderBy[],
            String excludedColumns[]) throws Exception {

        if (orderBy == null) {
            orderBy = new String[0];
        }
        if (excludedColumns == null) {
            excludedColumns = new String[0];
        }
        DataSetComparator comparator = new DataSetComparator(orderBy, excludedColumns, Collections.emptySet());

        IDatabaseConnection connection = new DatabaseDataSourceConnection(dataSource);
        IDataSet currentDataSet = new DatabaseDataSet(connection, false);
        IDataSet expectedDataSet = readDataSetFromClasspath(datasetPath);
        AssertionErrorCollector errorCollector = new AssertionErrorCollector();

        comparator.compare(currentDataSet, expectedDataSet, errorCollector);

        /*
         * fail with errors if we need to
         */
        errorCollector.report();
    }

    private static IDataSet readDataSetFromClasspath(String datasetPath) throws Exception {
        try (InputStream in = IntegrationHelper.class.getResourceAsStream("/datasets/" + datasetPath);) {
            return new FlatXmlDataSetBuilder().build(in);
        }
    }
}
