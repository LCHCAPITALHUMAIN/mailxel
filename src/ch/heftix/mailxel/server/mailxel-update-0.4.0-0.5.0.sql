-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

create table category (
  id integer primary key,
  name text
);

create table categoryxmessage (
  categoryid integer references address(id),
  messageid integer references message(id)
);
insert into sequence values ('category',       1000);
