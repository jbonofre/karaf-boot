== karaf-boot-sample-ds-service-provider ==

Exposes and configures a service using declarative services

= Design

The service "contract" is described by the Hello interface. It's a very simple service that expose one operation (hello).
The service client sends a message (String) to the hello service and he gets a response.

Additionally the example shows how to inject configuration into a service by using the type safe configurations of DS 1.3.

The HelloServiceImpl is very simple: it prefixes the message with "Hello" and adds the configured name.

We use the @Component DS annotation on HelloServiceImpl implementation in order to expose the service.

= Build

  mvn clean install

= Deploy

We enable DS support and install the example

  feature:install scr
  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-service-provider-ds/1.0

