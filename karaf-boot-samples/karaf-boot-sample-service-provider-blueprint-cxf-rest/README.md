== karaf-boot-sample-service-provider-blueprint-cxf-rest ==

This sample exposes a CXF rest service using blueprint.

= Design

The service "contract" is describe by the HelloServiceRest interface. It's a very simple service that expose one operation (hello).
The service client send a message (String) to the HelloServiceRest service , the HelloServiceRest then sends a the message to the karaf-boot-sample-service-provider-blueprint-api HelloService class who returns a Response for the HelloServiceRest to display.

The HelloServiceImpl is very simple: it prefixes the message with "Hello".

We use a blueprint XML descriptor (for blueprint annotations, see the corresponding sample) in order to expose the service.

= Build

To build, simply do:

  mvn clean install

= Deploy

To run the sample you should do the following in Apache Karaf prior to install the REST example:

> feature:repo-add cxf
> feature:install cxf
> bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-service-provider-blueprint-api/1.0.0-SNAPSHOT

* you can drop the generated jar file (target/karaf-boot-sample-service-provider-blueprint-cxf-rest-1.0.0-SNAPSHOT.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.bootkaraf-boot-sample-service-provider-blueprint-cxf-rest/1.0.0-SNAPSHOT


You can then view the result at:

http://localhost:8181/cxf/hello/karaf