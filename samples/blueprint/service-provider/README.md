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

== karaf-boot-sample-service-provider-blueprint ==

This sample exposes an OSGi service using blueprint.

= Design

The service "contract" is describe by the Hello interface. It's a very simple service that expose one operation (hello).
The service client send a message (String) to the hello service and he gets a response.

The HelloServiceImpl is very simple: it prefixes the message with "Hello".

We use a blueprint XML descriptor (for blueprint annotations, see the corresponding sample) in order to expose the service.

= Build

To build, simply do:

  mvn clean install

= Deploy

* you can drop the generated jar file (target/karaf-boot-sample-service-provider-blueprint-1.0.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-service-provider-blueprint/1.0
