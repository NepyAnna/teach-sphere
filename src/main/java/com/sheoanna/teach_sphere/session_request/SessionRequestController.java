package com.sheoanna.teach_sphere.session_request;

import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestRequest;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestResponse;
import com.sheoanna.teach_sphere.session_request.dtos.UpdateSessionStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/session_requests")
@RequiredArgsConstructor
public class SessionRequestController {
    private final SessionRequestService sessionService;

    @GetMapping("/student")
    public ResponseEntity<List<SessionRequestResponse>> findRequestsForStudent() {
        return ResponseEntity.ok(sessionService.findRequestsForStudent());
    }

    @GetMapping("/mentor")
    public ResponseEntity<List<SessionRequestResponse>> findRequestsForMentor() {
        return ResponseEntity.ok(sessionService.findRequestsForMentor());
    }

    @PostMapping("")
    public ResponseEntity<SessionRequestResponse> createSessionRequest(@Valid @RequestBody
                                                                       SessionRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.createSessionRequest(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SessionRequestResponse> updateStatus(@Valid @PathVariable Long id,
                                                               @RequestBody UpdateSessionStatusRequest request) {
        return ResponseEntity.ok().body(sessionService.updateStatus(id,request.status()));
    }
}
