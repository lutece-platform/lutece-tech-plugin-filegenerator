--liquibase formatted sql
--changeset filegenerator:create_db_filegenerator.sql
--preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS filegen_temporary_file;
CREATE TABLE filegen_temporary_file (
	id_file INT AUTO_INCREMENT,
	id_user int DEFAULT 0 NOT NULL,
	title LONG VARCHAR DEFAULT NULL, 
	description LONG VARCHAR DEFAULT NULL, 
	id_physical_file VARCHAR(255) DEFAULT NULL,  
	file_size  INT DEFAULT NULL,
	mime_type VARCHAR(255) DEFAULT NULL,
	date_creation timestamp default CURRENT_TIMESTAMP NOT NULL,
	PRIMARY KEY (id_file)
);