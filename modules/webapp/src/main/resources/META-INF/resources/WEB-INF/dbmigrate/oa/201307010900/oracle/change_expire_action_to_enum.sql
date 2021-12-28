alter table content add (ExpireActionNew VARCHAR2(32));
update content set expireactionnew = expireaction;
alter table content drop column expireaction;
alter table content rename column expireactionnew to ExpireAction;