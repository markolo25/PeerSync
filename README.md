# Peer-Sync
## Created By Amanda Pan and Mark Mendoza

### Nodes when running/using
- This is a maven project, so theoretically you should be able to import it in any IDE that supports maven projects the entrypoint of the application being MainApp.Java, but for the best experience in compiling and running we used NetBeans.

- To insure the best experience try to only have one Network interface, if you have virtualbox installed disable the virtual interface as Java Remote MethodInvocation randomly uses one of your network interfaces, and hard coding the interface makes it impossible to demo the application at home, tethered in my phone, and at school without having to lookup my IP address everytime.

### Challanges Faced

- When deleting a file, the node that still has the file would update the node with a missing file giving it back the file recently deleted.

    - This was fixed by using Java RMI, to delete the file in the other nodes, and depended on the application's nature of reading through the entire directory structure each loop in the thread to stop tracking the file.

- Due to the nature of UDP Broadcast, and the nature of our application always listening for these broadcasts to find nodes, a node would add itself as a node.

    - This was fixed by creating a list of blacklisted IPs which are all the ips of the current node in all of it's interfaces before starting to listen for other node's broadcast.

### Program Flow

- The Application Starts off with a JavaFX window allowing you to broadcast, refresh the listbox and select a directory to sync.

- So you start off, broadcasting in all your nodes, and refreshing your list to ensure that all your nodes are accounted for, don't worry you can add more later. 

- When you select a directory Syncing starts, The application uses a function provided by Apache-io for mapping out the directories and subdirectories of your sync folder, which is then added to a proposed list.

- The Proposed List is then compared to the currently tracked list of peerFiles which is our File with some additional data making it easier to map the directories, and their equality being determined by the md5 of the file as well as the location of the file relative to the directory being synced.

- If the Proposed List has a file, that is not currently tracked it is sent to the other nodes by creating a thread whose job it is to make a TransferRecieve object in the other nodes whose constructor determines the source of the file and where it will go. After the thread is started the application then creates a TransferSend which is sends an OutputStream to the InputStreams created by the TransferRecieve.

- In order to create an object in the other nodes we use Java Remote Method Invocation, via an interface in our node, we call the methods on the remote nodes by performing a Naming.lookup, with the proper URI of the other nodes. allowing our current nodes to use functions whose signitures are stored in the RemoteInterface interface. 

- In order to delete a file, we use the FileNotFoundException that occurs when we delete a local file and handle it by performaing a Remote Method Invocation, to delete the file in the other nodes.

- For modifying files we add the modified file as if it was a new file, and is sent to the other nodes, and is then added to the proposed list, and when compared to currently tracked files, the old peerFile having a relativeDirectory, and md5 has that is no longer present is deleted off of the tracked files.

### Answers to recommended considerations since I don't know what else to talk about.

- How does the client discover other clients on the network?

    - Clients find each other by performing UDP broadcast, the node that broadcasts is known by the other nodes.

- How does the client deal with files of the same name, but different contents? Different timestamps?

    - Due to how modfications are implemented, the node who last saved will determine the state of the file in other nodes. Files with the same name can co-exist as long as they are in different directories, as we track their directory relative to the sync folder, and their md5.

- How does the client determine which files to sync in which order.

    - Due to all the files being stored in hashsets and iterated over as hashset there is no guarantee to which order they are synced. However since we are iterating over an Arraylist provided by the apache-io function, and iterating over said list and determining during the iteration whether or not to send it to the other nodes, we are syncing in the order of the top level directory down to n-subdirectories. 