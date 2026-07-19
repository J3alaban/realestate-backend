package com.realestate.backend.services.abstracts;

public interface VerifyMailService {
    boolean verifyToken(String token);

}