use mediknight;
drop table if exists patient;
create table patient(
	id int auto_increment not null,
	name varchar(40) not null,
	vorname varchar (30) not null,
	titel varchar (20),
	anrede varchar (10),
	adresse1 varchar (50), 
	adresse2 varchar (50), 
	adresse3 varchar (50),
	telefonprivat varchar (25),
	telefonarbeit varchar(25),
	fax varchar(25),
	handy varchar(25),
	email varchar(60),
	bemerkung longtext,
	achtung longtext,
	geburtsdatum date,
	erstdiagnosedatum date,
	privatpatient char(1),
	krankenkasse varchar(50),
	erstdiagnose longtext,
	primary key (id),
	index (name)
);
drop table if exists tagesdiagnose;
create table tagesdiagnose (
	id         int(11) default 0 auto_increment primary key,
	patient_id int(11) default 0,
	datum      date default '0000-00-00',
	text       longtext default null
);
drop table if exists verordnung;
create table verordnung (
	id          int(11) default 1 auto_increment primary key,
	diagnose_id int(11) default 0,
	datum       date default '0000-00-00',
	object      longtext default null
);
drop table if exists verordnungsposten;
create table verordnungsposten(
        name varchar(100) not null,
	text longtext not null,
	primary key (name)
);

drop table if exists rechnung;
create table rechnung (
	id          int(11) default 1 auto_increment primary key,
	diagnose_id int(11) default 0,
	datum       date default '0000-00-00',
	object      longtext default null
);

drop table if exists rechnungsposten;
create table rechnungsposten (
	gebueh     varchar(10) not null primary key,
        goae       varchar(10) not null,
	text       longtext not null,
        preis      double(16,4) not null
);

drop table if exists rechnungsgruppe;
create table rechnungsgruppe (
	abk        varchar(10) not null primary key,
	text       longtext not null,
	object      longtext default null
);

drop table if exists patientlock;
create table patientlock (
	patient_id int(11),
	zaehler    int
);
