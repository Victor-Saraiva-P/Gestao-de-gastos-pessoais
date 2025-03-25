package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.categoria.CategoriaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapperImpl implements Mapper<CategoriaEntity, CategoriaDTO> {

    private final ModelMapper modelMapper;

    public CategoriaMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoriaDTO mapTo(CategoriaEntity categoriaEntity) {
        return modelMapper.map(categoriaEntity, CategoriaDTO.class);
    }

    @Override
    public CategoriaEntity mapFrom(CategoriaDTO categoriaDTO) {
        return modelMapper.map(categoriaDTO, CategoriaEntity.class);
    }
}
