package com.sheoanna.teach_sphere.session_request;

import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestRequest;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestResponse;
import com.sheoanna.teach_sphere.session_request.dtos.UpdateSessionStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session_requests")
@RequiredArgsConstructor
@Tag(name = "Session Request", description = "Operations related to Session Request.")
public class SessionRequestController {
    private final SessionRequestService sessionService;

    @GetMapping("/student")
    @Operation(summary = "Get all Session Request for STUDENT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session Request returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Page<SessionRequestResponse>> findRequestsForStudent(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sessionService.findRequestsForStudent(pageable));
    }

    @GetMapping("/mentor")
    @Operation(summary = "Get all Session Request for MENTOR.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session Request returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Page<SessionRequestResponse>> findRequestsForMentor(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sessionService.findRequestsForMentor(pageable));
    }

    @PostMapping("")
    @Operation(summary = "Get Session Request by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session Request returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/SessionRequestNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<SessionRequestResponse> createSessionRequest(@Valid @RequestBody
                                                                       SessionRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.createSessionRequest(request));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update status of Session Request.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Session Request status updated successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/SessionRequestNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<SessionRequestResponse> updateStatus(@Valid @PathVariable Long id,
                                                               @RequestBody UpdateSessionStatusRequest request) {
        return ResponseEntity.ok().body(sessionService.updateStatus(id,request.status()));
    }
}
