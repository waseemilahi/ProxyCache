# ProxyCache
cached proxy service

The Basic Technologies used are:

Java 8 inside Eclipse Neon on the Windows 10 Machine.

The project is exported as an archive in the ProxyCache.zip file.

The File->Import->ArchiveFile will allow the import of the project.

The listener port is 8081.


The main method is in the class Named:  REceiverServer.java in the package:  tremorvideo.proxy.cache.

The helper classes are in tremorvideo.utils.nbqueue package.


Once you run the project in the eclipse, you can see the output in the console. 

The project also writes to the disk(house keeping after the fact). The log files are located in the 
logs(may have to create it locally) sub directory under the project home.

Once the project is running I used fidler to test the code by sending the requests to the address:

e.g.,

http://localhost:8081?url=http://www.tremorvideo.com/en
