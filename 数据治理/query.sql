-- 按月查看总体销售额
--select buy_time, sum(goods_price) as sell_nums 
--from "data_yys"."m_br", "data_yys"."m_goods"
--where "data_yys"."m_br".goods_id = "data_yys"."m_goods".goods_id 
--group by buy_time

-- 按月查看新增注册人数
--select register_time, count(*) as register_nums from "data_yys"."m_rr" group by register_time

-- 按月查看城市销售额
--select buy_time, user_address, sum(goods_price) as sell_nums
--from "data_yys"."m_br", "data_yys"."m_goods", "data_yys"."m_user"
--where "data_yys"."m_br".goods_id = "data_yys"."m_goods".goods_id 
--and "data_yys"."m_br".user_id = "data_yys"."m_user".user_id 
--group by buy_time, user_address

--按月查看城市、商品类别的销售额
--select buy_time, user_address, goods_type, sum(goods_price) as sell_nums
--from "data_yys"."m_br", "data_yys"."m_goods", "data_yys"."m_user"
--where "data_yys"."m_br".goods_id = "data_yys"."m_goods".goods_id 
--and "data_yys"."m_br".user_id = "data_yys"."m_user".user_id 
--group by buy_time, user_address, goods_type

--按月查看性别、商品类别的销售额
--select buy_time, user_sex, goods_type, sum(goods_price) as sell_nums
--from "data_yys"."m_br", "data_yys"."m_goods", "data_yys"."m_user"
--where "data_yys"."m_br".goods_id = "data_yys"."m_goods".goods_id 
--and "data_yys"."m_br".user_id = "data_yys"."m_user".user_id 
--group by buy_time, user_sex, goods_type