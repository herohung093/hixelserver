create table application_user (id  bigserial not null, email varchar(255), first_name varchar(255), last_name varchar(255), password varchar(255), portfolio_id int8, refresh_token_id int8, reset_code_id int8, primary key (id));
create table portfolio (id  bigserial not null, primary key (id));
create table portfolio_company (id  bigserial not null, ticker varchar(255), portfolio_id int8, primary key (id));
create table refresh_token (id  bigserial not null, expiry_date timestamp, token_value varchar(255), primary key (id));
create table reset_code (id  bigserial not null, code_value varchar(255), expiry_date timestamp, primary key (id));
alter table application_user add constraint FK7c4odagj2ck4swlv8453rcox7 foreign key (portfolio_id) references portfolio on delete cascade;
alter table application_user add constraint FKprigqo09q92o9pjglnpt48a92 foreign key (refresh_token_id) references refresh_token on delete cascade;
alter table application_user add constraint FKgoqcpk8pc635jdi18fxaruuq8 foreign key (reset_code_id) references reset_code on delete cascade;
alter table portfolio_company add constraint FKjpeeuv3943vj631mn85de9bt foreign key (portfolio_id) references portfolio on delete cascade;