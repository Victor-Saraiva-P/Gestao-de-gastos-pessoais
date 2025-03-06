package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.dto.ReceitaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.services.ReceitaService;
import br.com.gestorfinanceiro.services.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {
    private final ReceitaService receitaService;
    private final Mapper<ReceitaEntity, ReceitaDTO> receitaMapper;
    private final JwtUtil jwtUtil;

    public ReceitaController(ReceitaService receitaService, Mapper<ReceitaEntity, ReceitaDTO> receitaMapper, JwtUtil jwtUtil) {
        this.receitaService = receitaService;
        this.receitaMapper = receitaMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ReceitaDTO> criarReceita(@RequestBody ReceitaDTO receitaDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        ReceitaEntity receita = receitaMapper.mapFrom(receitaDTO);
        ReceitaEntity novaReceita = receitaService.criarReceita(receita, userId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaReceita.getUuid()).toUri();

        return ResponseEntity.created(location).body(receitaMapper.mapTo(novaReceita));
    }

    @GetMapping
    public ResponseEntity<List<ReceitaDTO>> listarReceitas(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        List<ReceitaDTO> receitas = receitaService.listarReceitasUsuario(userId)
                .stream()
                .map(receitaMapper::mapTo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaDTO> buscarReceitaPorId(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        ReceitaEntity receita = receitaService.buscarReceitaPorId(id);

        if (receita == null) {
            return ResponseEntity.notFound().build();
        } else if (!Objects.equals(userId, receita.getUser().getUuid())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(receitaMapper.mapTo(receita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaDTO> atualizarReceita(@PathVariable String id, @RequestBody ReceitaDTO receitaDTO) {
        ReceitaEntity receitaAtualizada = receitaMapper.mapFrom(receitaDTO);
        ReceitaEntity receitaSalva = receitaService.atualizarReceita(id, receitaAtualizada);
        return ResponseEntity.ok(receitaMapper.mapTo(receitaSalva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirReceita(@PathVariable String id) {
        receitaService.excluirReceita(id);
        return ResponseEntity.noContent().build();
    }
}

