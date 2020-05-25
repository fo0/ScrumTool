![Build-Publish](https://github.com/fo0/ScrumTool/workflows/Build-Publish/badge.svg?event=push)

# ScrumTool

### Getting Started
Just download the latest app (via releases) and start it via
```
java -jar ScrumTool-VERSION.jar
``` 

## info for devs
add property `app.debug=true` to your i.e. eclipse run-configuration via `Override Properties` to see more informations

## read the database
You can easy read the h2-database file which is default located in the same directory like your `ScrumTool-VERSION.jar`
To access the file you just need to download the latest h2 client from the official site: https://h2database.com/h2-2019-10-14.zip
Unzip the .zip and use the client `h2-VERSION-.jar` from the `bin` directory.

The command to show the tables is i.e. 
``` sql
java -cp h2*.jar org.h2.tools.Shell -url jdbc:h2:file:./database -user sa -password sa -sql "show tables"
```

To print all Boards just execute the following statement
``` sql
java -cp h2*.jar org.h2.tools.Shell -url jdbc:h2:file:./database -user sa -password sa -sql "show * from tkbdata"
```
