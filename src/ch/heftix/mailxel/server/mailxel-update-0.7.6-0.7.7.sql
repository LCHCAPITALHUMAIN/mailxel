-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

delete from msgquery where id < 1000;

INSERT INTO "msgquery" VALUES(997,'TOM','messages to me',997,'select
  distinct m.id
from
  message m,
  address a,
  addressxmessage axm
where
  m.date>date(''now'',''-30 days'')
  and axm.messageid=m.id
  and axm.addressid=a.id
  and axm.type in (''T'',''C'')
  and a.id in (select id from address where shortname=(select shortname from address where address=(select v from conf where k=''me'')))',33);

INSERT INTO "msgquery" VALUES(998,'WFA','unanswered messages from me',998,'select 
  distinct m.id
from
  message m, address a, addressxmessage axm, category c, categoryxmessage cxm
where
  axm.messageid=m.id
  and axm.addressid=a.id
  and axm.type=''F''
  and cxm.current=1
  and cxm.messageid=m.id
  and cxm.categoryid in (select id from category where shortname in (''WFA''))
  and a.id in (select id from address where shortname=(select shortname from address where address=(select v from conf where k=''me'')))',5);

INSERT INTO "msgquery" VALUES(999,'ACT','unanswered messages to me',999,'select
  distinct m.id
from
  message m, address a, addressxmessage axm, category c, categoryxmessage cxm
where
  axm.messageid=m.id
  and axm.addressid=a.id
  and axm.type=''T''
  and cxm.current=1
  and cxm.messageid=m.id
  and cxm.categoryid not in (select id from category where shortname in (''DON'',''ANS'',''IGN'',''TON''))
  and a.id in (select id from address where shortname=(select shortname from address where address=(select v from conf where k=''me'')))',9);

INSERT INTO "msgquery" VALUES(996,'UTM','uncategorized messages to me',996,'select
  distinct m.id
from
  message m,
  category c,
  address a,
  addressxmessage axm
where
  m.date>date(''now'',''-30 days'')
  and axm.messageid=m.id
  and axm.addressid=a.id
  and axm.type in (''T'',''C'')
  and a.id in (select id from address where shortname=(select shortname from address where address=(select v from conf where k=''me'')))
  and (m.curcatid is null or m.curcatid not in (select id from category where shortname in (''SFT'', ''SFL'',''WFA'',''DON'',''IGN'',''TON'')))',5);

----

delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.7.7');