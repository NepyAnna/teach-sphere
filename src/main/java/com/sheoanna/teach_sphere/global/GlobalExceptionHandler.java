package com.sheoanna.teach_sphere.global;

import com.sheoanna.teach_sphere.auth.exceptions.RefreshTokenCookiesNotFoundException;
import com.sheoanna.teach_sphere.category.exceptions.CategoryAlreadyExistsException;
import com.sheoanna.teach_sphere.category.exceptions.CategoryNotFoundException;
import com.sheoanna.teach_sphere.mentor_subject.exceptions.MentorSubjectNotFoundException;
import com.sheoanna.teach_sphere.profile.exceptions.ProfileAlreadyExistsException;
import com.sheoanna.teach_sphere.profile.exceptions.ProfileNotFoundException;
import com.sheoanna.teach_sphere.review.exception.MentorSubjectReviewNotFoundException;
import com.sheoanna.teach_sphere.session_request.exceptions.SessionRequestNotFoundException;
import com.sheoanna.teach_sphere.subject.exceptions.SubjectByNameAlreadyExistsException;
import com.sheoanna.teach_sphere.subject.exceptions.SubjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RefreshTokenCookiesNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleRefreshTokenCookiesNotFoundException(RefreshTokenCookiesNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Oops! " + exp.getMessage() + ". Please log in again.");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException exp) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication Failed " + exp.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(Exception exp) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred " + exp.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<String> handleProfileNotFoundException(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ProfileAlreadyExistsException.class)
    public ResponseEntity<String> handleProfileAlreadyExistsException(ProfileAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<String> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<String> handleSubjectNotFoundException(SubjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SubjectByNameAlreadyExistsException.class)
    public ResponseEntity<String> handleSubjectByNameAlreadyExistsException(SubjectByNameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MentorSubjectNotFoundException.class)
    public ResponseEntity<String> handleMentorSubjectNotFoundByIDException(MentorSubjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SessionRequestNotFoundException.class)
    public ResponseEntity<String> handleSessionRequestNotFoundException(SessionRequestNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MentorSubjectReviewNotFoundException.class)
    public ResponseEntity<String> handleMentorSubjectReviewNotFoundByIDException(MentorSubjectReviewNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }
}
