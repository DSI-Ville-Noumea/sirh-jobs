----------------------------------------------------------------
-- Connect� en QRTZ_USR
----------------------------------------------------------------

INSERT INTO QRTZ_ADM.QRTZ_FIRED_TRIGGERS (SCHED_NAME,ENTRY_ID,TRIGGER_NAME,TRIGGER_GROUP,INSTANCE_NAME,FIRED_TIME,PRIORITY,STATE,JOB_NAME,JOB_GROUP,IS_NONCONCURRENT,REQUESTS_RECOVERY) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','instance_one1355354560156','cronSirhPrintTrigger','sirh','instance_one','1355354610000','5','ACQUIRED',null,null,'0','0');
INSERT INTO QRTZ_ADM.QRTZ_JOB_DETAILS (SCHED_NAME,JOB_NAME,JOB_GROUP,DESCRIPTION,JOB_CLASS_NAME,IS_DURABLE,IS_NONCONCURRENT,IS_UPDATE_DATA,REQUESTS_RECOVERY) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','sirhPrintJob','sirh',null,'nc.noumea.mairie.sirh.job.AvancementsWithEaesMassPrintJob','0','0','0','0');
INSERT INTO QRTZ_ADM.QRTZ_SCHEDULER_STATE (SCHED_NAME,INSTANCE_NAME,LAST_CHECKIN_TIME,CHECKIN_INTERVAL) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','instance_one','1355354605338','7500');
INSERT INTO QRTZ_ADM.QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,JOB_NAME,JOB_GROUP,DESCRIPTION,NEXT_FIRE_TIME,PREV_FIRE_TIME,PRIORITY,TRIGGER_STATE,TRIGGER_TYPE,START_TIME,END_TIME,CALENDAR_NAME,MISFIRE_INSTR) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhPrintTrigger','sirh','sirhPrintJob','sirh',null,'1355354610000','1355354600000','5','ACQUIRED','CRON','1355353943000','0',null,'0');
INSERT INTO QRTZ_ADM.QRTZ_CRON_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,CRON_EXPRESSION,TIME_ZONE_ID) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhPrintTrigger','sirh','0 * * * * ?','Pacific/Noumea');
COMMIT;

INSERT INTO QRTZ_FIRED_TRIGGERS (SCHED_NAME,ENTRY_ID,TRIGGER_NAME,TRIGGER_GROUP,INSTANCE_NAME,FIRED_TIME,PRIORITY,STATE,JOB_NAME,JOB_GROUP,IS_NONCONCURRENT,REQUESTS_RECOVERY) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','instance_one1355354560156','cronSirhAvctDeleteTrigger','sirh','instance_one','1355354610000','5','ACQUIRED',null,null,'0','0');
INSERT INTO QRTZ_JOB_DETAILS (SCHED_NAME,JOB_NAME,JOB_GROUP,DESCRIPTION,JOB_CLASS_NAME,IS_DURABLE,IS_NONCONCURRENT,IS_UPDATE_DATA,REQUESTS_RECOVERY) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','sirhAvctDeleteDocsJob','sirh',null,'nc.noumea.mairie.sirh.job.EraseAvancementsWithEaesMassPrintDocumentsJob','0','0','0','0');
INSERT INTO QRTZ_SCHEDULER_STATE (SCHED_NAME,INSTANCE_NAME,LAST_CHECKIN_TIME,CHECKIN_INTERVAL) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','instance_one','1355354605338','7500');
INSERT INTO QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,JOB_NAME,JOB_GROUP,DESCRIPTION,NEXT_FIRE_TIME,PREV_FIRE_TIME,PRIORITY,TRIGGER_STATE,TRIGGER_TYPE,START_TIME,END_TIME,CALENDAR_NAME,MISFIRE_INSTR) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhAvctDeleteTrigger','sirh','sirhAvctDeleteDocsJob','sirh',null,'1355354610000','1355354600000','5','ACQUIRED','CRON','1355353943000','0',null,'0');
INSERT INTO QRTZ_CRON_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,CRON_EXPRESSION,TIME_ZONE_ID) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhAvctDeleteTrigger','sirh','0 0 6 * * ?','Pacific/Noumea');
COMMIT;