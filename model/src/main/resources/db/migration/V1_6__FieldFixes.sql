alter table mix modify column content longtext null;

alter table textcontent modify column author varchar(255) null default '';

alter table textcontent modify column created timestamp default CURRENT_TIMESTAMP;

alter table textcontent modify column modified timestamp default CURRENT_TIMESTAMP;

alter table textcontent modify column content longtext null;

alter table textcontent modify column alias varchar(60) default '';

