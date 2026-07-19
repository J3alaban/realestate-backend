package com.realestate.backend.services.abstracts;

import com.realestate.backend.dtos.requests.ForgotPasswordRequest;
import com.realestate.backend.dtos.requests.ResetPasswordRequest;
import com.realestate.backend.dtos.responses.ForgotPasswordResponse;
import com.realestate.backend.dtos.responses.ResetPasswordResponse;

public interface ResetPasswordService {

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}