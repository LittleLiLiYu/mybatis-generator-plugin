create table LAVA_I18N
(
  id              INT(12) not null AUTO_INCREMENT,
  gmt_create      DATE not null,
  creator         VARCHAR(128) not null,
  gmt_modified    DATE not null,
  modifier        VARCHAR(128) not null,
  is_deleted      CHAR(1) not null,
  module          VARCHAR(30) not null comment '模块，不同模块之间可以有相同的key',
  i18n_key        VARCHAR(128) not null comment '国际化key，用于获取内容',
  i18n_lang       VARCHAR(6) not null comment '国际化语言标志，使用Java中的locale标志',
  i18n_message    VARCHAR(3000) comment '国际化内容',
  PRIMARY KEY (id),
  UNIQUE unique_i18n_mkl (module,i18n_key,i18n_lang) comment '国际化唯一键'
);