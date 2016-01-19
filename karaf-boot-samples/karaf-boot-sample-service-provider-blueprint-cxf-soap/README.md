== karaf-boot-sample-service-provider-blueprint-cxf-soap ==

This sample exposes a CXF soap service using blueprint.

= Design

The service "contract" is describe by the HelloServiceSoap interface. It's a very simple service that expose one operation (hello).
The service client send a message (String) to the HelloServiceSoap service , the HelloServiceSoap then sends a the message to the karaf-boot-sample-service-provider-blueprint-api HelloService class who returns a Response for the HelloServiceSoap to display.

The HelloServiceImpl is very simple: it prefixes the message with "Hello".

We use the @Component DS annotation on HelloServiceImpl implementation in order to expose the service.

You don't think anything else: karaf-boot will generate all the plumbing for you, and you will directly have a ready
to use artifact.

= Build

To build, simply do:

  mvn clean install

= Deploy

To run the sample you should do the following in Apache Karaf prior to install the REST example:

> feature:repo-add cxf
> feature:install cxf
> bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-service-provider-blueprint-api/1.0.0-SNAPSHOT

* you can drop the generated jar file (target/karaf-boot-sample-service-provider-blueprint-cxf-soap-1.0.0-SNAPSHOT.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-service-provider-blueprint-cxf-soap/1.0.0-SNAPSHOT


You can then view the WSDL at:

http://localhost:8181/cxf/hello?wsdl