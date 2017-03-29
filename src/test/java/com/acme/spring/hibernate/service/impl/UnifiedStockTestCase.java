/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme.spring.hibernate.service.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.spring.integration.test.annotation.SpringConfiguration;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.acme.spring.hibernate.DatabaseHelper;
import com.acme.spring.hibernate.Deployments;
import com.acme.spring.hibernate.IntegrationHelper;
import com.acme.spring.hibernate.PostgresqlHelper;
import com.acme.spring.hibernate.domain.Stock;
import com.acme.spring.hibernate.service.StockService;

/**
 * <p>Tests the {@link com.acme.spring.hibernate.service.impl.DefaultStockService} class.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@RunWith(Arquillian.class)
@SpringConfiguration("applicationContext.xml")
public class UnifiedStockTestCase  {

    /**
     * <p>Creates the test deployment.</p>
     *
     * @return the test deployment
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        return Deployments.createDeployment();
    }


    @Autowired
    @Qualifier("dataSource")
    private DataSource ds;

    @Autowired
    @Qualifier("dataSourceInt")
    private DataSource dsInt;

    /**
     * <p>Injected {@link com.acme.spring.hibernate.service.impl.DefaultStockService}.</p>
     */
    @Autowired
    private StockService stockService;

    /**
     * <p>{@link SessionFactory} instance used by tests.</p>
     */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * <p>Retrieves current {@link Session}.</p>
     *
     * @return the current session
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }


    /**
     * @ApplyScriptBefore is called before @UsingDataSet
     * @UsingDataSet is called before @Before
     *
     */
    @Before
    public void before() throws SQLException {
        System.out.println("============== before =============== ");
        PostgresqlHelper.fixSequences( ds);
        System.out.println("============== fixed sequences =============== ");
    }

    /**
     * @After is called before @ApplyScriptAfter
     */
    @After
    public void after() {
        System.out.println("============== after=============== ");
    }


    /**
     * Test case: http://beitrag-confluence/VVL/testcases/testcase2
     *
     */
    @Test
    public void test_case_1() throws Exception  {
      /*
       * clean and prepare old and new databases
       */
      PostgresqlHelper.prepareDatabase(ds, "stocktestcase_2/input_ds.xml");
      DatabaseHelper.prepareDatabase(dsInt, "stocktestcase_2/input_dsInt.xml");

      Stock acme = createStock("Acme", "ACM", 123.21D, new Date());
      stockService.save(acme);

      Stock redhat = createStock("Red Hat", "RHC", 59.61D, new Date());
      stockService.save(redhat);

      /*
       * assert the state of the new application database.
       */
      DatabaseHelper.assertTestData(ds, "stocktestcase_2/expected_result_1.xml", new String[]{"date"});

      /*
       * execute the integration job.
       */
      IntegrationHelper.executeIntegration();

      /*
       * assert the state of the DB2 database after integration.
       */
      DatabaseHelper.assertTestData(dsInt, "stocktestcase_2/expected_result_2.xml", new String[]{"date"});
    }


    /**
     * <p>Creates new stock instance</p>
     *
     * @param name   the stock name
     * @param symbol the stock symbol
     * @param value  the stock value
     * @param date   the stock date
     *
     * @return the created stock instance
     */
    private static Stock createStock(String name, String symbol, double value, Date date) {
        Stock result = new Stock();
        result.setName(name);
        result.setSymbol(symbol);
        result.setValue(new BigDecimal(value));
        result.setDate(date);
        return result;
    }

}
