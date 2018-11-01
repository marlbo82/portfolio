package com.portfolio.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.model.TodoVO;
import com.portfolio.service.TodoMgmtService;

@RestController
@RequestMapping("/test")
public class TodoMgmtController {

	@Autowired
	private TodoMgmtService todoMgmtService;
	
	
	@RequestMapping(value = "/works", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<TodoVO>> getTodoList(@RequestParam(value="workId", required = false) String workId,
															   @RequestParam(value="page", required = false, defaultValue = "0") String page) {
		List<TodoVO> queryResult = todoMgmtService.getTodoList(workId, Integer.parseInt(page));
		
		Map<String, List<TodoVO>> result = new HashMap<String, List<TodoVO>>();
		result.put("data", queryResult);
				
		return result;
	}
	
	
	@RequestMapping(value = "/works", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> createTodoWork(@RequestBody TodoVO todoVO) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 입력 parameter validation check
		if(StringUtils.isEmpty(todoVO.getWorkTitle())) {
			result.put("resultCode", 400);
			result.put("resultMsg", "Bad Request - \"할 일\"이 입력되지 않았습니다.");
		} else {
			result = todoMgmtService.createTodoWork(todoVO);
		}
		
		return result;
	}
	
	
	@RequestMapping(value = "/works", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> updateTodoWork(@RequestBody TodoVO todoVO) {
		Map<String, Object> resultMap = todoMgmtService.updateTodoWork(todoVO);
		
		return resultMap;
	}
	
	
	@RequestMapping(value = "/works", method = RequestMethod.PUT, params="isCompletion=true")
	public @ResponseBody Map<String, Object> compelteTodoWork(@RequestBody TodoVO todoVO) {
		Map<String, Object> resultMap = todoMgmtService.compelteTodoWork(todoVO);
		
		return resultMap;
	}
}
