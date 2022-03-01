package com.jpa.model;

public class Price {
	
	private int price;
	private long count;
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "Price [price=" + price + ", count=" + count + "]";
	}

}
