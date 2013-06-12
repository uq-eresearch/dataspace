DataSpace Data Collections Registry
=========

Introduction
------------

The [Seeding the Commons @ UQ](http://www.itee.uq.edu.au/eresearch/projects/ands/stc) project developed and deployed [DataSpace](http://dataspace.uq.edu.au/), a registry of University of Queensland's research data assets.

The registry supports creation, storage and management of metadata records describing UQ research data holdings, the researchers and projects that created the data, and any online services that allow access or manipulation of the data.
The registry syndicates these descriptions to the ANDS [Research Data Australia](http://researchdata.ands.org.au/) service, allowing national promotion and discovery of UQ research data holdings.

**Note:** The DataSpace registry has been superseded by the http://research.data.uq.edu.au/ registry created under the [ANDS MS06 UQ Data Collections Registry Project](http://www.itee.uq.edu.au/eresearch/projects/ands/uq-dcr)

Technologies
------------

DataSpace is written in Java and built on the technology stack shown in the figure below. 

![DataSpace technologies](http://www.itee.uq.edu.au/eresearch/filething/get/11061/764px-Dataspace_technology_stack.png)

Technologies used include:
* **Database:** The system is developed so that it can sit on top of any relational database. It has been tested on both PostgreSQL  and MySQL .
* **Spring:** The system uses Spring 2.5 Framework for connecting components of the application. 
* **Hibernate**  is used as an object-relational mapping framework. We use Hibernate annotations for defining the schema, relationships and constraints.
* **Apache Abdera:** is an implementation of the Atom Syndication Format and Atom Publishing Protocol. We use it to generate atom records from database records. 
* **XSLT 2.0:** is used to transform Atom output into different formats as shown in the diagram. The default format is HTML. The user can ask for specific format by adding an accept header or "repr" parameter to their request. 
* **Apache Solr:** is the search engine we use to index the database of metadata records. 
* **Quartz:** is to schedule Solr indexes of the database.
* **HTTP Clients:** The system is built so it is a HTTP service that can be communicated with by any HTTP clients. In theory if you have a valid ATOM entry that passes our requirements, you can send POST, PUT, GET and DELTE HTTP requests to perform actions with the records. 
* **JavaScript:** is used in Browsers to add interactivity to the HTML versions of the records. For example we use JQuery  to make the editing forms interactive. We also use OpenLayers  and Google Maps JavaScript APIs to show maps displays of any geospatial locations in the metadata records.

Code structure
--------------

The project was created by a maven web app archetype. It contains the pom.xml file and the usual structure of src/main and src/test. The main directory contains the usual three sub directory java, resources and webapp. The following list describes each sub directory's content in details:

* java directory
  * All the following packages are contained in the main package net.metadata.dataspace
  * app package contains java classes that are used application wide such as Constants, Initializers and Application Configurations
  * atom package contains java classes for processing atom input and output. It contains adapter sub-package which contains extensions of Abdera's EntityCollectionAdapter. It also contain a util package which contains utility classes for processing atom input and output. The adapters make use of the HttpMethodHelper class which then delegates work to the other helper classes depending on the operation it is executing. The writer package contains a Transformer that makes use of XSLT files to transform atom output into other format such as HTML, RIF-CS or RDF.
  * auth contains Authentication and Authorization code. The main two classes in this package are the AuthenticationManager and AuthorizationManger. The AuthenticationManager manages authentications by users as well as makes use of LDAPUtil class under the util package to extract information about users of the system from UQ LDAP service. The AuthorizationManager is used by the atom adapter and util classes to determine ACLs for users before performing operations
  * controller package contains implementation of Spring MVC controllers for displaying other html pages in the system such as the search and browse pages.
  * data package contains java all the classes dealing with the database
    * access package contains DAO classes. Under impl sub-package there is an abstract dao that performs common queries. Most of the other DAOs extend this DAO and they may have their own methods if they need to run specific queries. The DAOs only perform Read-Only queries. manager package includes a DaoManager for managing access to the DAOs through out the application and an EntityCreator for creating new entities.
    * connector package only contains a connector class which creates an entity manager that can be used through out the application for persisting and updating entities.
    * model package contains classes/entities defining the data model. Each of these entities (is a table) uses Hibernate annotations to specify relationships and constraints. The entities are divided into further sub-packages. record package contains the classes for defining the main entities in the system. version package contains classes defining version records of the main entities. context contains secondary entities used to store context information related to the main entities. types is Enums defining records types.
  * sequencer package contains Sequencer classes that maintain incremental sequence numbers used as URI keys for the records in the database and versions of the records.
  * oaipmh contains implementation code for producing OAI-PMH syndication formats from the system. The the RIFCSCrosswalk class makes use of the XSLT files used to convert atom entries into RIF-CS format and embed the output in an OAI-PHM document. The RIFCSOaiCatalog is the responsible class for responding to OAI-PMH verbs as well as selecting which records in the system are to be used in the OAI-PMH syndication.
  * servlets package contains Servlets for handling HTTP requests. The RegistryServiceProviderServlet handles requests about the records in the system. It delegates the requests to the atom adapters mentioned above. It uses extra RquestProcessors (on top of those provided by Abdera) in the package processor to determine which HTTP method is allowed for each request. The OAIPMHServlet handles OAI-PHM requests and delegates work to classes mentioned above.
  * solr contains classes used for talking to solr search engine. These classes are used by the RegistryInitializer to send an indexing command to the solr search engine.
  * util contains utility classes used application wide.
* resources contains non-java code and the application's configuration files such as registry.properties. The configuration attributes should be self explanatory.
  * conf contains properties files for the OAI-PMH service, Solr indexing and Spring bean configurations.
  * files contains non-java code (mainly XSLT) for converting atom xml into other formats. The sub-packages are divided based on the output format produced by the transformations. The other package contains rdf files that contain the anzsrc codes that are used to inject subject codes into the database so they can be used in subject lookup.
  * META-INF contains persistence.xml file for defining persistence units.
* webapp a directory for web resources such as javascript, JSPs, images, icons
  * WEB-INF contains the web.xml file is where the applications context is started and configured. The springmvc-servlet.xml file is a spring configuration for wiring up the Spring MVC section of the application.
  * doc contains help and documentation files
  * js contains JavaScript files used in the html pages of the applications. It contains JavaScript used for displaying Google Maps. It also contains a sub-directory containing an AJAX framework code used to interact with the Solr search engine through AJAX HTTP calls.
  * jsp contains java server page definitions

