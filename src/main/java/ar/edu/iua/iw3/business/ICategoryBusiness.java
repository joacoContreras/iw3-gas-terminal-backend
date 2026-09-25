package ar.edu.iua.iw3.business;

import java.util.List;

import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.FoundException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Category;

public interface ICategoryBusiness {
    public List<Category> list() throws BusinessException;

    public Category load(Long id) throws NotFoundException, BusinessException;

    public Category load(String category) throws NotFoundException, BusinessException;

    public Category add(Category category) throws FoundException, BusinessException;

    public Category update(Category category) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;
}