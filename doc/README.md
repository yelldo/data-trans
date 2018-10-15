# 迁移方案预演

## 数据分布情况说明
1. 福建省kt老耗材联采系统（kt耗材申报+hx耗材联采） 数据都已经备份到公司oracle数据库
2. 药品交易数据在王明钊交易结算系统
3. 药品联采系统数据
4. 耗材申报宁德项目（杨涛项目部分数据）

## 原始数据迁移
1. ~~kt现有线上系统（耗材联采）停机，行政单位下发通知用户停止操作，说明系统维护（周末两天时间）~~
2. DBA同步kt系统oracle中数据到海西oracle数据库
3. 使用navicat premium 迁移oracle数据到mysql
4. 迁移原始用户数据（Oracle视图：VW_ALL_USER_INFO),使用navicat premium导出视图的数据成insert sql 语句， 执行插入到预先创建好的mysql表中：
```
CREATE TABLE `vm_all_user_info` (
  `ID` varchar(100) DEFAULT NULL COMMENT '原始数据id有重复，所以不设置成primary key）',
  `ACCOUNT` varchar(200) DEFAULT NULL COMMENT '账户',
  `PASSWORD` varchar(200) DEFAULT NULL COMMENT '密码',
  `USER_NAME` varchar(100) DEFAULT NULL COMMENT '用户名',
  `ORG_ID` varchar(50) DEFAULT NULL COMMENT '机构id',
  `EMAIL` varchar(200) DEFAULT NULL,
  `STATUS` varchar(50) DEFAULT NULL COMMENT '状态',
  `LAST_LOGIN_IP` varchar(50) DEFAULT NULL,
  `LAST_LOGIN_TIME` timestamp NULL DEFAULT NULL,
  `PHONE` varchar(20) DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT NULL,
  `MODIFY_USER` varchar(50) DEFAULT NULL,
  `MODIFY_TIME` timestamp NULL DEFAULT NULL,
  `PROJECT_ID` varchar(50) DEFAULT NULL,
  `PRIVATE_KEY` varchar(50) DEFAULT NULL,
  `SEX` int(2) DEFAULT NULL,
  `IS_ACTIVATION` char(1) DEFAULT NULL,
  `REASONS_DISABLE` varchar(2000) DEFAULT NULL,
  `IS_ADMIN` char(1) DEFAULT NULL,
  `USER_TYPE` varchar(40) DEFAULT NULL,
  `LOGIN_TYPE` varchar(5) DEFAULT NULL,
  `DEPARTMENT_ID` varchar(50) DEFAULT NULL,
  `ORG_NAME` varchar(500) DEFAULT NULL,
  `ORG_CODE` varchar(100) DEFAULT NULL,
  `ORG_TYPE` varchar(40) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
至此，原始数据已经稳定到mysql中供转换操作

## 用户机构数据转换、归集
1. kt耗材联采
```
select count(1) from MCS_HOSPITAL_INFO; -- 1881
-- select * from MCS_HOSPITAL_INFO;
select count(1) from MCS_REGULATOR_INFO; -- 843
-- select * from MCS_REGULATOR_INFO;
-- 机构信息表
select count(1) from mcs_company_info; -- 9059
-- select * from mcs_company_info;
-- 机构变更表
select count(1) from mcs_company_info_do; -- 105
-- select * from mcs_company_info_do;
-- 机构变更操作表
select count(1) from mcs_company_info_do_his; -- 16958
-- select * from mcs_company_info_do_his;
-- 所有用户表
-- select count(1) from vw_all_user_info; -- 13439
-- select * from vw_all_user_info;
```
2. 通过java转换程序，整理成可用的生产数据

## 相关的文件迁移
1. 涉及的文件转存到公司的oss文件服务上
2. kt的文件被加密成edc文件，需要让kt解密

## 数据注意事项
kt的企业机构类型：
1. 国内生产企业/代理机构
2. 配送企业
3. 生产及配送企业

hx的企业机构类型：
1. 企业类
2. 采购类
3. 政府机构类
4. 运营中心

## 接口对接
1. 登录
2. 退出
- 本次耗材申报部分功能的开发由南京团队负责