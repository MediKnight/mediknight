# Mediknight
Powered by:

<p><a href="https://www.baltic-online.de/">
<img src="https://www.baltic-online.de/fileadmin/gfx/BO-LOGO.png" alt="alt text" width="100" heigh    t="50">
</a></p>

## What Does Medinight Do?

Mediknight is a Java based patient management system which is a standalone desktop GUI application where the diagnosis data of patients could be stored and printed. The terminologies are in German but you could translate it to other languages. Find the link to our license at the end of this document. We welcome contributions ([CONTRIBUTING.md](link)), so feel free to explore the project and see what you can add to it.



## Getting Started
These instructions will get a copy of the project up and running on your local machine for development.

### Prerequisites
What things you need to install Mediknight and how to install them


- [java8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

- [mysql](https://dev.mysql.com/doc/refman/5.6/en/installing.html)

	Note: Please check the above are already installed on your computer.
    If you already have then you are good to go otherwise go through the installation details by clicking on the above links (java8 and mysql).

- **Mediknight Database:**

When you fork the project (See [CONTRIBUTING.md](link) for details on that), there would a mysql-dumpfile named `mediknightdump.sql`. 
You have to convert into to mysql database i.e. 
												
	mysql -uroot mediknightDB > mediknightdump.sql
                                                
For details read [7.4.1](https://dev.mysql.com/doc/refman/5.7/en/using-mysqldump.html) to [7.4.5](https://dev.mysql.com/doc/refman/5.7/en/mysqldump-tips.html)
    
This is a test database for Mediknight. There is no password for the test database. We'll put this in `.gitignore` file so there would be no confusion of data changes. 

Note: For real data keep an eye yourself if you ever want to put your own real data. Make a copy of the test database and clean it to use it in production environment with a password.


## Built With

[Maven](https://maven.apache.org/) - Dependency Management

### Project Distribution:
The project is divided into four subprojects using Maven dependency management. The parent repository is this one [MediKnight](link)
The other project are `Debo, Borm` and `Flexgrid`. You will need to clone each of these projects and create a *workspace* in *eclipse*. Then you have import all the four projects into this workspace. We recommend to clone all the projects in one directory. 

## Integrated Development Environment (IDE)
[Eclipse](http://www.eclipse.org/) has been used for the development of this project. You can [download](https://www.eclipse.org/downloads/) it for free.


### Run The Project:
 You could run the project *Run as -> Java Application* by right clicking on `Mediknight` in your *package explorer* inside *eclipse* and select `Mediknight - main.java.de.baltic_online.mediknight`. You are good to go.


## Contributing
Please read [CONTRIBUTING.md](link-will-be-updated-here) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning (needs further clarification)

We use [somthing like SemVer](link) for versioning. For the versions available, see the tags on this repository.

## Owner
- Dagobert Michelsen - [dago](https://github.com/dago)
- Jochen Fritzenkoetter - [jfritzen](https://github.com/jfritzen)
- Tayyab Saeed - [MTayyabSaeed](https://github.com/MTayyabSaeed)

## License
This project is licensed under the [MIT License](link) - see the LICENSE.md file for details. We are not responsible for the irresponsible use of the software.

