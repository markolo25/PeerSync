# PeerSync
## Created By Amanda Pan and Mark Mendoza

### Notes when running/using
- This is a maven project, so theoretically you should be able to import it in any IDE that supports maven projects the entry point of the application being MainApp.Java, but for the best experience in compiling and running we used NetBeans.

- To insure the best experience try to only have one Network interface, if you have VirtualBox installed disable the VirtualBox network interface as Java Remote Method Invocation randomly uses one of your network interfaces, and hard coding the Naming makes the application less portable, as your Local IP address is dependent on the network you are in, which makes it hard to test and work on if you're outside your house. 

### Challenges Faced

- When deleting a file, the node that still has the file would update the node with a missing file giving it back the file recently deleted.

    - This was fixed by using Java RMI, to delete the file in the other nodes, and depended on the application's nature of reading through the entire directory structure each loop in the thread to stop tracking the file.

- Due to the nature of UDP Broadcast, and the nature of our application always listening for these broadcasts to find nodes, a node would add itself as a node.

    - This was fixed by creating a list of blacklisted IPs which are all the ips of the current node in all of it's interfaces before starting to listen for other node's broadcast.

- Early in the project we had issues with Concurrency, and needing things to happen at the same time.

    - We started implementing all of our classes implementing Runnable allowing us to run them in threads, but this led to issues of being unable to get data on time when we need it, and didn't want to resort to having the application halt when such cases occurred, so we had to write on paper what we needed to do concurrently and what can wait, as there were shared resources like ports we had to worry about.

    - Originally we had a version of the program which transferred the files in parallel opening sockets for each file utilizing different ports, but this proved too much, and settled with doing it serially, finishing each file with a call to TransferServer.send() function which when successful closed the port that will be used by the next file.

- When it came to this project, I couldn't find an ideal way to implement this project as a whole as I haven't done anything related to peer to peer communication.

    - To Overcome this, Amanda and I started out with 3 different applications, I had a simple File Transfer Program, and a calculator using RMI, and Amanda had an application that listened to UDP broadcasts, managing to figure out how to listen only to our Application's broadcasts. The application just interface with each other using the PeerSyncModel class to handle file detection and sync, and the JavaFX application to initiate a broadcast, create a Listener Thread and initializing the PeerSyncModel with a directory.

    - Another example of this is the fact that we started this application in Node.JS, thinking that "Oh this assignment involves networking, Node.JS should be great for that since it's made for the web in mind" However, this proved to be a harder route, as we couldn't figure out how to create multiple threads to perform tasks without, getting packages from npm, which at the time we were afraid to use, as we thought we had to implement everything.  
 
    
<!-- ### If I can do it over again...
- If I can do this project over again, I'd probably look for a framework that provides event based handling of files, providing functions like directoryOnChange, or fileOnChange since a big bulk of the Application was scanning the directory and handling changes in the directory. Another thing I would do would probably try to parallel-ize the transfers like originally tried to,by utilizing some sort of collection of known usable ports. Aside from that UDP Broadcast is pretty cool, and would keep it, perhaps just make it automatic where all you had to do was select the directory, but the need for the list box to displays the IPs of peers made it kinda messy when we were integrating everything together. 
!-->



### Program Flow

- The Application Starts off with a JavaFX window allowing you to broadcast, refresh the ListBox and select a directory to sync.

- So you start off, broadcasting in all your nodes, and refreshing your list to ensure that all your nodes are accounted for, don't worry you can add more later. 

- When you select a directory Syncing starts, The application uses a function provided by Apache Commons IO for mapping out the directories and subdirectories of your sync folder, which is then added to a proposed list.

- The Proposed List is then compared to the currently tracked list of peerFiles which is our File with some additional data making it easier to map the directories, and their equality being determined by the md5 of the file as well as the location of the file relative to the directory being synced.

- If the Proposed List has a file, that is not currently tracked it is sent to the other nodes by creating a thread whose job it is to make a TransferReceive object in the other nodes whose constructor determines the source of the file and where it will go. After the thread is started the application then creates a TransferSend which is sends an OutputStream to the InputStreams created by the TransferReceive.

- In order to create an object in the other nodes we use Java Remote Method Invocation, via an interface in our node, we call the methods on the remote nodes by performing a Naming.lookup, with the proper URI of the other nodes. allowing our current nodes to use functions whose signatures are stored in the RemoteInterface interface. 

- In order to delete a file, we use the FileNotFoundException that occurs when we delete a local file and handle it by performing a Remote Method Invocation, to delete the file in the other nodes.

- For modifying files we add the modified file as if it was a new file, and is sent to the other nodes, and is then added to the proposed list, and when compared to currently tracked files, the old peerFile having a relativeDirectory, and md5 has that is no longer present is deleted off of the tracked files.

### Answers to recommended considerations since I don't know what else to talk about.

- How does the client discover other clients on the network?

    - Clients find each other by performing UDP broadcast, the node that broadcasts is known by the other nodes.

- How does the client deal with files of the same name, but different contents? Different timestamps?

    - Due to how modifications are implemented, the node who last saved will determine the state of the file in other nodes. Files with the same name can co-exist as long as they are in different directories, as we track their directory relative to the sync folder, and their md5.

- How does the client determine which files to sync in which order.

    - Due to all the files being stored in HashSet and iterated over as such there is no guarantee to which order they are synced. However since we are iterating over an ArrayList provided by the Apache Commons IO function, and iterating over said list and determining during the iteration whether or not to send it to the other nodes, we are syncing in the order of the top level directory down to n-subdirectories. 

