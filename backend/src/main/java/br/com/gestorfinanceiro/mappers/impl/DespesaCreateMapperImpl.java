package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.despesa.DespesaCreateDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import org.springframework.stereotype.Component;

@Component
public class DespesaCreateMapperImpl implements Mapper<DespesaEntity, DespesaCreateDTO> {

    @Override
    public DespesaCreateDTO mapTo(DespesaEntity despesaEntity) {
        DespesaCreateDTO dto = new DespesaCreateDTO();
        dto.setData(despesaEntity.getData());
        dto.setValor(despesaEntity.getValor());
        dto.setDestinoPagamento(despesaEntity.getDestinoPagamento());
        dto.setObservacoes(despesaEntity.getObservacoes());
        return dto;
    }

    @Override
    public DespesaEntity mapFrom(DespesaCreateDTO dto) {
        DespesaEntity entity = new DespesaEntity();
        entity.setData(dto.getData());
        entity.setValor(dto.getValor());
        entity.setDestinoPagamento(dto.getDestinoPagamento());
        entity.setObservacoes(dto.getObservacoes());
        entity.setCategoria(DespesasCategorias.valueOf(dto.getCategoria()));
        return entity;
    }
}
