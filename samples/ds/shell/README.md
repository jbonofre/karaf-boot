== karaf-boot-sample-shell ==

This sample provides new shell command in the Karaf shell console, that uses the hello service, provided by another
artifact (as karaf-boot-sample-service-provider-ds for instance).

= Design

The shell commands are exposed using the Karaf shell annotations.

A command is basically a class implementing Action interface, and we use @Service, @Command, @Argument, @Option Karaf
shell annotations.

The @Reference annotation uses the hello service.

= Build

To build, simply do:

  mvn clean install

= Deploy

You have to install a hello service provider first. Please deploy karaf-boot-sample-service-provider-ds first.

To deploy in Karaf:

* you can drop the generated jar file (target/karaf-boot-sample-shell-1.0.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-shell/1.0

= Usage

Once deployed, you can use the sample:hello command like:

karaf@root()> sample:hello world
Hello world !
