package com.mtnfog.philter.registry.exceptions;

import com.mtnfog.philter.model.exceptions.InvalidFilterProfile;
import com.mtnfog.philter.model.exceptions.api.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
public class RestApiExceptions {

	private static final Logger LOGGER = LogManager.getLogger(RestApiExceptions.class);

	@ResponseBody
	@ExceptionHandler({BadRequestException.class, InvalidFilterProfile.class, MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String handleMissingParameterException(Exception ex) {
		LOGGER.error("A parameter was missing or invalid.", ex);
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public String handleNotFound(Exception ex) {
		final String message = "The requested resource was not found.";
		LOGGER.error(message, ex);
		return message;
	}

	@ResponseBody
	@ExceptionHandler({IOException.class, Exception.class})
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleUnknownException(Exception ex) {
		final String message = "An unknown error has occurred. Check the server log.";
		LOGGER.error(message, ex);
	    return message;
	}
	
}
