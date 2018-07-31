
public enum Result {
	success(1,"sucess"),failure(2,"failure"),nameExist(3,"nameAlreadyExists");
	private int code;
	private String msg;
	Result(int code,String msg){
		this.code = code;
		this.msg = msg;
				
	}
	@Override
	public String toString() {
		return String.valueOf(code) + " " + msg;
	}
	
	
}
