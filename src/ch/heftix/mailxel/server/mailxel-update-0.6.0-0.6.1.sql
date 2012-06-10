-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

insert into category (id,shortname,name,deleted) values (996,'ANS','s:answered',0);
insert into category (id,shortname,name,deleted) values (995,'FWD','s:forwarded',0);

alter table address add count integer;

delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.6.1');