# Simple File Storage README

## Sfs_tool rest server
The server is implemented as Scala Play rest server

### Requirements
1. java 11 & sbt https://www.scala-sbt.org/ need to be installed. sbt is able to down Scala and all dependencies.

### Build and Test
1. cd to the sfs_tool folder
2. run sbt in the terminal
3. change the configuration in conf/application.conf (see below)
4. run the command `test` in the sbt terminal to run tests
5. execute the command "run" to run the server. The default url is http::/localhost:9000

### Deployment

1. use the sbt `dist` command to create a distribution
2. this will produce a zip file `sfs_tool-1.0.zip`
3. expand the zip file into a folder
4. run the server:  `./bin/sfs_tool`. Take note of the following configurations. 
5. configure application secret
  `-Dplay.http.secret.key=ad31779d4ee49d5ad5162bf1429c32e2e9933f3b`
6. configure the file storage path (ensure this path exists)
    `-Dsfs.storagePath="/tmp/storage"`
7. configure the host address and port
   `-Dhttp.port=1234 -Dhttp.address=127.0.0.1`

8. other settings
<https://www.playframework.com/documentation/2.8.x/ProductionConfiguration#Server-configuration-options>
#### Example dist deployment 
`./bin/sfs_tool -Dplay.http.secret.key=ad31779d4ee49d5ad5162bf1429c32e2e9933f3b -Dsfs.storagePath="/tmp/storage" -Dhttp.port=9000`

or windows:
`sfs_tool.bat -Dplay.http.secret.key=ad31779d4ee49d5ad5162bf1429c32e2e9933f3b -Dsfs.storagePath="C:\\temp\\storage2" -Dhttp.port=9001`

It would be nice to have this deployed using docker <TODO>

#### Configuration application.conf (also by command line)
Configuration can also be done in the `application.conf` file
Example storage directory path
```  
sfs {
    # ensure this path exists and is accessable
    storagePath = "/tmp/storage"
}
```

## CLI sfs.py
Provides commands to upload, delete, and list files. Requires python to be installed.
type python `sfs.py --help for help`

### Upload command example
`python sfs.py --url http://localhost:9000 upload filename1 /tmp/myfile.txt`

### Delete command example
`python sfs.py --url http://localhost:9000 delete filename1`

### List command example
`python sfs.py --url http://localhost:9000 list`


## Other notes
Deployment would be better with docker. Also need to configure https.


