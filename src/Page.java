import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Page implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_PAGE_SIZE = 1000;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int currentPage = 1;
	private int totalPage = 0;
	private int totalRecords = 0;
	private List data;
	private List currentPageData;
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalPage() {
		this.totalPage = this.totalRecords % this.pageSize == 0 ?  this.totalRecords / this.pageSize:this.totalRecords / this.pageSize + 1;
		return this.totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}
	
	public List getCurrentPageData() {
		return currentPageData;
	}
	public void setCurrentPageData(List currentPageData) {
		this.currentPageData = currentPageData;
	}
	
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
//	public Page() {
//		
//	}
	
	public Page(List data) {
		this.data = data;
		this.totalRecords = data.size();
		firstPage();
	}
	public void firstPage() {
		this.currentPage = 1;
		int toIndex = 0;
		if(this.totalRecords < this.pageSize) {
			toIndex = this.totalRecords;
		}else {
			toIndex = this.currentPage * this.pageSize;
		}
		this.currentPageData =  this.data.subList(0, toIndex);
	}
	
	public void lastPage() {
		this.currentPage = this.getTotalPage();
		int fromIndex = (this.currentPage - 1) * this.pageSize;
		this.currentPageData =  this.data.subList(fromIndex, data.size());
	}
	
	public void nextPage() {
		this.currentPage = this.currentPage + 1;
		if(this.currentPage > this.getTotalPage()) {
			this.currentPage = this.getTotalPage();
		}
		int fromIndex = (this.currentPage - 1) * this.pageSize;
		int toIndex = this.currentPage * this.pageSize;
		if(this.currentPage == this.getTotalPage()) {
			toIndex = this.getData().size();
		}
		this.currentPageData =  this.data.subList(fromIndex, toIndex);
	}
	
	public void previousPage() {
		this.currentPage = this.currentPage - 1;
		if(this.currentPage <= 0) {
			this.currentPage = 1;
		}
		int fromIndex = (this.currentPage - 1) * this.pageSize;
		int toIndex = this.currentPage * this.pageSize;
		if(this.currentPage == 1 && this.getTotalPage() == 1) {
			toIndex = this.getData().size();
		}
		this.currentPageData =  this.data.subList(fromIndex, toIndex);
	}
	
	public static void main(String[] args) throws Exception {
		Database db = new Database();
		List<Map<String,Object>> contents = db.getTableContents("world","city");
		Page page = new Page(contents);
		page.nextPage();
		page.previousPage();
		page.lastPage();
		page.nextPage();
		page.firstPage();
		page.previousPage();
		List data = page.getCurrentPageData();
		db.print(data);
		
	}
}