== karaf-boot-sample-cdi ==

This sample shows how to define a CDI bundle.

= Design

TODO.

= Build

To build, simply do:

  mvn clean install

= Deploy

To deploy i:

* you can drop the generated jar file (target/karaf-boot-sample-cdi-1.0.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-cdi/1.0