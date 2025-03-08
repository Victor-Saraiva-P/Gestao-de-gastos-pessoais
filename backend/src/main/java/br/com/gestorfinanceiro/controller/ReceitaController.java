package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.dto.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.dto.ReceitaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.services.JwtUtil;
import br.com.gestorfinanceiro.services.ReceitaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
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
    public ResponseEntity<ReceitaDTO> criarReceita(@Valid @RequestBody ReceitaDTO receitaDTO, HttpServletRequest request) {
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

        // Checa se o usuário logado é o dono da receita
        if (!Objects.equals(userId, receita.getUser().getUuid())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(receitaMapper.mapTo(receita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaDTO> atualizarReceita(@PathVariable String id, @Valid @RequestBody ReceitaDTO receitaDTO) {
        ReceitaEntity receitaAtualizada = receitaMapper.mapFrom(receitaDTO);
        ReceitaEntity receitaSalva = receitaService.atualizarReceita(id, receitaAtualizada);
        return ResponseEntity.ok(receitaMapper.mapTo(receitaSalva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirReceita(@PathVariable String id) {
        receitaService.excluirReceita(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grafico-pizza")
    public ResponseEntity<GraficoPizzaDTO> gerarGraficoPizza(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        GraficoPizzaDTO graficoPizza = receitaService.gerarGraficoPizza(userId, inicio, fim);

        return ResponseEntity.ok(graficoPizza);
    }

    @GetMapping("/grafico-barras")
    public ResponseEntity<GraficoBarraDTO> gerarGraficoBarrasReceita(@RequestParam YearMonth inicio, @RequestParam YearMonth fim, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(receitaService.gerarGraficoBarras(userId, inicio, fim));
    }
}

