# Radio Tilos backend services

This project contains the java/scala based REST server for the Radio Tilos.

The core projects are:

* _service_: Contains the core REST server.
* _streamer_: NIO based HTTP endpoint to stream archived mp3 files to listen and download.
* _prerender_: utility to provider static version from the site for Facebook fetcher and Google search bots.
* _icecast_stat_: Collect listeners statistics from the icecast server.

The easyest way to start the backend services is:

```
cd service
mvn install -DskipTests
java -jar -Dmail.mock=true -Dconfig.file=../../tilos.properties target/service-jar-with-dependencies.jar
```

You need a running mongodb to start the backend service.








