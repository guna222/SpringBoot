package com.jpa.sevice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpa.model.Tshirt;
import com.jpa.repo.ProductRepository;

@Service
public class JpaService {
	
	@Autowired
	private ProductRepository prod;
	
	
	public void storeTshirt(Tshirt tsData)
	{
		prod.save(tsData);
	}
	
	public List<Tshirt> getAllProductDetails()
	{
		List<Tshirt> product = (List<Tshirt>)prod.findAll();
		return product; 
	}
	
	public Tshirt getIndividualTshirt(int id)
	{
		Tshirt product = prod.findOne(id);
		return product;
	}
	
	public List<Tshirt> trying()
	{
		List<Tshirt> product = (List<Tshirt>)prod.getTshirtRaw("red");
		return product; 
	}
	
	public  List<Tshirt> getgroupby(String groupbyelement)
	{
		List<Tshirt> arrobj  = new ArrayList<>();
		switch (groupbyelement) {
		case "size":
			arrobj = prod.getGroupBysize();
			break;
		case "price":
			arrobj = prod.getGroupByprice();
			break;
		case "colour":
			arrobj = prod.getGroupBycolour();
			break;
		case "brand":
			arrobj = prod.getGroupBybrand();
			break;
		default:
			break;
			
		}
		return arrobj;
	}
	

}
