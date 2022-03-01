
package com.jpa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "thsirt_data")
public class Tshirt {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private int price;
	private int size;
	private String colour;
	private String brand;
	private int count;
	
	
	public Tshirt()
	{
		
	}
	
	public Tshirt(int price, int size, String colour, String brand, int count) {
		super();
		this.price = price;
		this.size = size;
		this.colour = colour;
		this.brand = brand;
		this.count = count;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "Tshirt [id=" + id + ", price=" + price + ", size=" + size + ", colour=" + colour + ", brand=" + brand
				+ ", count=" + count + "]";
	}

}
