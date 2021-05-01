package proeza.finapp.command;

import javax.transaction.Transactional;

abstract class CarteraAbstractCommand implements ICommand {

    @Override
    @Transactional
    public void execute() {
        validate();
        loadContext();
        businessLogic();
    }

    protected abstract void businessLogic();

    protected abstract void validate();

    protected abstract void loadContext();
}
