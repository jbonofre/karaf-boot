== karaf-boot-sample-jpa ==

This sample shows how to define a JPA bundle and generate the persistent unit.

= Design

A Task entity is defined and annotated with the persistent unit annotations.

= Build

To build, simply do:

  mvn clean install

= Deploy

To deploy i:

* you can drop the generated jar file (target/karaf-boot-sample-jpa-1.0.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-jpa/1.0