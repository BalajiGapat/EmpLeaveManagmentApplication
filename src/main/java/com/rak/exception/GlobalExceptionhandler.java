package com.rak.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionhandler 
{
	@ExceptionHandler(EmailIdAlreadyExistException.class)
	public ResponseEntity<ErrorDetails> EmailIdAlreadyExistExceptionHandler(EmailIdAlreadyExistException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		log.error("email id already exist...!!!");
		return  ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	@ExceptionHandler(InsufficientLeaveBalanceException.class)
	public ResponseEntity<ErrorDetails> InsufficientLeaveBalanceExceptionHandler(InsufficientLeaveBalanceException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		log.error("insufficient leave balance...!!!");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(RoleAlreadyPresentException.class)
	public ResponseEntity<ErrorDetails> RoleAlreadyPresentExceptionHandler(RoleAlreadyPresentException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		log.error("role Already present...!!!");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorDetails> IllegalArgumentExceptionHandler(IllegalArgumentException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(NoLeaveFoundException.class)
	public ResponseEntity<ErrorDetails> NoleaveFoundExceptionHandler(NoLeaveFoundException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(IncorrectPasswordException.class)
	public ResponseEntity<ErrorDetails> IncorrectPasswordExceptionHandler(IncorrectPasswordException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetails> ResourceNotFoundExceptionHandler(ResourceNotFoundException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(RoleNotFoundException.class)
	public ResponseEntity<ErrorDetails> RoleNotFoundExceptionHandler(RoleNotFoundException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		log.error("Role Not found...!!!");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>>/*ResponseEntity<ErrorDetails>*/ MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, WebRequest request)
	{
		//ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		
		 Map<String, String> errors = new HashMap<>();
	     ex.getBindingResult().getFieldErrors().forEach(error -> 
	            errors.put(error.getField(), error.getDefaultMessage())
	        );
	     
	     return new ResponseEntity<Map<String, String>>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorDetails> HandlerMethodValidationExceptionHadler(HandlerMethodValidationException ex, WebRequest request)
	{
		ErrorDetails error=new ErrorDetails(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
}
