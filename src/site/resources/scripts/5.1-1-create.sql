----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
create table SIRH2.SIIDMA_SIRH
(
ID_AGENT INTEGER not null,
LOGIN varchar(500),
MAIL varchar(500),
NOMATR INTEGER not null,
constraint SIRH.PK_SIIDMA_SIRH
primary key (ID_AGENT)
);

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
create table SIRH.SIIDMA_SIRH
(
ID_AGENT INTEGER not null,
LOGIN varchar(500),
MAIL varchar(500),
NOMATR INTEGER not null,
constraint SIRH.PK_SIIDMA_SIRH
primary key (ID_AGENT)
);



