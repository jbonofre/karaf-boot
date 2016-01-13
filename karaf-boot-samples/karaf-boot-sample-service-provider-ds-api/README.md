== karaf-boot-sample-service-provider-ds-api ==

This sample exposes an OSGi service using the Karaf util classes and annotation.

= Design

The service "contract" is describe by the HelloService interface. It's a very simple service that expose one operation (sayHello).
The service client send a message (String) to the helloService and he gets a response.

The HelloServiceImpl is very simple: it prefixes the message with "Hello ".

We use the @Component DS annotation on HelloServiceImpl implementation in order to expose the service.

You don't think anything else: karaf-boot will generate all the plumbing for you, and you will directly have a ready
to use artifact.

= Build

To build, simply do:

  mvn clean install

= Deploy

To run the sample you should do the following in Apache Karaf prior to install the REST example:

> feature:install scr (Declarative service support)  

Once scr feature installed:

* you can drop the generated jar file (target/karaf-boot-sample-service-provider-ds-api-1.0.0-SNAPSHOT.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-service-provider-ds-api/1.0.0-SNAPSHOT