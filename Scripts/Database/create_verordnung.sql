create table verordnung (
	id          int(11) default 1 auto_increment primary key,
	diagnose_id int(11) default 0,
	datum       date default '0000-00-00',
	object      longtext default null
);
