----------------------------------------------------------------
-- Connecté en QRTZ_USR
----------------------------------------------------------------

-- Job for Pointages.ventilation
INSERT INTO QRTZ_JOB_DETAILS (SCHED_NAME,JOB_NAME,JOB_GROUP,DESCRIPTION,JOB_CLASS_NAME,IS_DURABLE,IS_NONCONCURRENT,IS_UPDATE_DATA,REQUESTS_RECOVERY) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','sirhPointagesVentilationJob','sirh',null,'nc.noumea.mairie.sirh.job.PointagesVentilationJob','0','0','0','0');
INSERT INTO QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,JOB_NAME,JOB_GROUP,DESCRIPTION,NEXT_FIRE_TIME,PREV_FIRE_TIME,PRIORITY,TRIGGER_STATE,TRIGGER_TYPE,START_TIME,END_TIME,CALENDAR_NAME,MISFIRE_INSTR) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhPointagesVentilationJobTrigger','sirh','sirhPointagesVentilationJob','sirh',null,'1355354610000','1355354600000','5','WAITING','CRON','1355353943000','0',null,'0');
INSERT INTO QRTZ_CRON_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,CRON_EXPRESSION,TIME_ZONE_ID) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhPointagesVentilationJobTrigger','sirh','0 0/2 6-20 * * ?','Pacific/Noumea');
COMMIT;

-- Job for Pointages.exportPaie
INSERT INTO QRTZ_JOB_DETAILS (SCHED_NAME,JOB_NAME,JOB_GROUP,DESCRIPTION,JOB_CLASS_NAME,IS_DURABLE,IS_NONCONCURRENT,IS_UPDATE_DATA,REQUESTS_RECOVERY) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','sirhPointagesExportPaieJob','sirh',null,'nc.noumea.mairie.sirh.job.PointagesExportPaieJob','0','0','0','0');
INSERT INTO QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,JOB_NAME,JOB_GROUP,DESCRIPTION,NEXT_FIRE_TIME,PREV_FIRE_TIME,PRIORITY,TRIGGER_STATE,TRIGGER_TYPE,START_TIME,END_TIME,CALENDAR_NAME,MISFIRE_INSTR) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhPointagesExportPaieJobTrigger','sirh','sirhPointagesExportPaieJob','sirh',null,'1355354610000','1355354600000','5','WAITING','CRON','1355353943000','0',null,'0');
INSERT INTO QRTZ_CRON_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,CRON_EXPRESSION,TIME_ZONE_ID) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhPointagesExportPaieJobTrigger','sirh','0 0/5 6-20 * * ?','Pacific/Noumea');
COMMIT;