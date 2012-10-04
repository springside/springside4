1.How to create archetype?

a. create a archetype.properties, the content is 

package=org.springside.examples.quickstart
projectName=QuickStart
tablePrefix=ss_


b. create archetype
mvn archetype:create-from-project -Darchetype.properties=./archetype.properties -Darchetype.filteredExtentions=java,xml,jsp,properties,sql


c. modify files
  1.archetype-metadata.xml，remove the default value of the require properties
  2.pom.xml, the <name> node change to ${projectName}