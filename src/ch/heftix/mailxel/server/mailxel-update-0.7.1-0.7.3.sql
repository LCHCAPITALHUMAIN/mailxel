-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

alter table categoryxmessage add current integer;
update categoryxmessage set current=0;

-- ready-made, personalized queries searching for specific messages
create table msgquery (
  id integer primary key,
  shortname text,
  name text, --
  iconid integer references icon(id),  
  sql text
);

insert into sequence values ('msgquery',       1000);

insert into msgquery (id,shortname,name,sql) values (999,'ACT','unanswered messages to me',  'select distinct m.id from message m, address a, addressxmessage axm, category c, categoryxmessage cxm where axm.messageid=m.id and axm.addressid=a.id and axm.type=''T'' and cxm.messageid=m.id and cxm.current=1 and cxm.categoryid not in (select id from category where shortname in (''DON'',''ANS'',''IGN'',''TON'')) and a.id in (select id from address where shortname=(select shortname from address where address=(select v from conf where k=''me'')))');
insert into msgquery (id,shortname,name,sql) values (998,'WFA','unanswered messages from me','select distinct m.id from message m, address a, addressxmessage axm, category c, categoryxmessage cxm where axm.messageid=m.id and axm.addressid=a.id and axm.type=''F'' and cxm.messageid=m.id and cxm.current=1 and cxm.categoryid in (select id from category where shortname in (''WFA'')) and a.id in (select id from address where shortname=(select shortname from address where address=(select v from conf where k=''me'')))');

delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.7.3');