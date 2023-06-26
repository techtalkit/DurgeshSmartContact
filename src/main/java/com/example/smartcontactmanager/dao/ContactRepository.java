package com.example.smartcontactmanager.dao;
import java.util.List;
import com.example.smartcontactmanager.entities.Contact;
import com.example.smartcontactmanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ContactRepository extends JpaRepository<Contact,Integer>{
	
	@Query("from Contact as d where d.user.id=:userId")
	public Page<Contact> findContactsbyUser(@Param("userId") int userId, Pageable pageAble);
	//search
	public List<Contact> findByNameContainingAndUser(String keywords, User user);

}
