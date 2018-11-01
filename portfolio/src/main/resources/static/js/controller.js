var app = angular.module('app', []);
app.controller('controller', function($scope, $http) {

	// 페이지당 표현 될 아이템 개수
	var itemCntByPage = 2;
	
	// 할일 등록
	$scope.createTodoWork = function(){
		$http({
			method:'POST',
			url:'/test/works',
			data : {
		        'workTitle' : workTitle.value, 
		        'uprWorkId' : uprWorkId.value
			}
		}).then(function(response, data){
			if (response.data.resultCode != 200) { 
				alert('등록 실패 (' + response.data.resultMsg + ")");
			} else {
				alert('등록 성공 (' + response.data.resultMsg + ")");
			}
			
			// 저장 폼 초기화
			workTitle.value = '';
			uprWorkId.value = '';
			$scope.getTodoList($scope.presentPage);
		});
	}
	
	// 할일 리스트 조회
	$scope.getTodoList = function(page){
		// 현재 페이지 갱신
		$scope.presentPage = page;
		
		// 최초 로딩 시 이전 페이지 값 세팅
		if ($scope.prePage == undefined) {
			$scope.prePage = 0;
		}
		
		$http({
			method:'GET',
			url:'/test/works?page=' + $scope.presentPage
		}).then(function(response, data){
			$scope.records = [];
			$scope.totalPages = [];
			for (var i = 0; i < response.data.data.length; i++) {
				$scope.records.push(response.data.data[i]);
			}
			
			$scope.ranges = [];
			
			for (var i = 0; i < response.data.data[0].totalPage; i++) {
				$scope.totalPages.push(i+1);
				
				// 이동하고자 하는 페이지가 이전 페이지보다 클 경우
				if (i<2 && $scope.prePage <= page) {
					if ((i+page<=response.data.data[0].totalPage) && (page<response.data.data[0].totalPage)) {
						$scope.ranges.push(i+page);						
					} else if(page==response.data.data[0].totalPage) {
						$scope.ranges.push(i+page-1);						
					}
				}
				
				// 이동하고자 하는 페이지가 이전 페이지보다 작을 경우
				if (i<2 && $scope.prePage > page) {
					if ((page-i>0) && (page<=response.data.data[0].totalPage) && page>1) {
						$scope.ranges.push(page-((itemCntByPage-1)-i));						
					} else if(page==1) {
						$scope.ranges.push(1+page-((itemCntByPage-1)-i));						
					}
				}
			}
			
			// 현재 페이지 Bold 처리 (초기 로딩시 1페이지 Bold 처리 안 되는 문제점 존재)
			if (document.getElementById("pagebtn_"+page) != null) {
				document.getElementById("pagebtn_"+page).style.fontWeight = 'bold';
			}
			
			// 이전 페이지 Bold 처리 해제
			if ($scope.prePage != undefined && $scope.prePage != 0 && $scope.prePage != page) {
				document.getElementById("pagebtn_"+$scope.prePage).style.fontWeight = 'normal';
			}
			
			// 이전 페이지 갱신
			$scope.prePage = page;
		});
	}
	
	// 할일 수정
	$scope.updateTodoWork = function(index){
		var workId = (($scope.presentPage-1) * itemCntByPage) + index + 1; 
		$http({
			method:'PUT',
			url:'/test/works',
			data : {
				'workId' : workId,
				'workTitle' : document.getElementById("workTitle_"+index).value, 
				'uprWorkId' : document.getElementById("uprWorkId_"+index).value,
				'firstRegDtm' : $scope.records[index].firstRegDtm
			}
		}).then(function(response, data){
			if (response.data.resultCode != 200) { 
				alert('수정 실패 (' + response.data.resultMsg + ")");
			} else {
				alert('수정 성공 (' + response.data.resultMsg + ")");
			}
			
			// 할일 리스트 현재 페이지 다시 불러오기
			$scope.getTodoList($scope.presentPage);
		});
	}
	
	// 할일 완료 처리
	$scope.compelteTodoWork = function(index){
		var workId = (($scope.presentPage-1) * itemCntByPage) + index + 1; 
		$http({
			method:'PUT',
			url:'/test/works?isCompletion=true',
			data : {
				'workId' : workId,
		        'workTitle' : $scope.records[index].workTitle, 
		        'uprWorkId' : $scope.records[index].uprWorkId,
		        'firstRegDtm' : $scope.records[index].firstRegDtm
			}
		}).then(function(response, data){
			if (response.data.resultCode != 200) { 
				alert('완료 처리 실패 (' + response.data.resultMsg + ")");
			} else {
				alert('완료 처리 성공 (' + response.data.resultMsg + ")");
			}
			
			// 할일 리스트 현재 페이지 다시 불러오기
			$scope.getTodoList($scope.presentPage);
		});
	}
	
	// 완료 날짜 존재여부 확인
	$scope.isCompleteDtmNull = function(index){
		if($scope.records[index].completeDtm == '' || $scope.records[index].completeDtm == undefined || $scope.records[index].completeDtm == null) {
			return false;
		} else {
			return true;
		}
	}
	
	// 이전 페이지
	$scope.getPrePage = function(){
		if ($scope.presentPage>1) {
			$scope.getTodoList($scope.presentPage-1);			
		}
	}
	
	// 다음 페이지
	$scope.getPostPage = function(){
		if ($scope.presentPage<$scope.totalPages.length) {
			$scope.getTodoList($scope.presentPage+1);			
		}
	}
	
	// 로딩시 첫 페이지 불러오기
	$scope.getTodoList(1);
});