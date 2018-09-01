
public class ArrayPrimitive {

	int[] array;
	String preFix;
	
	public ArrayPrimitive(int length, String Prefix) {
		array = new int[length];
		for (int i = 0; i < array.length; i++) {
			array[i] = i+1;
		}
		this.preFix = Prefix;
	}
	
	public int get(int i){
		String outPut = preFix + array[i];
		
		return Integer.parseInt(outPut);
	}
	
	public void clock(){
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i]+1;
		}
	}
}
