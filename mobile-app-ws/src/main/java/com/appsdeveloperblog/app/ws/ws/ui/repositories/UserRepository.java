package com.appsdeveloperblog.app.ws.ws.ui.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appsdeveloperblog.app.ws.data.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	UserEntity findUserByEmail(String email);
	UserEntity findByUserId(String userId);
	UserEntity findByEmail(String email);
	UserEntity findUserByEmailVerificationToken(String token);
	
	
	//countQuery is used to split the total number of results into different pages.
	@Query(value="Select * from Users u where u.EMAIL_VERIFICATION_STATUS='true'",
			countQuery = "select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS='true'",
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
	
	
	
	//?1 represents the first parameter. ?1 ?2 are called positional parameters
	@Query(value="select * from users u where u.FIRST_NAME = ?1",
			countQuery = "select count(*) from users u where u.FIRST_NAME = ?1",
			nativeQuery=true)
	List<UserEntity> findUserByFirstName(String firstName);
	
	@Query(value="select * from users u where u.LAST_NAME = :lastName",
			countQuery = "select count(*) from users u where u.LAST_NAME = :lastName",
			nativeQuery=true)
	List<UserEntity> findUserByLastName(@Param("lastName") String lastName);
	
	//%:keyword  means an expression that ends with the supplied keyword
	//:keyword%  means an expression that begins with the supplied keyword
	//%:keyword% means an expression that may begin or end with any characters but should contain "keyword"
	@Query(value="select * from users u where u.FIRST_NAME LIKE %:keyword% or u.LAST_NAME LIKE %:keyword%",
			countQuery = "select count(*) from users u where u.FIRST_NAME LIKE %:keyword% or u.LAST_NAME LIKE %:keyword%",
			nativeQuery=true)
	List<UserEntity> findUserByKeyword(@Param("keyword") String keyword);
	
	
	@Query(value="select u.FIRST_NAME, u.LAST_NAME from users u where u.FIRST_NAME LIKE %:keyword% or u.LAST_NAME LIKE %:keyword%", nativeQuery=true)
	List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);
	
	//transactional and modifying annotations are used for any update or delete queries.	
	@Transactional
	@Modifying
	@Query(value="update users u set u.email_verification_status = :emailVerificationStatus where u.user_id = :userId", nativeQuery=true)
	void updateEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus, @Param("userId") String userId);
	
//	in order to read the returned value List<Object[]> at the service class, create List<Object[]> users
	//Object[] user = users.get(0)
	//String firstName = String.valueOf(user[0])   as user is an array of objects
	//String lastName = String.valueOf(user[1])
	
	
	//So far we have seen the Native Query. Now we would see JPQL. this will help us to write queries in such a way that suppose in
	//future, we migrate from mysql to oracle or sql, we do not have to change the queries. JPQL are platform independent
	
	@Query("select user from UserEntity user where user.userId = :userId")
	UserEntity findUserByUserId(@Param("userId") String userId);
	
	
	//but make sure that you annotate UserEntity class with @Entity and @Table(name="users") instead of @Entity(name="users")
	
	@Query("select user.firstName, user.lastName from UserEntity user where user.userId = :userId")
	List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);
	
	
	//in the userServiceImpl class we will create List<Object[]> records to store the returned list of object array.
	//assertNotNull(records) will test if the records is null or not
	//assertTrue(records.size() == 1) will check if there is exactly one element in the list.
	//Object[] userDetails = record.get(0);
	//String firstName = String.valueOf(userDetails[0]);
	//String lastName = String.valueOf(userDetails[1]);
	
	@Transactional
	@Modifying
	@Query("update UserEntity user set user.emailVerificationStatus = :emailVerificationStatus where user.userId = :userId")
	void updateUserEntityEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
			@Param("userId") String userId);
	
}
