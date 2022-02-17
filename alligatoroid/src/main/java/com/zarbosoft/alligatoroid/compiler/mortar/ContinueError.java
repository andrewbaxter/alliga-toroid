package com.zarbosoft.alligatoroid.compiler.mortar;

/**
 * Raised in wrapped methods -- a previous call failed so now in an "error state" so abort this
 * invocation and return an error result. The previous error would already have been logged.
 */
public class ContinueError extends RuntimeException {}
