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
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.spring.integration.test.annotation.SpringConfiguration;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import com.acme.spring.hibernate.Deployments;
import com.acme.spring.hibernate.domain.Stock;
import com.acme.spring.hibernate.service.StockService;

/**
 * <p>Tests the {@link com.acme.spring.hibernate.service.impl.DefaultStockService} class.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@RunWith(Arquillian.class)
@SpringConfiguration("applicationContext.xml")
public class UnifiedStockTestCase {

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
    private DataSource ds;

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
     * Test case: http://beitrag-confluence/VVL/testcases/testcase1
     *
     */
    @Test
    @UsingDataSet("stocktestcase_1/input.xml")
    @ShouldMatchDataSet(value = "stocktestcase_1/expected-result.xml", excludeColumns={"date"})
    public void test_case_1() {
        Stock acme = createStock("Acme", "ACM", 123.21D, new Date());
        Stock redhat = createStock("Red Hat", "RHC", 59.61D, new Date());

//        stockService.save(acme);
//        stockService.save(redhat);
    }

    /**
     * Test case: http://beitrag-confluence/VVL/testcases/testcase1
     *
     */
    @Test
    @UsingDataSet("stocktestcase_2/input.xml")
    @ShouldMatchDataSet(value = "stocktestcase_2/expected-result.xml", excludeColumns={"date"})
	public void test_case_2() {

		List<Stock> stocks = stockService.getAll();
		assertEquals(stocks.size(), 2);

		Stock acme = createStock("Acme", "ACM", 123.21D, new Date());
		Stock redhat = createStock("Red Hat", "RHC", 59.61D, new Date());

		stockService.save(acme);
		stockService.save(redhat);
	}

    /**
     * Test case: http://beitrag-confluence/VVL/testcases/testcase1
     *
     */
    @Test
    @UsingDataSet("stocktestcase_3/input.xml")
    @ShouldMatchDataSet(value = "stocktestcase_3/expected-result.xml", excludeColumns={"date"})
	public void test_case_3() {

		List<Stock> stocks = stockService.getAll();
		assertEquals(stocks.size(), 4);

		Stock acme = createStock("XAcme", "XACM", 123.21D, new Date());
		Stock redhat = createStock("XRed Hat", "XRHC", 59.61D, new Date());

		stockService.save(acme);
		stockService.save(redhat);
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

    /**
     * <p>Asserts that the actual stock's properties values are correct.</p>
     *
     * @param expected   the expected stock object
     * @param actual     the tested stock object
     */
    private static void assertStock(Stock expected, Stock actual) {

        assertEquals("Stock has invalid name property.", expected.getName(), actual.getName());
        assertEquals("Stock has invalid symbol property.", expected.getSymbol(), actual.getSymbol());
        assertEquals("Stock has invalid value property.", expected.getValue().doubleValue(),
                actual.getValue().doubleValue(), 0.01D);
    }
}
