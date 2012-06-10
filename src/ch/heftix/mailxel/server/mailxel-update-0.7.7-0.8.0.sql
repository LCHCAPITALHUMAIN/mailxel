-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

alter table msgquery add type text;
update msgquery set type='M' where id < 1000 and type is NULL;

update message set fme=0 where fme is null;
update message set tme=0 where tme is null;

INSERT INTO msgquery (id,count,shortname,name,type,sql)
  VALUES(899,0,'F-ACT','fever, number of unanswered messages to me',
    'C', 'select count(id) from message where date>date(''now'',''-60 days'') 
      and fme=0 and tme=1 and (julianday(''now'') - julianday(date)) > 1 
      and curcatid not in (997,993,994)');

----

delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.8.0');