package com.egov.profileservice;

import lombok.Data;

@Data
public class ProfileEvent
{
    String principal;
    String type; // CREATE / UPDATE / DELETE / QUERY
    String action;
    String status; // SUCCESS / FAILURE
    String traceid;
}
