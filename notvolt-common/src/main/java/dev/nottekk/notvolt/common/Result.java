package dev.nottekk.notvolt.common;

import java.util.Objects;
import java.util.Optional;

public sealed interface Result<T> permits Result.Ok, Result.Err {
	boolean isOk();
	boolean isErr();
	Optional<T> getOrEmpty();
	T getOrThrow();
	Throwable getErrorOrNull();

	static <T> Ok<T> ok(T value) { return new Ok<>(value); }
	static <T> Err<T> err(Throwable error) { return new Err<>(error); }

	final class Ok<T> implements Result<T> {
		private final T value;

		public Ok(T value) { this.value = value; }
		public boolean isOk() { return true; }
		public boolean isErr() { return false; }
		public Optional<T> getOrEmpty() { return Optional.ofNullable(value); }
		public T getOrThrow() { return value; }
		public Throwable getErrorOrNull() { return null; }
		public String toString() { return "Ok(" + Objects.toString(value) + ")"; }
	}

	final class Err<T> implements Result<T> {
		private final Throwable error;

		public Err(Throwable error) { this.error = Objects.requireNonNull(error); }
		public boolean isOk() { return false; }
		public boolean isErr() { return true; }
		public Optional<T> getOrEmpty() { return Optional.empty(); }
		public T getOrThrow() { throw new RuntimeException(error); }
		public Throwable getErrorOrNull() { return error; }
		public String toString() { return "Err(" + error.getClass().getSimpleName() + ":" + error.getMessage() + ")"; }
	}
}
