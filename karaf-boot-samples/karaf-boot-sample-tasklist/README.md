# Tasklist

## Install
cat https://raw.githubusercontent.com/jbonofre/karaf-boot/tasklist-sample/karaf-boot-samples/karaf-boot-sample-tasklist/org.ops4j.datasource-tasklist.cfg | tac -f etc/org.ops4j.datasource-tasklist.cfg
feature:install jpa transaction hibernate pax-jdbc-config pax-jdbc-pool-dbcp2 pax-jdbc-h2 http-whiteboard scr
install -s mvn:org.apache.karaf.boot/karaf-boot-sample-tasklist/1.0.0-SNAPSHOT

## Test
http://localhost:8181/tasklist
