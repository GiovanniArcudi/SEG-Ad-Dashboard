package model.services;

import model.models.ModelInstance;
import java.util.stream.Stream;

public abstract class DataProviderService {

    public abstract <T extends ModelInstance> Stream<T> getStream();

}
