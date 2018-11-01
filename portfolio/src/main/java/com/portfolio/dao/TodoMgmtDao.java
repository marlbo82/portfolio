package com.portfolio.dao;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.portfolio.model.TodoVO;

@Component
public class TodoMgmtDao {
	
	@Autowired
    private RedisTemplate redisTemplate;
 
	public Set<String> getAllKeys() {
		return redisTemplate.keys("*");
	}	
	
	public TodoVO getTodoWork(String workId) {
		Map<String, String> value = (Map<String, String>) redisTemplate.opsForValue().get(workId);
    	TodoVO todoVO = new TodoVO();
    	todoVO.setWorkId(value.get("workId"));
		todoVO.setWorkTitle(value.get("workTitle"));
		todoVO.setUprWorkId(value.get("uprWorkId"));
		todoVO.setPath(value.get("path"));
		todoVO.setFirstRegDtm(value.get("firstRegDtm"));
		todoVO.setLastModDtm(value.get("lastModDtm"));
		todoVO.setCompleteDtm(value.get("completeDtm"));
		
		return todoVO;
	}
	
	public void createTodoWork(int newKey, Map<String,String> paramMap) {
        redisTemplate.opsForValue().set(newKey+"", paramMap);
	}

	public void updateTodoWork(Map<String, String> paramMap) {
		redisTemplate.opsForValue().set(paramMap.get("workId"), paramMap);    
	}
}
