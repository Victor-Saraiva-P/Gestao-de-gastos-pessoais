package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.receita.ReceitaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ReceitaMapperImpl implements Mapper<ReceitaEntity, ReceitaDTO> {
    private final ModelMapper modelMapper;

    public ReceitaMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.typeMap(ReceitaDTO.class, ReceitaEntity.class)
                .addMappings(mapper -> mapper.skip(ReceitaEntity::setCategoria));
    }

    @Override
    public ReceitaDTO mapTo(ReceitaEntity receitaEntity) {
        ReceitaDTO dto = modelMapper.map(receitaEntity, ReceitaDTO.class);
        dto.setCategoria(receitaEntity.getCategoria()
                .name());
        dto.setCategoriaCustomizada(receitaEntity.getCategoriaCustomizada()
                .getNome());
        return dto;

    }

    @Override
    public ReceitaEntity mapFrom(ReceitaDTO receitaDTO) {
        ReceitaEntity entity = modelMapper.map(receitaDTO, ReceitaEntity.class);

        try {
            // Tentativa direta de converter (se a string já estiver no formato correto)
            entity.setCategoria(ReceitasCategorias.valueOf(receitaDTO.getCategoria()));
        } catch (IllegalArgumentException e) {
            // Alternativa: normalizar a string para o formato do enum
            String enumValue = receitaDTO.getCategoria()
                    .toUpperCase()
                    .replace(" ", "_");
            entity.setCategoria(ReceitasCategorias.valueOf(enumValue));
        }

        return entity;
    }
}
