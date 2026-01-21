package com.example.website.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String companyEmail;

    private String companySize;
    private String primaryStack;
    

    @Column(nullable = false)
    private String password;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}



	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	public String getCompanySize() {
		return companySize;
	}

	public void setCompanySize(String companySize) {
		this.companySize = companySize;
	}

	public String getPrimaryStack() {
		return primaryStack;
	}

	public void setPrimaryStack(String primaryStack) {
		this.primaryStack = primaryStack;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    // getters & setters
    
}
