package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.dto.DespesaDTO;
import br.com.gestorfinanceiro.dto.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.dto.ReceitaDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.services.DespesaService;
import br.com.gestorfinanceiro.services.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/despesas")
public class DespesaController {
    private final DespesaService despesaService;
    private final Mapper<DespesaEntity, DespesaDTO> despesaMapper;
    private final JwtUtil jwtUtil;

    public DespesaController(DespesaService despesaService, Mapper<DespesaEntity, DespesaDTO> despesaMapper, JwtUtil jwtUtil) {
        this.despesaService = despesaService;
        this.despesaMapper = despesaMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<DespesaDTO> criarDespesa(@Valid @RequestBody DespesaDTO despesaDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        DespesaEntity despesa = despesaMapper.mapFrom(despesaDTO);
        DespesaEntity novaDespesa = despesaService.criarDespesa(despesa, userId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaDespesa.getUuid()).toUri();

        return ResponseEntity.created(location).body(despesaMapper.mapTo(novaDespesa));
    }

    @GetMapping
    public ResponseEntity<List<DespesaDTO>> listarDespesas(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        List<DespesaDTO> despesas = despesaService.listarDespesasUsuario(userId)
                .stream()
                .map(despesaMapper::mapTo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(despesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaDTO> buscarDespesaPorId(@PathVariable String id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        DespesaEntity despesa = despesaService.buscarDespesaPorId(id);

        // Checa se o usuário logado é o dono da despesa
        if (!Objects.equals(userId, despesa.getUser().getUuid())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(despesaMapper.mapTo(despesa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaDTO> atualizarDespesa(@PathVariable String id, @Valid @RequestBody DespesaDTO despesaDTO) {
        DespesaEntity despesaAtualizada = despesaMapper.mapFrom(despesaDTO);
        DespesaEntity despesaSalva = despesaService.atualizarDespesa(id, despesaAtualizada);
        return ResponseEntity.ok(despesaMapper.mapTo(despesaSalva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDespesa(@PathVariable String id) {
        despesaService.excluirDespesa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grafico-barras")
    public ResponseEntity<GraficoBarraDTO> gerarGraficoBarrasDespesa(@RequestParam YearMonth inicio, @RequestParam YearMonth fim, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(despesaService.gerarGraficoBarras(userId, inicio, fim));
    }

    @GetMapping("/grafico-pizza")
    public ResponseEntity<GraficoPizzaDTO> gerarGraficoPizza(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        GraficoPizzaDTO graficoPizza = despesaService.gerarGraficoPizza(userId, inicio, fim);

        return ResponseEntity.ok(graficoPizza);
    }

    @GetMapping("/por-intervalo-de-datas")
    public ResponseEntity<List<DespesaDTO>> buscarReceitasPorIntervaloDeDatas(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        List<DespesaEntity> despesas = despesaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, fim);

        // Converte a lista de receitas para DTOs
        List<DespesaDTO> despesaDTO = despesas.stream()
                .map(despesaMapper::mapTo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(despesaDTO);
    }

    @GetMapping("/por-intervalo-de-valores")
    public ResponseEntity<List<DespesaDTO>> buscarReceitasPorIntervaloDeValores(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);

        List<DespesaEntity> despesas = despesaService.buscarReceitasPorIntervaloDeValores(userId, min, max);

        // Converte a lista de receitas para DTOs
        List<DespesaDTO> despesaDTO = despesas.stream()
                .map(despesaMapper::mapTo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(despesaDTO);
    }
}

