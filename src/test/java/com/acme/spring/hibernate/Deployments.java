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
package com.acme.spring.hibernate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * <p>A helper class for creating the tests deployments.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public final class Deployments {
    /**
     * <p>Creates new instance of {@link Deployments} class.</p>
     *
     * <p>Private constructor prevents from instantiation outside of this class.</p>
     */
    private Deployments() {
        // empty constructor
    }

    /**
     * <p>Creates new tests deployment.</p>
     *
     * @return the create archive
     */
    public static WebArchive createDeployment() {
        try {
            File stockServicesJar = Maven.resolver().loadPomFromFile("pom.xml")
                    .resolve("org.scott:stock-application")
                    .withoutTransitivity().asSingleFile();

            WebArchive archive = ShrinkWrap.create(WebArchive.class, "stock-application.war")
              .addClass(IntegrationHelper.class)
              .addClass(DatabaseHelper.class)
              .addClass(PostgresqlHelper.class)
              .addClass(Db2Helper.class)
              .addAsResource("applicationContext.xml")
              .addAsResource("datasets")
              .addAsLibrary(stockServicesJar)
              .addAsLibraries(springDependencies());

            for (Map.Entry<ArchivePath, Node> e: archive.getContent().entrySet()) {
                System.out.println(e.getKey());
            }
            return archive;
        }
        catch(RuntimeException x) {
            x.printStackTrace(System.err);
            throw x;
        }
    }

    /**
     * <p>Retrieves the dependencies.</p>
     *
     * @return the array of the dependencies
     */
    public static File[] springDependencies() {
        ArrayList<File> files = new ArrayList<File>();
        files.addAll(resolveDependencies("org.springframework:spring-web"));
        files.addAll(resolveDependencies("org.springframework:spring-context"));
        files.addAll(resolveDependencies("org.springframework:spring-orm"));
        files.addAll(resolveDependencies("org.springframework:spring-tx"));
        files.addAll(resolveDependencies("org.hibernate:hibernate-core"));
        //files.addAll(resolveDependencies("org.hibernate:hibernate-annotations:3.5.6-Final"));
        files.addAll(resolveDependencies("org.hibernate.common:hibernate-commons-annotations"));
        files.addAll(resolveDependencies("org.javassist:javassist"));
        files.addAll(resolveDependencies("org.postgresql:postgresql"));
        files.addAll(resolveDependencies("com.ibm.db2.jcc:db2jcc4"));
        files.addAll(resolveDependencies("org.dbunit:dbunit"));
        return files.toArray(new File[files.size()]);
    }

    /**
     * <p>Resolves the given artifact by it's name with help of maven build system.</p>
     *
     * @param artifactName the fully qualified artifact name
     *
     * @return the resolved files
     */
    public static List<File> resolveDependencies(String artifactName) {
        return Arrays.asList( Maven.resolver().loadPomFromFile("pom.xml").resolve(artifactName).withTransitivity().asFile());
    }
}
