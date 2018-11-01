package com.portfolio.model;

import org.springframework.stereotype.Component;

@Component
public class TodoVO {
	
	private String workId;
	private String workTitle;
	private String uprWorkId;
	private String path;
	private String firstRegDtm; 
	private String lastModDtm; 
	private String completeDtm;
	
	/* 페이징 처리 */
	private int totalPage = 0;	
	
	public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public String getWorkTitle() {
		return workTitle;
	}
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	public String getUprWorkId() {
		return uprWorkId;
	}
	public void setUprWorkId(String uprWorkId) {
		this.uprWorkId = uprWorkId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFirstRegDtm() {
		return firstRegDtm;
	}
	public void setFirstRegDtm(String firstRegDtm) {
		this.firstRegDtm = firstRegDtm;
	}
	public String getLastModDtm() {
		return lastModDtm;
	}
	public void setLastModDtm(String lastModDtm) {
		this.lastModDtm = lastModDtm;
	}
	public String getCompleteDtm() {
		return completeDtm;
	}
	public void setCompleteDtm(String completeDtm) {
		this.completeDtm = completeDtm;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	
	@Override
	public String toString() {
		return "TodoVO [workId=" + workId + ", workTitle=" + workTitle + ", uprWorkId=" + uprWorkId + ", path=" + path
				+ ", firstRegDtm=" + firstRegDtm + ", lastModDtm=" + lastModDtm + ", completeDtm=" + completeDtm
				+ ", totalPage=" + totalPage + "]";
	}
}
