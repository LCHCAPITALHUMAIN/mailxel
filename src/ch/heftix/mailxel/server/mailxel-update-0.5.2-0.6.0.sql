-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

-- used to keep track of current schema version
create table conf (
  k text, -- key
  v text  -- value
);

-- log of sql statements used against this DB (only used for debugging purposes)
create table sqllog (
  date date,
  text sql
);


alter table category add shortname text;
alter table category add deleted integer; -- boolean
update category set deleted=0;
insert into category (id,shortname,name,deleted) values (999,'SFL','s:schedule-for-later',0);
insert into category (id,shortname,name,deleted) values (998,'WFA','s:wait-for-answer',0);
insert into category (id,shortname,name,deleted) values (997,'DON','s:done',0);

-- fix wrong references clause in categoryxmessage
create table cxm_tmp as select * from categoryxmessage;
drop table categoryxmessage;

create table categoryxmessage (
  categoryid integer references category(id),
  messageid integer references message(id),
  date date -- categorization date
);

insert into categoryxmessage (categoryid,messageid) select categoryid,messageid from cxm_tmp; 
drop table cxm_tmp;

-- last statement: set to current version
delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.6.0');
