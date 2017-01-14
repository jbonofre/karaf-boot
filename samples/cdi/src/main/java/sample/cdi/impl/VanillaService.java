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
package sample.cdi.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;
import sample.cdi.IceCreamService;

@OsgiServiceProvider(classes = { VanillaService.class, IceCreamService.class })
@Properties(@Property(name = "flavour", value = "vanilla"))
@ApplicationScoped
class VanillaService implements IceCreamService {

    private boolean initialized;

    @PostConstruct
    public void init() {
        initialized = true;
    }

    public String getFlavour() {
        if (!initialized) {
            throw new AssertionError("VanillaService is not initialized");
        }
        return "Vanilla";
    }
}