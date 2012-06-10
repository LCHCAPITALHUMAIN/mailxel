-- Copyright (C) 2007-2009 by Simon Hefti. All rights reserved.
--
-- Licensed under the EPL 1.0 (Eclipse Public License).
-- (see http://www.eclipse.org/legal/epl-v10.html)
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- Initial Developer: Simon Hefti

-- sqlite> select axm.addressid,a.address, count(*) n from addressxmessage axm, address a where axm.type<>'T' and messageid in (select id from message where date>'2009-01-01' and t='SH') and a.id=axm.addressid group by addressid order by n, a.address;

create table address (
  id integer primary key,
  shortname text, -- SH
  address text unique,  -- userid@domain.name
  isvalid integer, -- 1 valid 0 invalid
  ispreferred integer, -- which one (of many addresses of one user) is the preferred one?
  lastseen date -- date of last contact with address
);

create index adr_short on address(shortname);
create index adr_adr on address(address);

-- sqlite> select shortname,address from address where id in (select addressid from addressxmessage where messageid=1100100);
-- update stat set date=? where shortname in ()
-- problem: of course, not all address entries carry shortnames


-- mail message
create table message (
  id integer primary key,
  uuid text, -- unique message id as defined by the sender
  date date, -- send or recieved time
  subject text,
  relevance text, -- A (I had more than 10 conversations with), B or C
  deleted integer, -- boolean
  GTD text, -- GTD category (1, 2, ... Jan, Feb, ... next year, reference)
  f text, -- from (managed by trigger)
  t text, -- to
  c text, -- cc
  b text, -- bcc
  nattach int, -- number of attachements
  azip text -- zip file containing the attachements
);

create index msg_uuid on message(uuid);
create index msg_subject on message(subject);
create index msg_f on message(f);
create index msg_t on message(t);
create index msg_c on message(c);
create index msg_f_t on message(f,t);


-- for table scan efficiency, the body is stored in a separated table
create table body (
  id integer primary key references message(id),
  data clob
);

-- attachments
create table attachment (
  id integer primary key,
  messageid integer references message(id),
  name text, -- attachment file name
  md5 text -- md5 checksum if file, used for the detection of duplicates
);

create table addressxmessage (
  addressid integer references address(id),
  messageid integer references message(id),
  type text -- T(o), F(rom), C(c), B(cc)
);

create index axm_type on addressxmessage(type);
create index axm_msgid on addressxmessage(messageid);
create index axm_adrid on addressxmessage(addressid);
create index axm_msgidtype on addressxmessage(messageid,type);

create table category (
  id integer primary key,
  name text
);

create table categoryxmessage (
  categoryid integer references address(id),
  messageid integer references message(id)
);

-- poor mans sequences  
create table sequence(
  name text primary key, 
  val integer
);

insert into sequence values ('message',     1000000);
insert into sequence values ('attachment', 10000000);
insert into sequence values ('address',       10000);
insert into sequence values ('category',       1000);

--create view mail as
--  select 
--    T.messageid as id, 
--    m.date as date, 
--    m.subject as subject, 
--    m.relevance as relevance, 
--    m.GTD as GTD, 
--    F.name as f, 
--    T.name as t,
--    C.name as c,
--    B.name as b,
--  from
--    (select
--       messageid, group_concat(ifnull(shortname, address)) as name
--     from
--       addressxmessage, address
--     where addressid=id and type='F'
--     group by messageid) F,
--    (select
--       messageid, group_concat(ifnull(shortname, address)) as name
--     from
--       addressxmessage, address
--     where addressid=id and type='T'
--     group by messageid) T,
--    (select
--       messageid, group_concat(ifnull(shortname, address)) as name
--     from
--       addressxmessage, address
--     where addressid=id and type='C'
--     group by messageid) C,
--    (select
--       messageid, group_concat(ifnull(shortname, address)) as name
--     from
--       addressxmessage, address
--     where addressid=id and type='B'
--     group by messageid) B,
--    message m
--  where
--   (m.deleted is NULL or m.deleted <>1) and
--    F.messageid=m.id and
--    T.messageid=m.id;
    
--create trigger update_message_f_on_shortname after update of shortname on address for each row
--  begin
--	  update message 
--	    set f=(select f from mail where mail.id=new.id),
--	    set t=(select t from mail where mail.id=new.id),
--	    set c=(select c from mail where mail.id=new.id),
--	    set b=(select b from mail where mail.id=new.id)
--	  where
--	    id in (select messageid from addressxmessage,address where addressxmessage.addressid=address.id and address.shortname=old.shortname);
--  end; 
