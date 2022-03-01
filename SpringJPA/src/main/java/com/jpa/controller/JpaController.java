package com.jpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*import com.jpa.model.Customer;
import com.jpa.model.OrderRequest;
import com.jpa.model.OrderResponse;
import com.jpa.repo.CustomerRepository;*/
import com.jpa.repo.ProductRepository;
import com.jpa.sevice.JpaService;
import com.jpa.model.Tshirt;

@RestController()
public class JpaController {

	@Autowired
	public JpaService jpaser;
	
	/*
	 * @Autowired private CustomerRepository customerRepository;
	 */
	/*
	 * @Autowired private ProductRepository productRepository;
	 */

	@PostMapping("/StoreProdData")
	public void storeTshirt(@RequestBody Tshirt tshirtdata) {
		jpaser.storeTshirt(tshirtdata);
	}

	@GetMapping("/getAllTshirt")
	public List<Tshirt> productDetails() {

		/*
		 * Tshirt t = new Tshirt(); t.setBrand("POlO"); t.setColour("red");
		 * t.setCount(10); t.setPrice(1000); t.setSize(38); jpaser.storeTshirt(t);
		 */
		List<Tshirt> product = jpaser.getAllProductDetails();
		return product;
	}

	@GetMapping("/getselectedTshirt/{id}")
	public Tshirt indiviualProduct(@PathVariable("id") int id) {
		Tshirt product = jpaser.getIndividualTshirt(id);
		return product;
	}

	@GetMapping("/trying")
	public List<Tshirt> trying() {
		List<Tshirt> product = jpaser.trying();
		return product;
	}

	@GetMapping("/groupby/{groupby}")
	public List<Tshirt> groupby(@PathVariable("groupby") String element) {
		List<Tshirt> product = jpaser.getgroupby(element);
		return product;
	}
	
	/*
	 * @PostMapping("/placeOrder") public Customer placeOrder(@RequestBody
	 * OrderRequest request){ return customerRepository.save(request.getCustomer());
	 * }
	 * 
	 * @GetMapping("/findAllOrders") public List<Customer> findAllOrders(){ return
	 * customerRepository.findAll(); }
	 * 
	 * @GetMapping("/getInfo") public List<OrderResponse> getJoinInformation(){
	 * return customerRepository.getJoinInformation(); }
	 */

}
