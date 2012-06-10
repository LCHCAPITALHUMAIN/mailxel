-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

alter table message add curcatid integer references category(id);

update message set curcatid=(select categoryid from categoryxmessage where messageid=message.id and current=1) where exists (select categoryid from categoryxmessage where
messageid=message.id and current=1);

insert into sequence values ('person',        10000);

-- person
create table person (
  id integer primary key,
  shortname text, -- JS
  name text, -- Joe Sixpack
  address text unique  -- main mail address, js@domain.name
);

alter table address add personid integer references person(id);

insert into person (id,shortname,name,address) select distinct id,shortname,name,address from address where isvalid=1 and ispreferred=1 group by shortname order by count;
update sequence set val=(select max(id)+1 from person) where name='person';

update address set personid=(select id from person where person.shortname=address.shortname) where exists (select id from person where person.shortname=address.shortname);

INSERT INTO icon VALUES(996,'inbox-table.png',X'89504E470D0A1A0A0000000D49484452000000100000001008060000001FF3FF610000001974455874536F6674776172650041646F626520496D616765526561647971C9653C000002314944415478DA9C93CF6B134114C7BFBB9D7493B5A936D8D236443609B60D9516FC51B517D1A67A28E241F1E049F0A277050FB507E93F2082FE11A57810F1E0514850A11E4C4B2926F4A22EA5108D4DDC243B3BBEB7C9A64505AB0F8637CC7E3FDF79B3F346BBB6B0000E4DD32E531AC6FEE2B352EA394F842765B0185F5E5C7CBA1FFAEAFCFC9D602E4E555E43A349CE3CAB7B4AE1D28A0672872463CFF37ECB6F2E1C84EBBAFA742D0F459C7E7A278733D5DCEDB964F4AE54CA779D4D6B7ECEA65A836326D9FAC61AD632C3ACA0E51171A06FE270DC4A0606AF8A0AA990037CA91221918D5005763BAB2858CB4C7DA73CA24B0F89FECCF471E94AB8546250414AFCC0BDF118EE4FF4231B2EE3C189613FB386B53E43ACA05D2DA5E95D742EB87B2A38DF0DBC2F16FDB39BE130F2ABABFE37D6B096196675321BED19B062BC28F754C071CCB230994EA3E638383936D6FA07A461ADCF102B5C0FB1DEC12343C68B97B0B18D2BE52E7C5A4BA067721685CD4DBF82B0616065F9318CEF5BB037FA607C7887DEB97343CC8AA6875074301EB97EEB86BFC3388D67F912D6288F2612FE5AA154C280DE40E6E2544B337594538459E14A15722A15346AB54EA37CA36926FF041B6F1D706F18BA8E0AE58A6D7734DDA60966455322D4A033F208E2E64CEA8F1DB84D47EA0499322B6A4D984EB50AB75EDF6DF4F5F5BFB63357C62C1928B341065EFB063854FB3A7F0D7A70BB067413CC8AC2965A721E3D8CE03FE263194B6C492D8343FCB0FE9177697CFD29C0001D862126DCFDA4A50000000049454E44AE426082');

insert into category (id,shortname,name,deleted) values (992,'SFT','s:schedule-for-today',0);
insert into category (id,shortname,name,deleted) values (991,'POP','s:postpone',0);

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

update message set deleted=0 where deleted is null;
update message set count=0 where count is null;

----

delete from conf where k='mailxel-db-version';
insert or replace into conf (k,v) values ('mailxel-db-version','0.7.6');