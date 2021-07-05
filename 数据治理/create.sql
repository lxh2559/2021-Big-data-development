-- 用户
drop table if exists data_yys.m_user;
create table data_yys.m_user
(
  user_id varchar(50) primary key,
  user_name varchar(50),
  user_sex varchar(50),
  user_age int,
  user_phone varchar(50),
  user_address varchar(50)
);
-- Add comments to the table 
comment on table data_yys.m_user
  is '用户';
-- Add comments to the columns 
comment on column data_yys.m_user.user_id
  is '用户ID';
comment on column data_yys.m_user.user_name
  is '用户名称';
comment on column data_yys.m_user.user_sex
  is '用户性别';
comment on column data_yys.m_user.user_age
  is '用户年龄';
comment on column data_yys.m_user.user_phone
  is '手机号码';
comment on column data_yys.m_user.user_address
  is '用户地址';
 
-- 商家
drop table if exists data_yys.m_seller;
create table data_yys.m_seller
(
  seller_id varchar(50) primary key,
  seller_name varchar(50),
  seller_phone varchar(50),
  seller_address varchar(50)
);
-- Add comments to the table 
comment on table data_yys.m_seller
  is '商家';
-- Add comments to the columns 
comment on column data_yys.m_seller.seller_id
  is '商家ID';
comment on column data_yys.m_seller.seller_name
  is '商家名称';
comment on column data_yys.m_seller.seller_phone
  is '手机号码';
comment on column data_yys.m_seller.seller_address
  is '商家地址';
 
-- 商品
drop table if exists data_yys.m_goods;
create table data_yys.m_goods
(
  goods_id varchar(50) primary key,
  goods_name varchar(50),
  goods_type varchar(50),
  goods_price int,
  seller_id varchar(50)
);
-- Add comments to the table 
comment on table data_yys.m_goods
  is '商品';
-- Add comments to the columns 
comment on column data_yys.m_goods.goods_id
  is '商品ID';
comment on column data_yys.m_goods.goods_name
  is '商品名称';
comment on column data_yys.m_goods.goods_type
  is '商品类别';
comment on column data_yys.m_goods.goods_price
  is '商品价格';
comment on column data_yys.m_goods.seller_id
  is '商家ID';
 
-- 注册记录
drop table if exists data_yys.m_rr;
create table data_yys.m_rr
(
  user_id varchar(50) primary key,
  register_time varchar(50)
);
-- Add comments to the table 
comment on table data_yys.m_rr
  is '注册记录';
-- Add comments to the columns 
comment on column data_yys.m_rr.user_id
  is '用户ID';
comment on column data_yys.m_rr.register_time
  is '注册时间';
 
-- 购物记录
drop table if exists data_yys.m_br;
create table data_yys.m_br
(
  buy_id varchar(50) primary key,
  goods_id varchar(50),
  user_id varchar(50),
  buy_time varchar(50)
);
-- Add comments to the table 
comment on table data_yys.m_br
  is '购物记录';
-- Add comments to the columns 
comment on column data_yys.m_br.buy_id
  is '购物ID';
comment on column data_yys.m_br.goods_id
  is '商品ID';
comment on column data_yys.m_br.user_id
  is '用户ID';
comment on column data_yys.m_br.buy_time
  is '购物时间';
 
INSERT INTO "data_yys"."m_user" VALUES ('100001', '张三', '男', 10, '17898754375', '广东省广州市');
INSERT INTO "data_yys"."m_user" VALUES ('100002', '李四', '女', 20, '15027439675', '广东省佛山市');
INSERT INTO "data_yys"."m_user" VALUES ('100003', '王五', '男', 30, '17854398734', '广东省广州市');
INSERT INTO "data_yys"."m_user" VALUES ('100004', '赵六', '女', 40, '17254389756', '广东省珠海市');
INSERT INTO "data_yys"."m_user" VALUES ('100005', '孙七', '男', 50, '15698745273', '广东省广州市');

INSERT INTO "data_yys"."m_seller" VALUES ('1001', '天猫超市', '17865429304', '广东省广州市');
INSERT INTO "data_yys"."m_seller" VALUES ('1002', '新华书店', '17889735426', '广东省佛山市');
INSERT INTO "data_yys"."m_seller" VALUES ('1003', '华工商店', '15278645834', '广东省广州市');

INSERT INTO "data_yys"."m_goods" VALUES ('10001', '可乐', '饮料',10, '1001');
INSERT INTO "data_yys"."m_goods" VALUES ('10002', '面包', '食品',5, '1001');
INSERT INTO "data_yys"."m_goods" VALUES ('10003', '大学数学', '书籍',30, '1002');
INSERT INTO "data_yys"."m_goods" VALUES ('10004', '大学英语', '书籍',30, '1002');
INSERT INTO "data_yys"."m_goods" VALUES ('10005', '沐浴露', '日用品',35, '1003');
INSERT INTO "data_yys"."m_goods" VALUES ('10006', '洗发水', '日用品',40, '1003');

INSERT INTO "data_yys"."m_rr" VALUES ('100001', '2021-1');
INSERT INTO "data_yys"."m_rr" VALUES ('100002', '2021-3');
INSERT INTO "data_yys"."m_rr" VALUES ('100003', '2021-1');
INSERT INTO "data_yys"."m_rr" VALUES ('100004', '2021-2');
INSERT INTO "data_yys"."m_rr" VALUES ('100005', '2021-3');

INSERT INTO "data_yys"."m_br" VALUES ('1000001', '10001', '100001', '2021-1');
INSERT INTO "data_yys"."m_br" VALUES ('1000002', '10003', '100001', '2021-1');
INSERT INTO "data_yys"."m_br" VALUES ('1000003', '10004', '100001', '2021-3');
INSERT INTO "data_yys"."m_br" VALUES ('1000004', '10002', '100002', '2021-2');
INSERT INTO "data_yys"."m_br" VALUES ('1000005', '10003', '100003', '2021-1');
INSERT INTO "data_yys"."m_br" VALUES ('1000006', '10004', '100003', '2021-2');
INSERT INTO "data_yys"."m_br" VALUES ('1000007', '10005', '100004', '2021-2');
INSERT INTO "data_yys"."m_br" VALUES ('1000008', '10006', '100005', '2021-3');
INSERT INTO "data_yys"."m_br" VALUES ('1000009', '10001', '100005', '2021-1');
INSERT INTO "data_yys"."m_br" VALUES ('1000010', '10004', '100005', '2021-3');