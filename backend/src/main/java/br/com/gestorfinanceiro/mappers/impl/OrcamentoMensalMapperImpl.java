package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.OrcamentoMensal.OrcamentoMensalDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.OrcamentoMensalEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoMensalMapperImpl implements Mapper<OrcamentoMensalEntity, OrcamentoMensalDTO> {

    private final ModelMapper modelMapper;

    public OrcamentoMensalMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public OrcamentoMensalDTO mapTo(OrcamentoMensalEntity orcamentoMensalEntity) {
        OrcamentoMensalDTO dto = modelMapper.map(orcamentoMensalEntity, OrcamentoMensalDTO.class);
        dto.setCategoria(orcamentoMensalEntity.getCategoria().name());
        return dto;
    }

    @Override
    public OrcamentoMensalEntity mapFrom(OrcamentoMensalDTO orcamentoMensalDTO) {
        OrcamentoMensalEntity entity = modelMapper.map(orcamentoMensalDTO, OrcamentoMensalEntity.class);
        entity.setCategoria(DespesasCategorias.valueOf(orcamentoMensalDTO.getCategoria()));
        return entity;
    }
}
