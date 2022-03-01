/*
 * package com.jpa.repo;
 * 
 * 
 * 
 * import java.util.List;
 * 
 * import org.springframework.data.jpa.repository.JpaRepository; import
 * org.springframework.data.jpa.repository.Query; import
 * org.springframework.stereotype.Repository;
 * 
 * import com.jpa.model.Customer; import com.jpa.model.OrderResponse;
 * 
 * 
 * //@Repository public interface CustomerRepository extends
 * JpaRepository<Customer,Integer> {
 * 
 * @Query("SELECT new com.jpa.model.OrderResponse(c.name , p.productName) FROM Customer c JOIN c.products p"
 * ) public List<OrderResponse> getJoinInformation(); }
 */