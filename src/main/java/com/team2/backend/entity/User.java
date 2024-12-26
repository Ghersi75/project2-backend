package com.team2.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
     /**
     * Unique identifier for the Account entity.
     * It is the primary key in the database and is generated automatically.
     */
    @Column(name="account_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    /**
     * The username for this account.
     * It must be unique and not blank.
     *
     */
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    /**
     * The password for this account.
     * It must have more than 6 characters for security purposes.
     */
    private String password;

     /**
     * The role of the account, which specifies user access level.
     * For example: "MODERATOR" or "CONSUMER".
     */
    private String role;
     
    /** 
     *  @return a String representation of this class.
     */
 

    
}
