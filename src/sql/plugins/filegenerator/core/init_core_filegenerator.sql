-- liquibase formatted sql
-- changeset filegenerator:init_core_filegenerator.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_admin_right VALUES ('VIEW_TEMP_FILES','filegenerator.adminFeature.temporary_files.name',2,'jsp/admin/plugins/filegenerator/ManageMyFiles.jsp','filegenerator.adminFeature.temporary_files.description',0,NULL,'SYSTEM','ti ti-file-export',NULL,3, 0);

-- changeset filegenerator:init_core_filegenerator.sql-rev1
-- preconditions onFail:MARK_RAN onError:WARN
-- The admin user of the core (id 1) gets the feature of the plugin on a fresh install (rights are never granted by an upgrade script)
DELETE FROM core_user_right WHERE id_right = 'VIEW_TEMP_FILES' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('VIEW_TEMP_FILES',1);
