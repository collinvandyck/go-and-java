# Overview

I've been curious about the different runtime characteristics of the Go and Java languages. I decided to code a simple authentication service in both languages and compare them both from a language/framework as well as a performance perspective.

## Requirements

The service is a web service that responds to GET requests to /authenticate.  Authentication data is passed via the Authorization HTTP header using the form "Basic [base64 encoded API key]". The service must:

* Decode the API key
* Lookup the user in a postgres databsae using the API key
* Serialize the user record to JSON. Date fields must be ISO-8601 formatted.
* Send the serialized JSON to the client

## Java Stack

I built the Java service using [Maven](http://maven.apache.org/) and the excellent [Dropwizard](http://dropwizard.codahale.com/) web services framework.  It provides, out of the box:

* Built-in YAML-based configuration support
* [Jackson](http://jackson.codehaus.org/) for efficient JSON serialization
* [JDBI](http://www.jdbi.org/) for database access
* [Jetty](http://jetty.codehaus.org/jetty/) for a performant HTTP server
* [Jersey](http://jersey.java.net/) for JAX-RS RESTful serices
* [Guava](http://code.google.com/p/guava-libraries/) for making your Java life more enjoyable

There are many other things it provides, like [Metrics](http://dropwizard.codahale.com/getting-started/#metrics-for-metrics), which are not part of this discussion, but definitely a MUST for web services in general.

## Go Stack

The Go service was built using the standard library, which provides:

* [Http Server](http://golang.org/pkg/net/http/) functionality
* [SQL](http://golang.org/pkg/database/sql/) access
* [JSON](http://golang.org/pkg/encoding/json/) codec
* [Base64](http://golang.org/pkg/encoding/base64/) codec

***

# Code Comparison

We'll break down how this service looks in both languages in these categories:

1. Service configuration
2. HTTP routing and dispatch
3. Database access
4. JSON Serialization

## Configuration

### Dropwizard Service Configuration

Dropwizard provides out of the box configuration support in the form of configuration classes which are unmarshalled from a YAML config file.

<script src="https://gist.github.com/3708969.js?file=gistfile1.yml"></script>
<script src="https://gist.github.com/3708961.js?file=gistfile1.java"></script> 

When the service comes up, Dropwizard provides the service with a validated configuration instance. You specify what configuration file to use on the command line.  In this case we specified a database configuration element which is explicitly declared in our AuthConfiguration.  We also specified some HTTP configuration which comes from the Configuration class that AuthConfiguration extends.

### Go Service Configuration

Go does not have built-in support for YAML, so we will use JSON instead.

<script src="https://gist.github.com/3709007.js?file=gistfile1.json"></script>
<script src="https://gist.github.com/3709014.js?file=gistfile1.go"></script>

To read in the configuration, ReadConfig() simply reads the contents of the configuration file, also specified on the command line, into a Config struct that mirrors the structure of the JSON using the built-in encoding/json package.

## HTTP Routing

### Dropwizard HTTP Routing

Dropwizard uses [Jersey](http://jersey.java.net/) which is an implementation of JAX-RS.  To create a RESTful endpoint, you create a resource class that has JAX-RS annotations which tells Jersey how to route incoming HTTP requests.

<script src="https://gist.github.com/3709088.js?file=gistfile1.java"></script>

We also need to add this resource in our Dropwizard service callback:

```environment.addResource(new AuthResource(userDAO));```


### Go HTTP Routing

Using the built-in net/http package, we can route incoming URIs to functions.  Go has first-class function support, so we can use function identifers when mapping URIs.

<script src="https://gist.github.com/3709123.js?file=gistfile1.go"></script>

Authenticate is an exported function that lives in auth_resource.go:

<script src="https://gist.github.com/3709135.js?file=gistfile1.go"></script>

## Database Access

### Dropwizard Database Access

The dropwizard-db module includes the JDBI library which we've used to access our users table.  We create a DAO, which is an interface whose methods map to DB queries (defined by method level annotations). In order to create User class instances we also provide a mapper which JDBI will call on each result set row returned.

<script src="https://gist.github.com/3709166.js?file=gistfile1.java"></script>

### Go Database Access

Using the built-in database/sql package, we can make a query on a database handle, and then scan values from a result row into a pre-allocated struct.

<script src="https://gist.github.com/3709176.js?file=gistfile1.go"></script>

One thing that you'll notice is that we build a User struct, which has string fields for Id, Email, and Name.  For non-string fields we can scan values from the row directly into the struct.  However, we use []byte slices to scan in the string rows.  This is because in Go, strings cannot be nil.  The empty representation of a string is the empty string (""). Scanning in a NULL column into a Go string will cause an error for this reason.

To get around this, we scan into a []byte slice, which can be nil, and then do a string([]byte) conversion to create the string value.  The way I've structured this particular struct, you cannot differentiate between null and "" string values coming from the database. There are ways around this, but I chose the simplest solution for this comparison.

## JSON Serialization

### Dropwizard JSON Serialization

Dropwizard provides convenient hooks into the web framework that allow you to simply return a User from your resource method at which point the User is JSON serialized and sent back to the client.  The serialization is driven by annotations on your value class, which allow you to do things like omitting null values, changing property names, excluding properties, etc.

<script src="https://gist.github.com/3709237.js?file=gistfile1.java"></script>

Note that by default, Date fields are serialized as timestamp long values.  In order to represent dates as ISO-8601 formatted values, we create two more methods, annotate them as JSON properties, and return the dates in the ISO-8601 format.

### Go JSON Serialization

The encoding/json library makes it easy to serialize structs. 

<script src="https://gist.github.com/3709261.js?file=gistfile1.go"></script>

Structs may be annotated with metadata that helps configure the serialization:

<script src="https://gist.github.com/3709266.js?file=gistfile1.go"></script>

Note that by default, Go serializes time.Time values as ISO-8601 formatted strings, so there is no extra work that needs to be done.


***

# Performance - TBD



