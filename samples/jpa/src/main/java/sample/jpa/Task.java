/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package sample.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.karaf.boot.jpa.Hibernate;
import org.apache.karaf.boot.jpa.PersistentUnit;
import org.apache.karaf.boot.jpa.Property;
import org.apache.karaf.boot.jpa.Provider;

@PersistentUnit( //
    name = "task", provider = Provider.Hibernate, properties = //
    {
     @Property(name = "hibernate.hbm2ddl.auto", value = "create-drop"),
     @Property(name = "javax.persistence.jdbc.driver", value = "org.apache.derby.jdbc.EmbeddedDriver"),
     @Property(name = "javax.persistence.jdbc.url", value = "jdbc:derby:memory:DSFTEST;create=true")
    })
@Hibernate.Dialect(Hibernate.Dialect.Value.HSQL)
@Entity
public class Task {
    @Id
    Integer id;
    String title;

    public Task() {
    }

    public Task(Integer id, String title) {
        super();
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = new Integer(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
