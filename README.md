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

Helper/Wrapper Classes

TransferObject -> This class holds the socket,url and header information for the original request.
ResponseObject -> This class holds the response information for the response to be sent to the originator.
CacheHashMap  -> It extends the LinkedHaskMap and overrides the removeeldest method. The value field is of the type ResponseObject
Non-Blocking Queue
There are three other classes in the utils package. They basically implement the non blocking queue used to transport the transfer object accross different "threads".

As mentioned above the main method sits in the ReceiverServer class.

This is a server daemon that listens on a port for incoming traffic.
When there is some traffic, it accepts the traffic and hands it off to the ReceiverThread (Thread) class. The ReceiverThread class
then reads in the incoming stream from the socket and parses the request to get the url field and the request headers and pushes then to an instance of the "TransferObject" class. This object is then added to the non blocking concurrent queue. and returns.

The transferqueue is also polled by the ProcessServer (initialized in the ReceiverServer). If there is a transferObject on the queue, the object is pulled and handed down to the ProcessThread class. The rest of the work is done by this thread class.

The ProcessThread class first looks into the cache (key being the url field of the transferObject) and if the cache contains the key then it simply returns the response.
If the cache doesn't have the key(url) then it connects to the url and gets the response back. And this response is sent out to the originator via the socket in the transferobject.

Finally, if the response was 200 then we put it to the cache. The removeeldest method insures that the oldest entry will be removed if the max limit is reached. The max limit and port is given in the main method in the ReceiverServer class and the max limit is set in the same class later when it is passed as an argument to the cacheMap object.

All the error cases, plus the final url(cache or new) is logged in the log files under the logs folder, the exceptions are also displayed on the System.out.
