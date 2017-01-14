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
