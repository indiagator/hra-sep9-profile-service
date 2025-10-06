package com.egov.profileservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/internal") // INTERNAL UNSECURED API FOR QUERY OPTIONS
public class InternalRestController
{
        @Autowired
        ProfileService profileService;

        @GetMapping("profile/get/{principal}")
        ResponseEntity<?> getProfile(@PathVariable("principal") String principal)
        {
            // VERIFY ORIGIN BEFORE PROCEEDING - LEFT AS EXERCISE FOR THE READER
            Profile profile = profileService.getProfile(principal);
            return  ResponseEntity.ok().body(profile);
        }
}
