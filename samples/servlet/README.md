== karaf-boot-sample-servlet ==

This sample shows how to easily create a servlet, ready to be deployed in Karaf.

= Design

The SampleServlet is servlet containing the @WebServlet annotation.

This servlet is directly deployed by Karaf as soon as it's deployed.

= Build

To build, simply do:

  mvn clean install

= Deploy

To deploy in Karaf, you have to enable the web support by installing the http and http-whiteboard features:

  feature:install http
  feature:install http-whiteboard

Once http features installed:

* you can drop the generated jar file (target/karaf-boot-sample-servlet-1.0.jar) in the
Karaf deploy folder
* in the Karaf shell console, do:

  bundle:install -s mvn:org.apache.karaf.boot/karaf-boot-sample-servlet/1.0