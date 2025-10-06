package com.egov.profileservice;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profiles")
@Data
public class Profile {

    @Id
    String phone;
    String firstname;
    String lastname;
    String email;
    String location;
    String aadhar;

}
