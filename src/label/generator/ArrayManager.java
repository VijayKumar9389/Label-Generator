

import java.util.Arrays;


public class ArrayManager {
	private Object[] item;  
	private int count;
	private int resizeAmount;

	public ArrayManager() {}

	public ArrayManager(Object[] item){
		this.item = item;
		count = item.length;	
	}

	public ArrayManager(int size){
		count = 0;
		item = new Object [size];
	}

	public int size(){
		return count;
	}

	public void resize(){
		Object[] temp = new Object [item.length + 1];
		System.arraycopy(item, 0, temp, 0, count);
		item = temp;
	}

	public void printarray(){
		for (int i = 0; i < item.length; i++) {
			System.out.println(item[i]);
		}
	}
	
	public void RemoveRange(int start, int end) throws OutOfBoundsException{
		if (start < 0 || end >= size()){
			throw new OutOfBoundsException();
		}
	}
	
	public int countoccurences(Object o){
		int num = 0;
		for (int i = 0; i < item.length; i++) {
			if (item[i].equals(o)){
				num++;
			}
		}
		return num;
	}
	
	public void addAll( Object[] os){
		for (int i = 0; i < os.length; i++) {
			add(os[i]);//should take each object from the array and add to the end of the current array.
		}
	}
	
	public int removeAllOccurences(Object o){
		int count=0;
		for (int i = 0; i < size(); i++) {
			if (item[i].equals(o)){
				count++;
				remove(i);//everytime the while loop finds a similar object it uses the remove method 
			}//to delete that object out of the array.
		}
		return count;
	}

	public void print(){
		for (int i = 0; i < count; i++) {
			System.out.println(item[i]);
		}
	}

	public void add(Object newitem){
		if (count == item.length){
			resize();
		}
		item[count] = newitem;
		count++;
	}

	public void remove(int position){
		for (int i = position; i < count - 1; i++) {
			item[i] = item[i+1];
		}
		count--;
	}
	
	public Object Getelementbypos(int pos)
	{
		return item[pos];
	}

	public void additem(int position, Object newitem){

		if (item.length == count){
			resize();
		}
		for (int i = count; i >= position; i--) {
			item[i] = item[i-1];
		}
		item[position] = newitem;
		count++;
	}

    private static class OutOfBoundsException extends Exception {

        public OutOfBoundsException() {
        }
    }
}        
	
//	public static void Sort(){
//		
//		Object[] temp = new Object[count];
//		
//		for (int i = 0; i < temp.length; i++) {
//			if (item[i] != null){
//				temp[i] = item[i];
//			}
//		}
//		Arrays.sort(temp);
//	}