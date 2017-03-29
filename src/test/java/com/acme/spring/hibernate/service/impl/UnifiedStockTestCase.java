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

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.spring.integration.test.annotation.SpringConfiguration;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.acme.spring.hibernate.Db2Helper;
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

    private PostgresqlHelper postgresHelper;

    private Db2Helper db2Helper;

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

    @PostConstruct
    public void init() {
        postgresHelper = new PostgresqlHelper(ds);
        db2Helper = new Db2Helper(dsInt);
    }

    /**
     * Test case: http://beitrag-confluence/VVL/testcases/testcase1
     *
     */
    @Test
    public void test_case_1() throws Exception  {
      /*
       * clean and prepare old and new databases
       */
      postgresHelper.prepareNewDatabase();
      db2Helper.prepareOldDatabase();

      /*
       * perform some business logic in the new application
       */
      Stock acme = createStock("Acme", "ACM", 123.21D, new Date());
      stockService.save(acme);

      Stock redhat = createStock("Red Hat", "RHC", 59.61D, new Date());
      stockService.save(redhat);

      /*
       * assert the state of the new application database.
       */
      postgresHelper.assertNewTestData(new String[]{"date"});

      /*
       * execute the integration job.
       */
      IntegrationHelper.executeIntegration();

      /*
       * assert the state of the DB2 database after integration.
       */
      db2Helper.assertFirstIntegration(new String[]{"date"});
    }


    /**
     * Test case: http://beitrag-confluence/VVL/testcases/testcase2
     *
     */
    @Test
    public void test_case_2() throws Exception  {
      /*
       * clean and prepare old and new databases
       */
      postgresHelper.prepareNewDatabase();
      db2Helper.prepareOldDatabase();

      /*
       * perform some business logic in the new application
       */
      Stock acme = createStock("ABC", "ABC", 999.21D, new Date());
      stockService.save(acme);

      /*
       * assert the state of the new application database.
       */
      postgresHelper.assertNewTestData(new String[]{"date"});

      /*
       * execute the integration job.
       */
      IntegrationHelper.executeIntegration();

      /*
       * assert the state of the DB2 database after integration.
       */
      db2Helper.assertFirstIntegration(new String[]{"date"});
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
