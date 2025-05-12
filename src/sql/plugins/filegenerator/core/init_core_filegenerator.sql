--liquibase formatted sql
--changeset filegenerator:init_core_filegenerator.sql
--preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_admin_right VALUES ('VIEW_TEMP_FILES','filegenerator.adminFeature.temporary_files.name',2,'jsp/admin/plugins/filegenerator/ManageMyFiles.jsp','filegenerator.adminFeature.temporary_files.description',0,NULL,'SYSTEM',NULL,NULL,3, 0);
