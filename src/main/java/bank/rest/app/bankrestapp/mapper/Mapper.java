package bank.rest.app.bankrestapp.mapper;

@FunctionalInterface
public interface Mapper<T,R>  {

    R toDto(T entity);
}
