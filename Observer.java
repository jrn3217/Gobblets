package common;

import model.ClientData;

public interface Observer<Subject> {

    void update(Subject subject, ClientData clientData);

}
