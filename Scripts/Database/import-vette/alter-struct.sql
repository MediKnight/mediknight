#use mo;

create table benutzer (
       id int auto_increment not null,
       name varchar(40) not null,
       passwort varchar(40),
       zugriff int,
       bild blob,
       primary key (id)
);

create table benutzerprofil (
       id int not null,
       bezeichner varchar(40) not null,
       wert longtext,
       primary key (id,bezeichner)
);

drop table if exists patientlock;
create table patientlock (
	patient_id int not null,
	aspekt varchar(250) not null,
	primary key (patient_id,aspekt)
);

alter table rechnung
      add text longtext,
      add adresse longtext,
      add gruss longtext,
      add modus char(1);

update rechnung set modus='N';

drop table if exists verordnungsposten;
create table verordnungsposten(
	gruppe int not null,
	nummer int not null,
        name varchar(100) not null,
	text longtext not null,
	primary key (gruppe, nummer)
);
