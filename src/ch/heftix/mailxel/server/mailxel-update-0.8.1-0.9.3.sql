-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

-- new field inreplyto
alter table message add inreplyto text;

-- new table to track message references
create table msgxmsg (
  srcid integer references message(id),
  refid integer references message(id)
);

-- DB housekeeping
analyze;
INSERT INTO messagetext(messagetext) VALUES('optimize');
vacuum;

----
delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.9.3');