package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.DespesaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DespesaMapperImpl implements Mapper<DespesaEntity, DespesaDTO> {
    private final ModelMapper modelMapper;

    public DespesaMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public DespesaDTO mapTo(DespesaEntity despesaEntity) {
        DespesaDTO dto = modelMapper.map(despesaEntity, DespesaDTO.class);
        dto.setCategoria(despesaEntity.getCategoria().name());
        return dto;
    }

    @Override
    public DespesaEntity mapFrom(DespesaDTO despesaDTO) {
        DespesaEntity entity = modelMapper.map(despesaDTO, DespesaEntity.class);
        entity.setCategoria(DespesasCategorias.valueOf(despesaDTO.getCategoria()));
        return entity;
    }
}