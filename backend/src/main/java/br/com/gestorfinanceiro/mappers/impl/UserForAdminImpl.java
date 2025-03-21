package br.com.gestorfinanceiro.mappers.impl;

import br.com.gestorfinanceiro.dto.UserForAdminDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserForAdminImpl implements Mapper<UserEntity, UserForAdminDTO> {
    private final ModelMapper modelMapper;

    public UserForAdminImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserForAdminDTO mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserForAdminDTO.class);
    }

    @Override
    public UserEntity mapFrom(UserForAdminDTO userWithStatusDTO) {
        return modelMapper.map(userWithStatusDTO, UserEntity.class);
    }
}
