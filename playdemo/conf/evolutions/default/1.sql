# Document schema
 
# --- !Ups
 
CREATE TABLE Document (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    uid varchar(255),
    owner varchar(255)  NULL,
    name varchar(255)  NULL,
    description varchar(255)  NULL,
    size bigint,
    lastModified bigint,
    dateCreated DateTime,
    path varchar(255) NOT NULL,
    folder boolean NOT NULL,
    PRIMARY KEY (id)
);
 
