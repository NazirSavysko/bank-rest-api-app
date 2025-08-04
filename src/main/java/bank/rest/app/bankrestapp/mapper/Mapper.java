package bank.rest.app.bankrestapp.mapper;

import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetCardDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.utils.MapperUtils;


@FunctionalInterface
public interface Mapper<T,R>  {


    R toDto(T entity);
}
