/*
 * @(#)$Id: mediknight-database-creation.sql,v 2.13 2001/08/14 08:19:09 sml Exp $
 *
 * (C)2000 Baltic online Computer GmbH
 */

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
	erstdiagnose longtext,
	privatpatient char(1),
	primary key (id),
	index (name)
);

drop table if exists tagesdiagnose;
create table tagesdiagnose(
       id int auto_increment not null,
       patient_id int,
       datum date,
       text longtext,
       primary key (id)
);

drop table if exists verordnung;
create table verordnung(
       id int auto_increment not null,
       diagnose_id int,
       datum date,
       object longtext,
       primary key (id)
);

drop table if exists verordnungsposten;
create table verordnungsposten(
	gruppe int not null,
	nummer int not null,
        name varchar(100) not null,
	text longtext not null,
	primary key (gruppe, nummer)
);

drop table if exists rechnung;
create table rechnung(
       id int auto_increment not null,
       diagnose_id int,
       datum date,
       object longtext,
       text longtext,
       adresse longtext,
       gruss longtext,
       modus char(1),
       primary key (id)
);

drop table if exists rechnungsposten;
create table rechnungsposten(
	gebueh varchar(10) not null,
	goae varchar(10) not null,
	text longtext not null,
	preis double(16,4) not null,
	primary key (gebueh)
);

drop table if exists rechnungsgruppe;
create table rechnungsgruppe(
	abk varchar(10) not null,
	text longtext not null,
	object longtext,
	primary key (abk)
);


drop table if exists patientlock;
create table patientlock (
	patient_id int not null,
	aspekt varchar(250) not null,
	primary key (patient_id,aspekt)
);


drop table if exists benutzer;
create table benutzer (
       id int auto_increment not null,
       name varchar(40) not null,
       passwort varchar(40),
       zugriff int,
       bild blob,
       primary key (id)
);

drop table if exists benutzerprofil;
create table benutzerprofil (
       id int not null,
       bezeichner varchar(40) not null,
       wert longtext,
       primary key (id,bezeichner)
);


#UPDATE SYSTEM_CONNECTIONINFO SET VALUE=1000 WHERE KEY='IDENTITY'
#
#CREATE CACHED TABLE SEQUENCEID(ID INTEGER)
#GRANT ALL ON CLASS "de.bo.mediknight.domain.KnightObject" TO PUBLIC
#CREATE ALIAS LAST_INSERT_ID FOR "de.bo.mediknight.domain.KnightObject.getSequenceId"
#
#INSERT INTO SEQUENCEID VALUES(1000)
