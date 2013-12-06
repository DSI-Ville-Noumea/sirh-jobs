----------------------------------------------------------------
-- Connecté en QRTZ_USR
----------------------------------------------------------------

-- Job for Pointages.EtatPointage
INSERT INTO QRTZ_JOB_DETAILS (SCHED_NAME,JOB_NAME,JOB_GROUP,DESCRIPTION,JOB_CLASS_NAME,IS_DURABLE,IS_NONCONCURRENT,IS_UPDATE_DATA,REQUESTS_RECOVERY) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','sirhEtatPointageJob','sirh',null,'nc.noumea.mairie.sirh.job.EtatPointageJob','0','0','0','0');
INSERT INTO QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,JOB_NAME,JOB_GROUP,DESCRIPTION,NEXT_FIRE_TIME,PREV_FIRE_TIME,PRIORITY,TRIGGER_STATE,TRIGGER_TYPE,START_TIME,END_TIME,CALENDAR_NAME,MISFIRE_INSTR) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhEtatPointageJobTrigger','sirh','sirhEtatPointageJob','sirh',null,'1355354610000','1355354600000','5','WAITING','CRON','1355353943000','0',null,'0');
INSERT INTO QRTZ_CRON_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,CRON_EXPRESSION,TIME_ZONE_ID) VALUES ('org.springframework.scheduling.quartz.SchedulerFactoryBean#0','cronSirhEtatPointageJobTrigger','sirh','0 0 7 ? * MON','Pacific/Noumea');
COMMIT;
