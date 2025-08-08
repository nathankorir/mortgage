package com.mortgage.backend.service;

import com.mortgage.backend.config.StreamProducer;
import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DecisionRequestDto;
import com.mortgage.backend.enums.Enum.ApplicationStatus;
import com.mortgage.backend.enums.Enum.DecisionType;
import com.mortgage.backend.mapper.ApplicationMapper;
import com.mortgage.backend.model.Application;
import com.mortgage.backend.model.Decision;
import com.mortgage.backend.model.User;
import com.mortgage.backend.repository.ApplicationRepository;
import com.mortgage.backend.repository.DecisionRepository;
import com.mortgage.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ApplicationServiceTest {
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private DecisionRepository decisionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationMapper applicationMapper;
    @Mock
    private StreamProducer streamProducer;

    @InjectMocks
    private ApplicationService applicationService;

    private Application application;
    private ApplicationRequest request;
    private ApplicationResponse response;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setNationalId(12345678L);

        application = new Application();
        application.setId(UUID.randomUUID());
        application.setApplicant(user);
        application.setPurpose("Home Loan");
        application.setAmount(100000.0);
        application.setStatus(ApplicationStatus.PENDING);

        request = new ApplicationRequest();
        request.setNationalId(12345678L);
        request.setPurpose("Home Loan");
        request.setAmount(100000.0);

        response = new ApplicationResponse();
        response.setId(application.getId());
        response.setNationalId(12345678L);
        response.setPurpose("Home Loan");
        response.setAmount(100000.0);
        response.setStatus(ApplicationStatus.PENDING);
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        when(userRepository.findByNationalId(12345678L)).thenReturn(Optional.of(user));
        when(applicationMapper.toEntity(request)).thenReturn(application);
        when(applicationRepository.save(application)).thenReturn(application);
        when(applicationMapper.toDto(application)).thenReturn(response);

        ApplicationResponse result = applicationService.create(request);

        assertThat(result).isEqualTo(response);
        verify(applicationRepository).save(application);
    }

    @Test
    void create_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByNationalId(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.create(request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void get_ShouldReturnResponse_WhenFound() {
        UUID id = application.getId();
        when(applicationRepository.findById(id)).thenReturn(Optional.of(application));
        when(applicationMapper.toDto(application)).thenReturn(response);

        Optional<ApplicationResponse> result = applicationService.get(id);

        assertThat(result).isPresent().contains(response);
    }

    @Test
    void get_ShouldReturnEmpty_WhenNotFound() {
        when(applicationRepository.findById(any())).thenReturn(Optional.empty());

        Optional<ApplicationResponse> result = applicationService.get(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void decide_ShouldSaveDecisionAndReturnResponse() {
        UUID appId = application.getId();
        UUID approverId = UUID.randomUUID();

        DecisionRequestDto decisionRequest = new DecisionRequestDto();
        decisionRequest.setApproverId(approverId);
        decisionRequest.setDecisionType(DecisionType.APPROVED);
        decisionRequest.setStatus(ApplicationStatus.APPROVED);
        decisionRequest.setComments("Looks good");

        User approver = new User();
        approver.setId(approverId);

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(application));
        when(userRepository.findById(approverId)).thenReturn(Optional.of(approver));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(applicationMapper.toDto(application)).thenReturn(response);

        ApplicationResponse result = applicationService.decide(appId, decisionRequest);

        assertThat(result).isEqualTo(response);
        verify(decisionRepository).save(any(Decision.class));
        verify(applicationRepository).save(application);
    }

    @Test
    void decide_ShouldThrow_WhenApplicationNotFound() {
        when(applicationRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.decide(UUID.randomUUID(), new DecisionRequestDto()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Application not found");
    }

    @Test
    void decide_ShouldThrow_WhenApproverNotFound() {
        UUID appId = application.getId();
        DecisionRequestDto dto = new DecisionRequestDto();
        dto.setApproverId(UUID.randomUUID());

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(application));
        when(userRepository.findById(dto.getApproverId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.decide(appId, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Approver not found");
    }
}
