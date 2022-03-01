package com.jpa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_rating")
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rating_id")
	private int ratingId;

	@Column(name = "rating")
	private int rating;

	@Column(name = "product_id", length = 60, nullable = false)
	private String productid;

	@Column(name = "user_id", length = 60, nullable = false)
	private String userid;

	public Rating() {

	}

	public Rating(int ratingId, int rating, String productid, String userid) {
		super();
		this.ratingId = ratingId;
		this.rating = rating;
		this.productid = productid;
		this.userid = userid;
	}

	public int getRatingId() {
		return ratingId;
	}

	public void setRatingId(int ratingId) {
		this.ratingId = ratingId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
