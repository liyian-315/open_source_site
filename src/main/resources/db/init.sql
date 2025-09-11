-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
   `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '账号是否启用：1=启用，0=禁用',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) DEFAULT 'USER' COMMENT '角色',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户，密码为123456的BCrypt加密形式
INSERT INTO `user` (`username`, `password`, `email`, `role`) VALUES
('admin', '$2a$10$EqWWNlQOWMdC0rGbcpYJEONQRm9yZZMOGx2H8s4KRGVdQSNdQTmPy', 'admin@example.com', 'ADMIN'),
('user', '$2a$10$EqWWNlQOWMdC0rGbcpYJEONQRm9yZZMOGx2H8s4KRGVdQSNdQTmPy', 'user@example.com', 'USER');