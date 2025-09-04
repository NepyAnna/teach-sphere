package com.sheoanna.teach_sphere.user;

import com.sheoanna.teach_sphere.user.dtos.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "Operations related to user")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    @Operation(summary = "Get all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users returned successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Page<UserResponse>> findAllUsers(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users returned successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<UserResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(userService.findById(id));
    }
}
