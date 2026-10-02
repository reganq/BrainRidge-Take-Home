package org.app.bank.exception;

/*
 * Representation of the JSON error response
*/
public record ErrorResponse(int status, String message) {}