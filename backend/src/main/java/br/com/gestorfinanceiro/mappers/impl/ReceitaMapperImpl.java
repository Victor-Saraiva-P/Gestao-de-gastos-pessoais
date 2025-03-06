package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.ReceitaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import org.modelmapper.ModelMapper;
import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import org.springframework.stereotype.Component;

@Component
public class ReceitaMapperImpl implements Mapper<ReceitaEntity, ReceitaDTO> {
    private final ModelMapper modelMapper;

    public ReceitaMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ReceitaDTO mapTo(ReceitaEntity receitaEntity) {
        ReceitaDTO dto = modelMapper.map(receitaEntity, ReceitaDTO.class);
        dto.setCategoria(receitaEntity.getCategoria().name());
        return dto;

    }

    @Override
    public ReceitaEntity mapFrom(ReceitaDTO receitaDTO) {
        ReceitaEntity entity = modelMapper.map(receitaDTO, ReceitaEntity.class);
        entity.setCategoria(ReceitasCategorias.valueOf(receitaDTO.getCategoria()));
        return entity;
    }
}
