package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.config.security.JwtUtil;
import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaUpdateDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.services.ReceitaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {
    private final ReceitaService receitaService;
    private final Mapper<ReceitaEntity, ReceitaDTO> receitaMapper;
    private final JwtUtil jwtUtil;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public ReceitaController(ReceitaService receitaService, Mapper<ReceitaEntity, ReceitaDTO> receitaMapper, JwtUtil jwtUtil) {
        this.receitaService = receitaService;
        this.receitaMapper = receitaMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ReceitaDTO> criarReceita(@Valid @RequestBody ReceitaCreateDTO receitaCreateDTO, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER)
                .replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        ReceitaEntity novaReceita = receitaService.criarReceita(receitaCreateDTO, userId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaReceita.getUuid())
                .toUri();

        return ResponseEntity.created(location)
                .body(receitaMapper.mapTo(novaReceita));
    }

    @GetMapping
    public ResponseEntity<List<ReceitaDTO>> listarReceitas(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER)
                .replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        List<ReceitaDTO> receitas = receitaService.listarReceitasUsuario(userId)
                .stream()
                .map(receitaMapper::mapTo)
                .toList();

        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaDTO> buscarReceitaPorId(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER)
                .replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);
        ReceitaEntity receita = receitaService.buscarReceitaPorId(id);

        // Checa se o usuário logado é o dono da receita
        if (!Objects.equals(userId, receita.getUser()
                .getUuid())) {
            return ResponseEntity.status(403)
                    .build();
        }

        return ResponseEntity.ok(receitaMapper.mapTo(receita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaDTO> atualizarReceita(@PathVariable String id, @Valid @RequestBody ReceitaUpdateDTO receitaUpdateDTO) {
        ReceitaEntity receitaSalva = receitaService.atualizarReceita(id, receitaUpdateDTO);
        return ResponseEntity.ok(receitaMapper.mapTo(receitaSalva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirReceita(@PathVariable String id) {
        receitaService.excluirReceita(id);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/grafico-pizza")
    public ResponseEntity<GraficoPizzaDTO> gerarGraficoPizza(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request) {

        String token = request.getHeader(AUTHORIZATION_HEADER)
                .replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        GraficoPizzaDTO graficoPizza = receitaService.gerarGraficoPizza(userId, inicio, fim);

        return ResponseEntity.ok(graficoPizza);
    }

    @GetMapping("/grafico-barras")
    public ResponseEntity<GraficoBarraDTO> gerarGraficoBarrasReceita(@RequestParam YearMonth inicio, @RequestParam YearMonth fim, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER)
                .replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(receitaService.gerarGraficoBarras(userId, inicio, fim));
    }

    @GetMapping("/por-intervalo-de-datas")
    public ResponseEntity<List<ReceitaDTO>> buscarReceitasPorIntervaloDeDatas(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request) {

        String token = request.getHeader(AUTHORIZATION_HEADER)
                .replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        List<ReceitaEntity> receitas = receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, fim);

        // Converte a lista de receitas para DTOs
        List<ReceitaDTO> receitasDTO = receitas.stream()
                .map(receitaMapper::mapTo)
                .toList();

        return ResponseEntity.ok(receitasDTO);
    }

    @GetMapping("/por-intervalo-de-valores")
    public ResponseEntity<List<ReceitaDTO>> buscarReceitasPorIntervaloDeValores(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            HttpServletRequest request) {

        String token = request.getHeader(AUTHORIZATION_HEADER)
                .replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        List<ReceitaEntity> receitas = receitaService.buscarReceitasPorIntervaloDeValores(userId, min, max);

        // Converte a lista de receitas para DTOs
        List<ReceitaDTO> receitasDTO = receitas.stream()
                .map(receitaMapper::mapTo)
                .toList();

        return ResponseEntity.ok(receitasDTO);
    }

}

