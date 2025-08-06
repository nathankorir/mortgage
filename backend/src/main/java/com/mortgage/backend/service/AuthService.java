package com.mortgage.mortgage.service;

import com.mortgage.mortgage.dto.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
