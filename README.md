DataSpace Data Collections Registry
=========

Introduction
------------

The [Seeding the Commons @ UQ](http://www.itee.uq.edu.au/eresearch/projects/ands/stc) developed and deployed [DataSpace](http://dataspace.uq.edu.au/), a registry of University of Queensland's research data assets.

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

