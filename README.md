# Introduction

I've been curious about the different runtime characteristics of the Go and Java languages. I decided to code a simple authentication service in both languages and compare them.

## Requirements

The service is a web service that responds to GET requests to /authenticate.  Authentication data is passed via the Authorization HTTP header using the form "Basic [base64 encoded API key]". The service must:

* Decode the API key
* Lookup the user in a postgres databsae using the API key
* Serialize the user record to JSON. Date fields must be ISO-8601 formatted.
* Send the serialized JSON to the client

## Java Stack

I built the Java service using [Maven](http://maven.apache.org/) and the excellent [Dropwizard](http://dropwizard.codahale.com/) web services framework.  It provides, out of the box:

* Built-in YAML-based configuration support
* [Jackson](http://jackson.codehaus.org/) for extremely efficient JSON serialization
* [JDBI](http://www.jdbi.org/) for database access
* [Jetty](http://jetty.codehaus.org/jetty/) for a performant HTTP server
* [Jersey](http://jersey.java.net/) for JAX-RS RESTful serices
* [Guava](http://code.google.com/p/guava-libraries/) for making your Java life more enjoyable as well as some codecs we will use

There are many other things it provides, like [Metrics](http://dropwizard.codahale.com/getting-started/#metrics-for-metrics), which are not part of this discussion, but definitely a MUST for web services in general.

## Go Stack

I've limited the Go implementation to using the standard library, as it provides:

* [Http Server](http://golang.org/pkg/net/http/) functionality
* [SQL](http://golang.org/pkg/database/sql/) access
* [JSON](http://golang.org/pkg/encoding/json/) codec
* [Base64](http://golang.org/pkg/encoding/base64/) codec

## Disclaimer

I've spent most of my professional life coding against the JVM.  I've only been coding Go for the past month or two. As such, if you have been doing any Go programming for a while, you'll probably see that I've done something stupid, which is probably true.  And you may also see that I'm doing something supid in Java, which is also probably true.  I'd really appreciate any constructive criticism of my code.

***

# Performance

In order to test the performance, I used the [http_perf](https://github.com/collinvandyck/http_perf) library I wrote in Go to measure response times.  I decided a couple of different scenarios were relevant:

* 1000 requests, concurrency = 1
* 100,000 requests, concurrency = 1
* 1000 requests, concurrency = 4
* 100,000 requests, concurrency = 4

In these tests, I'm measuring:

* min response time
* max response time
* average response time
* standard deviation

The hardware I'm running this on is a 2012 Macbook Pro, OS X 10.8.1, 2.6GHz i7, 16GB RAM.

To perform each request, the code I run is:

    http_perf -h "Authorization: Basic [base 64 encoded api key]=" -url "http://localhost:8080/authenticate" -iterations [iterations] -concurrency [concurrency]

Each app server is restarted before each start.    
    
### Test 1: 1,000 requests, concurrency = 1

#### Java
* min: 1.20ms
* max: 126.86ms
* avg: 1.92ms
* std dev: 3.97ms

#### Go
* min: 0.51ms
* max: 8.74ms
* avg: 0.64ms
* std dev: 0.28ms

### Test 2: 1,000,000 requests, concurrency = 1

#### Java

* min: 0.42ms
* max: 129.06ms
* avg: 0.50ms
* std dev: 0.52ms

#### Go

* min: 0.49ms
* max: 41.11ms
* avg: 0.62ms
* std dev: 0.33ms


### Test 3: 1,000 requests, concurrency = 4

#### Java

* min: 1.41ms
* max: 138.64ms
* avg: 3.02ms
* std dev: 8.60ms

#### Go

* min: 0.75ms
* max: 12.40ms
* avg: 1.75ms
* std dev: 1.76ms

### Test 4: 1,000,000 requests, concurrency = 4

#### Java

* min: 0.46ms
* max: 177.25ms
* avg: 0.68ms
* std dev: 1.07ms

#### Go

* min: 0.60ms
* max: 50.80ms
* avg: 1.68ms
* std dev: 1.73ms

***

# Conclusion

The Java and Go solutions perform similarly, if not for some spikes that occur on the Java side. However, the means tend to be somewhat inline.  I hope that this might give those debating whether or not to use Java or Go for a similar type of service the motivation to consider things like code clarity, brevity, and general maintainability of a solution over performance considerations.



