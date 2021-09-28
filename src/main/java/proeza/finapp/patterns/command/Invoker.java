package proeza.finapp.patterns.command;

import io.vavr.control.Either;

public class Invoker {

    public <T, E> Either<E, T> invoke(ICommand<T, E> c) {
        return c.execute();
    }
}
