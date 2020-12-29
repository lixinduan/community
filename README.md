数据库 community
user表
`username`,
`activation_code`,
`create_time`,
`header_url`,
`email`,
`id`,
`number`,
`password`,
`salt`,
`status`,
`type`

discuss_post表
`type`,
`content`,
`comment_count`,
`score`,
`title`,
`status`,
`id`,
`create_time`,
`user_id`

message表
`conversation_id`,
`content`,
`create_time`,
`from_id`,
`id`,
`status`,
`to_id`

comment表
`content`,
`create_time`,
`entity_id`,
`entity_type`,
`id`,
`status`,
`target_id`,
`user_id`

login_ticket表
`user_id`,
`ticket`,
`id`,
`expired`,
`status`