alter table bookmark 
        add column full_episode TINYINT(1) not null;

    alter table bookmark 
        add column karma integer;

    alter table bookmark 
        add column selected TINYINT(1) not null;

    alter table user -
        add column role integer;