
--- 初始化令牌桶配置
--- @param key 令牌的唯一标识
--- @param max_permits 桶大小
--- @param rate 向桶里添加令牌的速率
--- @param apps 可以使用令牌桶的应用列表，应用之前用逗号分隔
local result=1
redis.pcall("HMSET",KEYS[1],
		"last_mill_second",ARGV[1],
		"curr_permits",ARGV[2],
		"max_permits",ARGV[3],
		"rate",ARGV[4],
		"app",ARGV[5])
return result

