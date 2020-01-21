<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

# karaf-boot-sample-ds-service-consumer

This sample binds and uses an OSGi service using declarative services (DS).

## Design

This artifact uses the hello service.

It uses the DS annotations to create a bean with a reference (@Reference) to the hello service.
In the HelloServiceClient bean, we use the @Activate annotation to execute a specific method.

## Build

  mvn clean install

## Deploy

We need to enable DS support and install the service as well as the consumer

  feature:install scr
  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-ds-service-consumer/1.0.0-SNAPSHOT
  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-ds-service-consumer/1.0.0-SNAPSHOT

