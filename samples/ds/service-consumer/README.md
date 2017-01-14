== karaf-boot-sample-ds-service-consumer ==

This sample binds and uses an OSGi service using declarative services (DS).

= Design

This artifact uses the hello service.

It uses the DS annotations to create a bean with a reference (@Reference) to the hello service.
In the HelloServiceClient bean, we use the @Activate annotation to execute a specific method.

= Build

  mvn clean install

= Deploy

We need to enable DS support and install the service as well as the consumer

  feature:install scr
  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-ds-service-consumer/1.0.0-SNAPSHOT
  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-ds-service-consumer/1.0.0-SNAPSHOT

