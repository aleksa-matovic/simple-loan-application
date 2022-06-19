package com.leanpay.simpleloan.rep;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leanpay.simpleloan.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>{
	
}
