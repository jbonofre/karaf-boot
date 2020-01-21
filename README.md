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

# Apache Karaf Boot

Karaf Boot provides an easy way to create artifacts ready to be deployed in Karaf, and also possibility to embed, configure, and bootstrap Karaf in a ready to run artifact.

You focus on your business, and Karaf Boot deals with all the "plumbing" for you.

Karaf Boot provides:

* set of dependencies providing annotations that you can use directly in your code: you focus on your business code, Karaf Boot does the rest
* a Maven plugin processing the annotations to create key turn artifacts
* an utility start to easily embed, configure, and bootstrap Karaf

## Building

To build Karaf Boot, just do:

    mvn clean install

Once done, you can build the samples using:

    mvn clean install -Psamples