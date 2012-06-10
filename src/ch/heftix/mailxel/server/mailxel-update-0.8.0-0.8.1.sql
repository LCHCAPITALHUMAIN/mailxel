-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

-- fix message query table content

delete from msgquery where id < 1000;

INSERT INTO "msgquery" (id,shortname,name,type,iconid,sql,count) VALUES(899,'F-ACT','fever, number of unanswered messages to me','C',NULL,
'select
  count(id)
from
  message
where
  date>date(''now'',''-60 days'')
  and fme=0
  and tme=1
  and (julianday(''now'') - julianday(date)) > 1
  and curcatid not in (997,993,994)' ,0);

INSERT INTO "msgquery" (id,shortname,name,type,iconid,sql,count) VALUES(799,'F-ALL','count all messages','C',NULL,'select count(id)
from
  message',0);
  
INSERT INTO "msgquery" (id,shortname,name,type,iconid,sql,count) VALUES(995,'TOP','recent messages','M',null, 'select
  distinct m.id
from
  message m
where
  m.date>date(''now'',''-30 days'')
  and m.deleted=0' ,0);

INSERT INTO "msgquery" (id,shortname,name,type,iconid,sql,count) VALUES(996,'UTM','uncategorized messages to me','M',996, 'select
  distinct m.id
from
  message m
where
  m.date>date(''now'',''-30 days'')
  and m.deleted=0
  and m.tme=1
  and m.fme=0
  and m.curcatid=990',0);

INSERT INTO "msgquery" (id,shortname,name,type,iconid,sql,count) VALUES(997,'TOM','messages to me','M',997,'select
  distinct m.id
from
  message m
where
  m.date>date(''now'',''-30 days'')
  and m.deleted=0
  and m.tme=1',0);

INSERT INTO "msgquery" (id,shortname,name,type,iconid,sql,count) VALUES(998,'WFA','unanswered messages from me','M',998,'select
  distinct m.id
from
  message m
where
  m.date>date(''now'',''-30 days'')
  and m.deleted=0
  and m.curcatid=998
  and m.fme=1',0);

INSERT INTO "msgquery" (id,shortname,name,type,iconid,sql,count) VALUES(999,'ACT','unanswered messages to me',999,'M','select
  distinct m.id
from
  message m
where
  m.date>date(''now'',''-90 days'')
  and m.deleted=0
  and m.tme=1
  and m.fme<>1
  and m.curcatid not in (997, 996, 994, 993)',0);
---

insert into category (id,shortname,name,deleted) values (990,'UNC','s:uncategorized',0);
update message set curcatid=990 where deleted=0 and curcatid is null;

update message set fme=0 where fme is null;
update message set tme=0 where tme is null;

-- introduce FTS
alter table message rename to messagesd;

create virtual table messagetext using fts3 (
  subject text,
  f text,
  t text,
  c text,
  b text,
  r text, -- all recipients
  a text, -- all attachments
  GTD text,
  body text
);

insert into messagetext (docid,subject,f,t,c,b,r,body)
  select
    m.id,
    m.subject,
    m.f, m.t, m.c, m.b,
    group_concat(ifnull(a.shortname,'') || ' ' || ifnull(a.name,'') || ' ' || a.address),
    b.data
  from
    messagesd m,
    body b,
    address a,
    addressxmessage axm
  where
    m.deleted=0
    and b.id=m.id
    and axm.messageid=m.id
    and axm.addressid=a.id
  group by
    m.id;

drop table body;    

create table message (
  id integer primary key,
  uuid text, -- unique message id as defined by the sender
  date date, -- send or received time
  deleted integer, -- boolean
  nattach int, -- number of attachments
  azip text, -- zip file containing the attachments
  tme integer, -- boolean, mail is from myself
  fme integer, -- boolean, mail addressed to myself
  count integer, -- counts access times
  curcatid integer references category(id) -- last attributed category
);

insert into message select m.id,m.uuid,m.date,m.deleted,m.nattach,m.azip,m.tme,m.fme,count,curcatid from messagesd m;

drop table messagesd;

analyze;

INSERT INTO messagetext(messagetext) VALUES('optimize');

vacuum;

----
delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.8.1');