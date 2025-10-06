package com.egov.profileservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);


    ProfileRepository profileRepository;

    ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    String saveProfile(Profile profile)
    {

        logger.info("Saving profile ...");
        Profile savedProfile =  profileRepository.save(profile);
        logger.info("Profile saved successfully with id: {}", savedProfile.getPhone());
        return savedProfile.getPhone();
    }

    boolean checkProfile(String principal)
    {
        return profileRepository.findById(principal).isPresent();
    }

    Profile getProfile(String principal)
    {
        if(profileRepository.findById(principal).isPresent())
        {
            return profileRepository.findById(principal).get();
        }
        else  return null;
    }

}
