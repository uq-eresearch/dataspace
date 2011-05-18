                    Seeding    the   Commons
                    Data Collection Registry
                    ========================
                    Last update: 2010-09-14


Introduction
~~~~~~~~~~~~
The Seeding the Commons project is developing a UQ policy on research data
management and establishing a UQ data collections registry. The project is
funded by Australian National Data Service (ANDS). The UQ data collections
registry will be linked to the ANDS Collection Registry, allowing UQ data
collections to be discovered in Research Data Australia.

The data-registry web application is developed with the following technology
stack:
    - Java 1.6
    - Hibernate
    
It relies directly on the following Open Source components:
    - Log4j for general logging
        (see: http://logging.apache.org/)
    - Spring Framework for use of MVC enabled Web framework
        (see: http://www.springframework.org/)
    - JUnit
        (see http://www.junit.org/)

Note that these packages may have other Open Source dependencies.

Directory Structure:

src:
    - source code for the web app

resources:
    - supplementary resources for source code such as spring configuration
      files

test:
    - unit testing for the web app

webapp:
    - web directory structure for the web archive

Other temporary directories containing derived, configured, and copied files
will be created by the build process.  These are not considered part of the
source distribution.


Setting Up the Database
~~~~~~~~~~~~~~~~~~~~~~~
To use PostgreSQL with the standard settings you need to:
- Create a database called "registry". Usually this means calling

                "createdb registry"

  on the shell as postgres user
- Create a user "registry" with password "registry" and grant full access to
  the "registry" database

- create a file "local/coralwatch.properties" in the project's root directory
  containing the line:

                persistenceUnitName=registry-pgsql

A common sequence of commands on a Ubuntu installation looks like this (with
abbreviated prompt):

                uqpbecke$ sudo su postgres
                postgres$ createdb registry
                postgres$ psql registry
                Welcome to psql 8.3.7, the PostgreSQL interactive terminal.

                Type:  \copyright for distribution terms
                       \h for help with SQL commands
                       \? for help with psql commands
                       \g or terminate with semicolon to execute query
                       \q to quit

                registry=# create user registry password 'registry';
                CREATE ROLE
                registry=# grant all on database registry to registry;
                GRANT

Using MySQL on localhost with standard credentials
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To use the Data Registry application with a MySQL database you need to:

- Create a database in Mysql named "registry" use the

                "CREATE DATABASE registry;"

  in the command line.

- Execute the following command to grant access "registry" database user:

                GRANT ALL ON registry.*
                TO 'registry'@'localhost'
                IDENTIFIED BY 'registry'
                WITH GRANT OPTION;

- create a file "local/registry.properties" in the project's root directory
  containing the line:

                persistenceUnitName=registry-mysql


Advanced configuration
~~~~~~~~~~~~~~~~~~~~~~

If you want to run against a non-local database or use different credentials,
you will need to edit the "persistence.xml" file which can be found in the
"META-INF" folder. This folder can be found in the data-registry-webapp
directory of the deployment or in the src/main/webapp folder.

If you want to use another database you will need to also add a dependency to
the matching JDBC driver in the "pom.xml" file.

Packaging
~~~~~~~~~
The project is set to be packaged as war file (see pom.xml). When packaging the
project, your should run the antrun:run goal to append the revision number to
the project's version number

Adding Solr search
~~~~~~~~~~~~~~~~~~
- Download solr from http://lucene.apache.org/solr/
- Unzip the tar file and copy [UNZIPPED_DIR]/example/webapps/solr.war to [TOMCAT_HOME]/webapps
- Copy [UNZIPPED_DIR]/example/solr to /opt and make sure it is readable by tomcat
- Add the following to JVM options in the [TOMCAT_HOME]/bin/catalina.sh:
        JAVA_OPTS=-Dsolr.solr.home=/opt/solr
- Create directory [TOMCAT_HOME]/bin/solr/data
- Add the jdbc connector jars for postgresql and mysql in [SOLR_HOME]/lib.
- Create data-config.xml file under [SOLR_HOME]/conf/ and add the datasource details and entities to index
<?xml version="1.0" encoding="UTF-8"?>
<dataConfig>
    <dataSource type="JdbcDataSource"
                driver="org.postgresql.Driver"
                url="jdbc:postgresql://localhost/registry"
                user="registry"
                password="registry"
            readOnly="true"
            autoCommit="false"
            transactionIsolation="TRANSACTION_READ_COMMITTED"
            holdability="CLOSE_CURSORS_AT_COMMIT"/>

    <document name="collection">
        <entity name="collectionversion"
                query="select * from collectionversion">
        </entity>
    </document>
</dataConfig>
        
- In the solrconfig.xml file add the following:

<requestHandler name="/dataimport" class="org.apache.solr.handler.dataimport.DataImportHandler" default="true">
    <lst name="defaults">
        <str name="config">data-config.xml</str>
    </lst>
</requestHandler>

- Add indexing configuration for the schema in the schema.xml file inside the <fields> tag. You need to use
  the <copyField> tags to concatenate different fields for full text search.
- Restart tomcat and check the logs
- Open the browser go to http://localhost:8080/solr/dataimport?command=full-import, This needs to be ran every now and
  then, maybe using some scheduling mechanism.
- Go to http://localhost:8080/solr/select?qt=standard&q=subject:sheep to search for sheep
- The query string should look like q=health&qt=standard. To do search on a particular field/column you can use
  something like q=label:health&qt=standard


Deployment
~~~~~~~~~~
TBA
                          




                          Good Luck!



                            -oOo-
