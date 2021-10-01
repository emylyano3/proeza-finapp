package proeza.finapp.patterns.command;


import io.vavr.control.Either;

public interface ICommand<T, E> {

	Either<E, T> execute();
}
