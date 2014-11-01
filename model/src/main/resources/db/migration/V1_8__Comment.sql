   create table comment (
        id integer not null,
        comment varchar(255) not null,
        created datetime,
        moment datetime,
        type integer not null ,
        identifier integer not null,
        status integer not null ,
        author_id integer not null,
        parent_id integer,
        primary key (id)
    );

    alter table comment
        add constraint FK_COMMENT_AUTHOR
        foreign key (author_id)
        references user (id);

    alter table comment
        add constraint FK_COMMENT_COMMENT
        foreign key (parent_id)
        references comment (id);