package com.mortgage.backend.service;

import com.mortgage.backend.dto.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
