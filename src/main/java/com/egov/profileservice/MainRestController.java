package com.egov.profileservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("api/v1") // EXTERNAL API
public class MainRestController {

    private static final Logger logger = LoggerFactory.getLogger(MainRestController.class);
    private final TokenService tokenService;
    @Autowired
    Producer producer;
    ProfileService profileService;

    MainRestController(ProfileService profileService, TokenService tokenService) {
        this.profileService = profileService;
        this.tokenService = tokenService;
    }

    @PostMapping("profile/update") // SECURED ENDPOINT
    ResponseEntity<?> updateProfile(@RequestBody Profile profile,
                                    HttpServletRequest request,
                                    ProfileEvent profileEvent) throws JsonProcessingException {
        // micrometer observation pre-handler tasks have already been executed

        Optional<String> token =  Optional.ofNullable(tokenService.getAuthCookieValue(request));
        Optional<String> principal =  Optional.ofNullable(tokenService.validateToken(token.orElse(null)));

        if(principal.isPresent())
        {
            if(principal.get().equals(profile.getPhone()))
            {
                if(profileService.checkProfile(profile.getPhone()))
                {
                    logger.info("Profile update successful for principal: {}", profile.getPhone());

                    profileEvent.setPrincipal(profile.getPhone());
                    profileEvent.setType("UPDATE");
                    profileEvent.setAction("PROFILE_UPDATE");
                    profileEvent.setStatus("SUCCESS");
                    profileEvent.setTraceid(io.opentelemetry.api.trace.Span.current().getSpanContext().getTraceId());

                    producer.pubProfileEvent_1(profileEvent);

                    String savedPrincipal =  profileService.saveProfile(profile);
                }
                else
                {
                    logger.info("Creating new profile for principal: {}", profile.getPhone());

                    profileEvent.setPrincipal(profile.getPhone());
                    profileEvent.setType("CREATE");
                    profileEvent.setAction("PROFILE_SAVE");
                    profileEvent.setStatus("SUCCESS");
                    profileEvent.setTraceid(io.opentelemetry.api.trace.Span.current().getSpanContext().getTraceId());

                    producer.pubProfileEvent_1(profileEvent);

                    String savedPrincipal =  profileService.saveProfile(profile);
                }
            }
            else
            {

                profileEvent.setPrincipal(profile.getPhone());
                profileEvent.setType("ERROR");
                profileEvent.setAction("PROFILE_UPDATE");
                profileEvent.setStatus("FAILED");
                profileEvent.setTraceid(io.opentelemetry.api.trace.Span.current().getSpanContext().getTraceId());

                producer.pubProfileEvent_1(profileEvent);

                return  ResponseEntity.badRequest().body("Don't be cute | Can't update somebody else's profile");
            }
        }
        else
        {
            profileEvent.setPrincipal(profile.getPhone());
            profileEvent.setType("ERROR");
            profileEvent.setAction("PROFILE_UPDATE");
            profileEvent.setStatus("FAILED");
            profileEvent.setTraceid(io.opentelemetry.api.trace.Span.current().getSpanContext().getTraceId());

            producer.pubProfileEvent_1(profileEvent);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("TRY LOGIN AGAIN");
        }

        return ResponseEntity.ok().body(principal);
    }
}
