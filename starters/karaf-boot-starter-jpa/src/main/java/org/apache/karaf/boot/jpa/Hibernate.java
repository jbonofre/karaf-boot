/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.boot.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.karaf.boot.jpa.PersistentUnit.ProviderProperty;

public interface Hibernate {

    @ProviderProperty("hibernate.query.substitutions")
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    @interface QuerySubstitutions {
        String value();
    }

    @ProviderProperty("hibernate.hbm2ddl.auto")
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    @interface Hbm2DdlAuto {
        Value value();
        enum Value {
            Validate("validate"),
            Update("update"),
            Create("create"),
            CreateDrop("create-drop");
            
            private String value;

            private Value(String value) {
                this.value = value;
            }

            public String toString() {
                return value;
            }
        }
    }

    @ProviderProperty("hibernate.dialect")
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    @interface Dialect {
        Value value();
        enum Value {
            Cache71,
            DataDirectOracle9,
            DB2390,
            DB2400,
            DB2,
            Derby,
            Firebird,
            FrontBase,
            H2,
            HSQL,
            Informix,
            Ingres10,
            Ingres9,
            Ingres,
            Interbase,
            JDataStore,
            Mckoi,
            MimerSQL,
            MySQL5,
            MySQL5InnoDB,
            MySQL,
            MySQLInnoDB,
            MySQLMyISAM,
            Oracle10g,
            Oracle8i,
            Oracle9,
            Oracle9i,
            Oracle,
            Pointbase,
            PostgresPlus,
            PostgreSQL,
            Progress,
            RDMSOS2200,
            SAPDB,
            SQLServer2008,
            SQLServer,
            Sybase11,
            SybaseAnywhere,
            SybaseASE15,
            Sybase,
            Teradata,
            TimesTen;

            public String toString() {
                return "org.hibernate.dialect." + super.toString() + "Dialect";
            }
        }
    }

}
