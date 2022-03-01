package com.jpa;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.jpa.model.Tshirt;
import com.jpa.sevice.JpaService;

@Configuration
@ComponentScan(basePackages = {"com.jpa*","com.jpa.repo"})
@EnableAutoConfiguration
@EnableJpaRepositories
public class Appmain implements CommandLineRunner {

	@Autowired
	private JpaService jpaservice;
	
	public static void main(String[] args) {
		SpringApplication.run(Appmain.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//createPerson();
		createProduct();
		getAllproduct();
		getSelectedproduct();
		//getgroupby();
	}
	

	private void createProduct() {
		Tshirt tshirt = new Tshirt(20, 38, "Red", "polo", 5);
		jpaservice.storeTshirt(tshirt);
		
	}
	
	private void getAllproduct() {
		
		Iterable<Tshirt> getproduct = jpaservice.getAllProductDetails();
		for(Tshirt ts : getproduct)
		{
			System.out.println("Get all product"+ts.toString());
		}
		
	}
	
	private void getSelectedproduct() {
		
		Tshirt getproduct = jpaservice.getIndividualTshirt(1);
		System.out.println("Get selected product"+getproduct.toString());
		
	}
	
	/*
	 * private void getgroupby() { List<Tshirt> getgroupby =
	 * jpaservice.getgroupby("size"); for(Tshirt ts : getgroupby) {
	 * System.out.println("GroupBy :"+ts.toString()); } }
	 */
}