Area multi-threaded server - CA 2
========================
 by Anton Krug 20062210


![screenshot](/images/shot.jpg)

Features
--------

* Proper use of Java8. Utilization of **streams** together with **lambda expressions** and **method referencing**, to disconnect all clients quickly and cleanly before closing the GUI window.
    `    clientSockets.values().stream().forEach(this::clientDrop); `
	
	The following method reference
	`this::clientDrop` 
	is same as **lamda** expression below, but much cleaner:
    `(handlerThread) -> clientDrop(handlerThread)`

* Multithreaded server and thread safe syncrhonization.
    
* Separation of concerns, all the database configuration is removed from code and kept in separate **config.properties** file.



* Externalized Strings, all texts which comunicate with enduser are exported into **messagess.properties** allows faster proof reading, or easy multilangual support.

* GUI is separate from the worker Classes, this allows better JUnit testing.
    

Schema
------

Using the schema created from **RegisteredApplicants.sql**


Classes
-------
![screenshot](/images/uml.png)


Documentation
-------------

Javadoc (only showing documentation for public methods) generated under the **doc/index.html**. There is bit more comments in the git repository as well which will be located under [github repository](https://github.com/truhlikfredy/assignments-y4-areaMT) when it will be made public (till then it will show 404).

Metrics
-------

Did static code analysis and was changing the code depending on the results. Got the cyclomatic complexity average to very low values. This should resort to very few possible bugs. And because this means very low branching it allows to simpler tests. When there are only 1 or 2 branches, then it's easier to cover fully in the tests all conditions and branches. 

Metric                           | Total  | Mean  | Std. Dev.  
:--------------------------------| ------:| -----:| ----------:
Cyclomatic Complexity            |        |   1.6 |        1.2
Nested Block Depth               |        |   1.1 |        0.7
Packages                         |      2 |       |            
Classes                          |      7 |       |            
Methods                          |     73 |       |            
Lines of code (without comments) |    694 |       |   


Testing
-------
Was done with JUnit tests and together with manual GUI testing on Debian Linux and Windows 7. 
  
