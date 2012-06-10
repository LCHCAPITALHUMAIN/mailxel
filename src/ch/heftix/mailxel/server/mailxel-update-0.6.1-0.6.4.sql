-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

create index a_messageid on attachment(messageid);
create index a_md5 on attachment(md5);

-- add name to address
alter table address add name text;

-- alter sqllog (table not used since now)
drop table sqllog;
create table sqllog (
  date date,
  duration integer, -- in milliseconds
  sql text,
  args text
);

-- personalized icons

delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.6.4');