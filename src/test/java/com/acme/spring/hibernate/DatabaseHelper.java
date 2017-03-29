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

    protected final DataSource dataSource;

    public DatabaseHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Prepares the new database.</br>
     * Looks for file 'input_ds.xml' in the folder for the test case.<br/>
     * <br/>
     * This method must be called directly in the test case method so that the
     * test case name can be found from the StackTraceElement
     * @throws Exception
     */
    public void prepareNewDatabase() throws Exception {
        prepareDatabase('/' + getTestCaseName() + "/input_ds.xml");
    }

    /**
     * Prepares the new database.</br>
     * Looks for file 'input_dsInt.xml' in the folder for the test case.<br/>
     * <br/>
     * This method must be called directly in the test case method so that the
     * test case name can be found from the StackTraceElement
     * @throws Exception
     */
    public void prepareOldDatabase() throws Exception {
        prepareDatabase('/' + getTestCaseName() + "/input_dsInt.xml");
    }

    private String getTestCaseName() {
        StackTraceElement elements[] = Thread.currentThread().getStackTrace();
        return elements[3].getMethodName();
    }


    public void prepareDatabase(String datasetPath) throws Exception {
        IDatabaseTester databaseTester = new DataSourceDatabaseTester(dataSource);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(readDataSetFromClasspath(datasetPath));
        databaseTester.onSetup();
    }

    public void assertNewTestData() throws Exception {
        assertTestData("asserting new application database", "/" + getTestCaseName() + "/expected_result_1.xml", null, null);
    }

    public void assertNewTestData(String excludedColumns[]) throws Exception {
        assertTestData("asserting new application database", "/" + getTestCaseName() + "/expected_result_1.xml", null, excludedColumns);
    }

    public void assertFirstIntegration() throws Exception {
        assertTestData("asserting integration tables", "/" + getTestCaseName() + "/expected_result_2.xml", null, null);
    }

    public void assertFirstIntegration(String excludedColumns[]) throws Exception {
        assertTestData("asserting integration tables", "/" + getTestCaseName() + "/expected_result_2.xml", null, excludedColumns);
    }

    public void assertTestData(String context, String datasetPath, String excludedColumns[]) throws Exception {
        assertTestData(context, datasetPath, null, excludedColumns);
    }

    public void assertTestData(final String context, String datasetPath, String orderBy[],
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
         * fail with errors if we need to (we catch the exception and adjust it to add the context information
         */
        try {
            errorCollector.report();
        }
        catch(AssertionError x) {
            if (context != null)  {
                AssertionError xAdjusted = new AssertionError("Test Failed " + context + "\n" + x.getMessage());
                xAdjusted.setStackTrace( x.getStackTrace() );
                throw xAdjusted;
            }
            else {
                throw x;
            }
        }
    }

    private IDataSet readDataSetFromClasspath(String datasetPath) throws Exception {
        try (InputStream in = IntegrationHelper.class.getResourceAsStream("/datasets/" + datasetPath);) {
            return new FlatXmlDataSetBuilder().build(in);
        }
    }
}
