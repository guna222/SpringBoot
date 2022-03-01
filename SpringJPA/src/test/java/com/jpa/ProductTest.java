package com.jpa;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import com.jpa.controller.JpaController;
import com.jpa.model.Tshirt;
import com.jpa.sevice.JpaService;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class ProductTest {

	
	/*
	 * @InjectMocks JpaController jpa = new JpaController();
	 * 
	 * @Mock JpaService jpaservice;
	 * 
	 * @Before public void setup() { MockitoAnnotations.initMocks(this); }
	 */
	
	@Test
	public void contextLoads() {
	}
	
	
	/*
	 * @Test public void test1() { Tshirt tshirt = new Tshirt(20, 38, "Red", "polo",
	 * 5); jpaservice.storeTshirt(tshirt);
	 * 
	 * }
	 * 
	 * @Test public List<Tshirt> productDetailsTest() { Tshirt tshirt = new
	 * Tshirt(20, 38, "Red", "polo", 5); Tshirt tshirt2 = new Tshirt(30, 43,
	 * "yellow", "US", 4); List<Tshirt> li = new ArrayList<Tshirt>();
	 * li.add(tshirt); li.add(tshirt2);
	 * 
	 * Mockito.when(jpaservice.getAllProductDetails()).thenReturn(li); List<Tshirt>
	 * product = jpa.productDetails(); return null; }
	 */
	

}
