package br.com.gestorfinanceiro.mappers.impl.categoria;

import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoriaCreateMapperImpl implements Mapper<CategoriaEntity, CategoriaCreateDTO> {
    private final ModelMapper modelMapper;

    public CategoriaCreateMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoriaCreateDTO mapTo(CategoriaEntity categoriaEntity) {
        return modelMapper.map(categoriaEntity, CategoriaCreateDTO.class);
    }

    @Override
    public CategoriaEntity mapFrom(CategoriaCreateDTO categoriaCreateDTO) {
        return modelMapper.map(categoriaCreateDTO, CategoriaEntity.class);
    }
}
