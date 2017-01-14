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
package sample.shell;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import sample.ds.service.provider.HelloService;

@Service
@Command(scope = "sample", name = "hello", description = "The hello command")
public class HelloCommand implements Action {

    @Reference
    public HelloService helloService;

    @Argument(index = 0, name = "arg", description = "This is the message argument", required = true, multiValued = false)
    public String arg;

    @Option(name = "opt", description = "This is an option", required = false, multiValued = false)
    public String opt;

    public Object execute() throws Exception {
        if (opt != null)
            System.out.println("Opt is " + opt);
        System.out.println(helloService.hello(arg));
        return null;
    }

}
