package com.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jpa.model.Price;
import com.jpa.model.Tshirt;


@Repository
public interface ProductRepository extends CrudRepository<Tshirt, Integer> {
	
	@Query(value = "SELECT p FROM Tshirt p WHERE p.colour=?1")
	List<Tshirt> getTshirtRaw(String colour);
	
	@Query(value = "SELECT COUNT(p.brand) as count, p.size FROM Tshirt p GROUP BY p.size")
	List<Tshirt> getGroupBysize();
	
	@Query(value = "SELECT COUNT(size) as count,colour FROM Tshirt GROUP BY colour")
	List<Tshirt> getGroupBycolour();
	
	@Query(value = "SELECT COUNT(size) as count,brand FROM Tshirt GROUP BY brand")
	List<Tshirt> getGroupBybrand();
	
	@Query(value = "SELECT COUNT(size) as count,price FROM Tshirt GROUP BY price")
	List<Tshirt> getGroupByprice();
}


