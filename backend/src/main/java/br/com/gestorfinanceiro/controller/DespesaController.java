package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.config.security.JwtUtil;
import br.com.gestorfinanceiro.dto.despesa.DespesaCreateDTO;
import br.com.gestorfinanceiro.dto.despesa.DespesaDTO;
import br.com.gestorfinanceiro.dto.despesa.DespesaUpdateDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.services.DespesaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
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
@RequestMapping("/despesas")
public class DespesaController {
    private final DespesaService despesaService;
    private final Mapper<DespesaEntity, DespesaDTO> despesaMapper;
    private final JwtUtil jwtUtil;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public DespesaController(DespesaService despesaService, Mapper<DespesaEntity, DespesaDTO> despesaMapper, JwtUtil jwtUtil) {
        this.despesaService = despesaService;
        this.despesaMapper = despesaMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<DespesaDTO> criarDespesa(@Valid @RequestBody DespesaCreateDTO despesaCreateDTO, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        DespesaEntity novaDespesa = despesaService.criarDespesa(despesaCreateDTO, userId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaDespesa.getUuid()).toUri();

        return ResponseEntity.created(location).body(despesaMapper.mapTo(novaDespesa));
    }

    @GetMapping
    public ResponseEntity<List<DespesaDTO>> listarDespesas(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        List<DespesaDTO> despesas = despesaService.listarDespesasUsuario(userId)
                .stream()
                .map(despesaMapper::mapTo)
                .toList();

        return ResponseEntity.ok(despesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaDTO> buscarDespesaPorId(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);
        DespesaEntity despesa = despesaService.buscarDespesaPorId(id);

        // Checa se o usuário logado é o dono da despesa
        if (!Objects.equals(userId, despesa.getUser().getUuid())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(despesaMapper.mapTo(despesa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaDTO> atualizarDespesa(@PathVariable String id, @Valid @RequestBody DespesaUpdateDTO despesaUpdateDTO, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);
        DespesaEntity despesa = despesaService.buscarDespesaPorId(id);
    
        if (!Objects.equals(userId, despesa.getUser().getUuid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        DespesaEntity despesaAtualizada = despesaService.atualizarDespesa(id, despesaUpdateDTO);
        return ResponseEntity.ok(despesaMapper.mapTo(despesaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDespesa(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);
        DespesaEntity despesa = despesaService.buscarDespesaPorId(id);

        if (!Objects.equals(userId, despesa.getUser().getUuid())) {
            return ResponseEntity.status(403).build();
        }
    
        despesaService.excluirDespesa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grafico-barras")
    public ResponseEntity<GraficoBarraDTO> gerarGraficoBarrasDespesa(@RequestParam YearMonth inicio, @RequestParam YearMonth fim, HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(despesaService.gerarGraficoBarras(userId, inicio, fim));
    }

    @GetMapping("/grafico-pizza")
    public ResponseEntity<GraficoPizzaDTO> gerarGraficoPizza(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request) {

        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        GraficoPizzaDTO graficoPizza = despesaService.gerarGraficoPizza(userId, inicio, fim);

        return ResponseEntity.ok(graficoPizza);
    }

    @GetMapping("/por-intervalo-de-datas")
    public ResponseEntity<List<DespesaDTO>> buscarReceitasPorIntervaloDeDatas(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request) {

        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio, fim);

        // Converte a lista de receitas para DTOs
        List<DespesaDTO> despesaDTO = despesas.stream()
                .map(despesaMapper::mapTo)
                .toList();

        return ResponseEntity.ok(despesaDTO);
    }

    @GetMapping("/por-intervalo-de-valores")
    public ResponseEntity<List<DespesaDTO>> buscarReceitasPorIntervaloDeValores(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            HttpServletRequest request) {

        String token = request.getHeader(AUTHORIZATION_HEADER).replace(BEARER_PREFIX, "");
        String userId = jwtUtil.extractUserId(token);

        List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeValores(userId, min, max);

        // Converte a lista de receitas para DTOs
        List<DespesaDTO> despesaDTO = despesas.stream()
                .map(despesaMapper::mapTo)
                .toList();

        return ResponseEntity.ok(despesaDTO);
    }
}

