package br.com.editor.model.tools;

public class IntCounter {

	private int value = 0;

	public IntCounter() {
	}
	public IntCounter(int startValue) {
		value = startValue;
	}

	public int inc(){
		return ++value;
	}
	public int getThenInc() {
		return value++;
	}
	
	public int inc(int amount){
		value += amount;
		return value;
	}

	public int dec(){
		return --value;
	}
	public int getThenDec() {
		return value--;
	}

	public int dec(int amount){
		value -= amount;
		return value;
	}
	
	public int getThenDec(int amount) {
		int r = value;
		value -= amount;
		return r;
	}

	public int getValue(){
		return value;
	}

	public void setValue(int value){
		this.value = value;
	}


}
