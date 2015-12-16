package org.apache.aries.jpa.example.tasklist.feature;

/**
 * Example how a feature definition could look like
 */
@Feature(
         name="tasklist",
         version="1.0.0", 
         features = {"jpa", "transaction", "hibernate", "http"},
         configFile = "org.ops4j.datasource-tasklist.cfg"
)
public class TasklistFeature {
}
