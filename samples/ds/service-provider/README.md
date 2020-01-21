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

# karaf-boot-sample-ds-service-provider

Exposes and configures a service using declarative services

## Design

The service "contract" is described by the Hello interface. It's a very simple service that expose one operation (hello).
The service client sends a message (String) to the hello service and he gets a response.

Additionally the example shows how to inject configuration into a service by using the type safe configurations of DS 1.3.

The HelloServiceImpl is very simple: it prefixes the message with "Hello" and adds the configured name.

We use the @Component DS annotation on HelloServiceImpl implementation in order to expose the service.

## Build

  mvn clean install

## Deploy

We enable DS support and install the example

  feature:install scr
  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-service-provider-ds/1.0

