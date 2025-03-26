package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import org.springframework.stereotype.Component;

@Component
public class ReceitaCreateMapperImpl implements Mapper<ReceitaEntity, ReceitaCreateDTO> {

    @Override
    public ReceitaCreateDTO mapTo(ReceitaEntity receitaEntity) {
        ReceitaCreateDTO dto = new ReceitaCreateDTO();
        dto.setData(receitaEntity.getData());
        dto.setValor(receitaEntity.getValor());
        dto.setOrigemDoPagamento(receitaEntity.getOrigemDoPagamento());
        dto.setObservacoes(receitaEntity.getObservacoes());
        return dto;
    }

    @Override
    public ReceitaEntity mapFrom(ReceitaCreateDTO dto) {
        ReceitaEntity entity = new ReceitaEntity();
        entity.setData(dto.getData());
        entity.setValor(dto.getValor());
        entity.setOrigemDoPagamento(dto.getOrigemDoPagamento());
        entity.setObservacoes(dto.getObservacoes());
        entity.setCategoria(ReceitasCategorias.valueOf(dto.getCategoria()));
        return entity;
    }
}
