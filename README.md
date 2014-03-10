This is a sample application which expose a REST API throught RestEasy-Netty 4.

This REST WS is exposed throught Spring Integration as a gateway

Moreover this sample application show how Swagger and Jolokia can be embedded into.


To build
========
```
mvn package
```


To launch
=========
untar the package (spring-integration-netty4-1.0-SNAPSHOT.tar.gz).

on linux, run: 
```
./bin/spring-integration-netty4 start
```


To stop
=======

on linux, run: 
```
./bin/spring-integration-netty4 stop
```

To test
=======

on linux, run:
```
curl -XPOST -H "Content-Type: application/json" -d '{"message": "hello", "time": "2014-03-05T10:55:39.835+01:00"}'  http://127.0.0.1:8081/sample/write
```
Files are written into /tmp directory.


