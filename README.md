# MediKnight


## What Does MediKnight Do?

MediKnight is a Java based patient management system which is a standalone desktop GUI application where the diagnosis data of patients could be stored and printed. It is useful for clinics to manage patients data. The terminologies are in German but you could translate it to other languages. Find the link to our license at the end of this document. We welcome contributions ([CONTRIBUTING.md](CONTRIBUTING.md)), so feel free to explore the project and see what you can add to it.


## Getting Started
These instructions will get a copy of the project up and running on your local machine for development.

### Prerequisites
What you need to install MediKnight and how to install them?

Here is a simple guide to set up your environment for this project.

- [java8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

- [mysql](https://dev.mysql.com/doc/refman/5.6/en/installing.html)

	Note: Please check the above if they are already installed on your computer.
    If you already have them, you are good to go otherwise go through the installation details by clicking on the above links (java8 and mysql).

- **MediKnight Database:**

When you fork the project (See [CONTRIBUTING.md](CONTRIBUTING.md) for details on that), there would a mysql-dumpfile named `mediknightdump.sql`. 
You have to convert it into mysql database i.e. 
												
	mysql -uroot mediknight > mediknightdump.sql
                                                
For details read mysql documentation from [7.4.1](https://dev.mysql.com/doc/refman/5.7/en/using-mysqldump.html) to [7.4.5](https://dev.mysql.com/doc/refman/5.7/en/mysqldump-tips.html)
    
This is a test database for MediKnight. There is no password for the test database. We'll put this in `.gitignore` file so there would be no confusion of data changes from developers branches.

Note: We expect you that you to know how to take care of your data. For real data keep an eye yourself if you ever want to put your own real data. Make a copy of the test database and clean it to use in production environment with a password.


## Built With

[Maven](https://maven.apache.org/) - Dependency Management



### Project Distribution:
The project is divided into four subprojects using Maven dependency management. The parent repository is this one **[MediKnight](https://github.com/MediKnight/mediknight.git)**
The other project are **[borm](https://github.com/MediKnight/debo)**, **[Debo](https://github.com/MediKnight/debo)**, and **[Flexgrid](https://github.com/MediKnight/flexgrid)**. You will need to clone each of these projects and create a *workspace* in *eclipse*. Then you have import all the four projects into this workspace. We recommend to clone all the projects in one directory. 

## Integrated Development Environment (IDE)
[Eclipse](http://www.eclipse.org/) has been used for the development of this project. You can [download](https://www.eclipse.org/downloads/) it for free.


### Running The Project:
 You could run the project *Run as -> Java Application* by right clicking on `mediknight` in your *package explorer* inside *eclipse* and select `MediKnight - main.java.de.baltic_online.mediknight`. You are good to go.


## Contributing
Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.


## Owners
- Dagobert Michelsen - [dago](https://github.com/dago)
- Jochen Fritzenkoetter - [jfritzen](https://github.com/jfritzen)
- M Tayyab Saeed - [MTayyabSaeed](https://github.com/MTayyabSaeed)


## License and copyright
© Dagobert Michelsen
 
This project is licensed under the [MIT License](LICENSE). 




