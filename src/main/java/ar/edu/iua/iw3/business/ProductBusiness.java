package ar.edu.iua.iw3.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.FoundException;
import ar.edu.iua.iw3.model.Product;
import ar.edu.iua.iw3.model.persistence.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Service // Este es el candidato a instanciar cuando alguien quiera algo de tipo IProductBusiness
@Slf4j // Sistema de log para java
public class ProductBusiness implements IProductBusiness {

    // IOC
    @Autowired
    private ProductRepository productDAO;

    @Override
    public Product add(Product product) throws FoundException, BusinessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Product> list() throws BusinessException {
        try {
            // TODO Auto-generated method stub
            return productDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Product load(long id) throws NotFoundException, BusinessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Product load(String product) throws NotFoundException, BusinessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Product update(Product product) throws NotFoundException, BusinessException {
        // TODO Auto-generated method stub
        return null;
    }

}
