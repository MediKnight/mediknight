create table tagesdiagnose (
	id         int(11) default 0 auto_increment primary key,
	patient_id int(11) default 0,
	datum      date default '0000-00-00',
	text       longtext default null
);
