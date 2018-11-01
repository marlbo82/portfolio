package com.portfolio.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.portfolio.dao.TodoMgmtDao;
import com.portfolio.model.TodoVO;

@Service("com.portfolio.service.TodoMgmtService")
public class TodoMgmtServiceImpl implements TodoMgmtService {
	
	@Autowired
	private TodoMgmtDao todoMgmtDao;
	
	@Override
	public List<TodoVO> getTodoList(String workId, int page) {
		
		// 결과를 담기 위한 변수 세팅
		List<TodoVO> result = new ArrayList<TodoVO>();
		
		// 할일 조회를 위한 redis key 값 가져오기
		Set<String> keys = todoMgmtDao.getAllKeys();
		Object[] keyArr = keys.toArray();
		Arrays.sort(keyArr);
		
		// 할일 조회 및 페이징 처리
		getTodoListByPaging(page, result, keyArr);

        return result;
	}
	
	
	@Override
	public Map<String, Object> createTodoWork(TodoVO todoVO) {
		
		// MAX 키 값 조회 및 새로운 KEY 값을 계산
		Set<String> keys = todoMgmtDao.getAllKeys();
		Object[] keyArr = keys.toArray();
		int newKey = 1;
		if (keyArr.length > 0) {
			Arrays.sort(keyArr);
			newKey = (Integer.parseInt((String) keyArr[keyArr.length-1])) + 1;			
		}
		
		// 결과를 담기 위한 변수 세팅 
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 참조 ID 존재 여부 check를 위한 변수 초기화
		boolean existUprWorkId = false; 
		
		// 참조 ID 존재 여부 check
		if (!StringUtils.isEmpty(todoVO.getUprWorkId())) {
			for (int i = 0; i < keyArr.length; i++) {
				TodoVO todo = todoMgmtDao.getTodoWork(keyArr[i]+"");
				if (todoVO.getUprWorkId().equals(todo.getWorkId())) {
					existUprWorkId = true;
				}
			}
		} else {
			existUprWorkId = true;			
		}
		
		// 참조 ID 부재시 실패 리턴
		if (!existUprWorkId) {
			result.put("resultCode", 400);
			result.put("resultMsg", "Bad Request - 참조 불가능한 ID 입니다.");
			return result;
		}
		
		// 할일 등록을 위한 파라미터 세팅
        Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("workId", newKey+"");
		paramMap.put("workTitle", todoVO.getWorkTitle());
		paramMap.put("uprWorkId", StringUtils.isEmpty(todoVO.getUprWorkId())?null:todoVO.getUprWorkId());
		if ("".equals(paramMap.get("uprWorkId")) || paramMap.get("uprWorkId") == null) {
			paramMap.put("path", null);			
		} else {
			TodoVO uprTodoVO = todoMgmtDao.getTodoWork(paramMap.get("uprWorkId"));
			paramMap.put("path", uprTodoVO.getPath()+">>>"+paramMap.get("uprWorkId"));
		}
		paramMap.put("firstRegDtm", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		paramMap.put("lastModDtm", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		paramMap.put("completeDtm", null);
		
		// 할일 등록
		todoMgmtDao.createTodoWork(newKey, paramMap);

		// 결과 값 세팅
		result.put("resultCode", 200);
		result.put("resultMsg", "Success");
		
		return result;
	}

	@Override
	public Map<String, Object> updateTodoWork(TodoVO todoVO) {
		
		// 결과를 담기 위한 변수 세팅
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 자기 참조를 막기 위한 로직
		if (todoVO.getWorkId().equals(todoVO.getUprWorkId())) {
			result.put("resultCode", 400);
			result.put("resultMsg", "Bad Request - 셀프 참조는 불가능 합니다.");
			return result;
		}
		
		// 순환 참조 여부 체크용 변수 초기화
		boolean isRoundExist = true;
		
		// 순환 참조 여부 체크
		TodoVO uprTodoVO = todoMgmtDao.getTodoWork(todoVO.getUprWorkId());
		String [] arrUprId = uprTodoVO.getPath().split(">>>");
		for (int i = 0; i < arrUprId.length; i++) {
			if(arrUprId[i] == todoVO.getWorkId()) {
				isRoundExist = false;
			}
		}
		
		// 순환 참조시 실패 리턴 
		if (isRoundExist) {
			result.put("resultCode", 400);
			result.put("resultMsg", "Bad Request - 참조 불가능한 ID 입니다.");
			return result;
		}
		
		// 할일 수정을 위한 파라미터 세팅
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("workId", todoVO.getWorkId());
		paramMap.put("workTitle", todoVO.getWorkTitle());
		paramMap.put("uprWorkId", todoVO.getUprWorkId());
		paramMap.put("path", todoVO.getPath());
		paramMap.put("firstRegDtm", todoVO.getFirstRegDtm());
		paramMap.put("lastModDtm", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		paramMap.put("completeDtm", null);
		
		// 할일 수정
		todoMgmtDao.updateTodoWork(paramMap);
		
		// 결과 값 세팅
		result.put("resultCode", 200);
		result.put("resultMsg", "Success");
		
		return result;
	}
	
	@Override
	public Map<String, Object> compelteTodoWork(TodoVO todoVO) {
		
		// 할일 목록 KEY값 조회
		Set<String> keys = todoMgmtDao.getAllKeys();
		Object[] keyArr = keys.toArray();
		Arrays.sort(keyArr);
		
		// 결과를 담기 위한 변수 세팅
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 해당 할일을 참조하는 미완료 할일이 있는지 체크
		for (int i = 0; i < keyArr.length; i++) {
			TodoVO todo = todoMgmtDao.getTodoWork(keyArr[i]+"");
			if (todoVO.getWorkId().equals(todo.getUprWorkId()) && todo.getCompleteDtm() == null) {
				result.put("resultCode", 400);
				result.put("resultMsg", "Bad Request - 미완료 참조가 남아 있습니다.");
				return result;
			}
		}
		
		// 할일 완료 처리를 위한 파라미터 세팅
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("workId", todoVO.getWorkId());
		paramMap.put("workTitle", todoVO.getWorkTitle());
		paramMap.put("uprWorkId", todoVO.getUprWorkId());
		paramMap.put("path", todoVO.getPath());
		paramMap.put("firstRegDtm", todoVO.getFirstRegDtm());
		paramMap.put("lastModDtm", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		paramMap.put("completeDtm", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		
		// 할일 완료 처리
		todoMgmtDao.updateTodoWork(paramMap);
		
		// 결과 값 세팅
		result.put("resultCode", 200);
		result.put("resultMsg", "Success");
		
		return result;
	}
	
	private void getTodoListByPaging(int page, List<TodoVO> result, Object[] keyArr) {
		String workId;
		int cntOfOnePage = 2;
		int totalPage = keyArr.length/cntOfOnePage;
		if((keyArr.length % cntOfOnePage) > 0) {
			totalPage += 1;
		}
		
		int start = (page-1) * cntOfOnePage;
		int end = page * cntOfOnePage;
		if(end > keyArr.length) {
			end = keyArr.length;
		}
		
		for (int i = start; i < end; i++) {
			workId = (String) keyArr[i];
			TodoVO todoVO = todoMgmtDao.getTodoWork(workId);
			todoVO.setTotalPage(totalPage);
			result.add(todoVO);
		}
	}
}