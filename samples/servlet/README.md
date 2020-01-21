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

# karaf-boot-sample-servlet

This sample shows how to easily create a servlet, ready to be deployed in Karaf.

## Design

The SampleServlet is servlet containing the @WebServlet annotation.

This servlet is directly deployed by Karaf as soon as it's deployed.

## Build

To build, simply do:

  mvn clean install

## Deploy

To deploy in Karaf, you have to enable the web support by installing the http and http-whiteboard features:

  feature:install http
  feature:install http-whiteboard

Once http features installed:

* you can drop the generated jar file (target/karaf-boot-sample-servlet-1.0.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-servlet/1.0