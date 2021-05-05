package proeza.finapp.command;

import proeza.finapp.exception.EntityNotFoundException;

import javax.transaction.Transactional;

abstract class CarteraAbstractCommand implements ICommand {

    @Override
    @Transactional
    public void execute() {
        validate();
        loadContext();
        businessLogic();
    }

    protected EntityNotFoundException entityNotFoundException(String entityName, Long id) {
        return new EntityNotFoundException(String.format("No se encontro la entidad %s con id %s", entityName, id));
    }

    protected abstract void businessLogic();

    protected abstract void validate();

    protected abstract void loadContext();
}
