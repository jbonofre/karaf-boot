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
package sample.ds.service.consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import sample.ds.service.provider.HelloService;

@Component
public class HelloServiceClient implements Runnable {

    @Reference
    HelloService helloService;

    private Thread thread;

    @Activate
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Deactivate
    public void stop() {
        thread.interrupt();
    }

    public void run() {
        while (true) {
            System.out.println(helloService.hello("World"));
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                // nothing to do
            }
        }
    }

}
