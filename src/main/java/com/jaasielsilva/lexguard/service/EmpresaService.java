package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.empresa.EmpresaRequest;
import com.jaasielsilva.lexguard.dto.empresa.EmpresaResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.Empresa;
import com.jaasielsilva.lexguard.repository.EmpresaRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<EmpresaResponse> listAll() {
        return empresaRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmpresaResponse getById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    @Transactional
    public EmpresaResponse create(EmpresaRequest request) {
        empresaRepository.findByCnpj(normalizeCnpj(request.getCnpj()))
                .ifPresent(e -> {
                    throw new BadRequestException("Já existe uma empresa com este CNPJ");
                });

        Empresa empresa = new Empresa();
        // Empresa é a própria "tenant root" — empresaId aponta para si mesma após
        // salvar
        empresa.setNome(request.getNome().trim());
        empresa.setCnpj(normalizeCnpj(request.getCnpj()));
        empresa.setContatoEmail(request.getContatoEmail().trim().toLowerCase());
        empresa.setAtivo(true);

        // Salva primeiro para obter o ID gerado
        empresa = empresaRepository.save(empresa);

        // empresaId da BaseEntity aponta para o próprio ID (tenant self-reference)
        empresa.setEmpresaId(empresa.getId());
        empresa = empresaRepository.save(empresa);

        return mapToResponse(empresa);
    }

    @Transactional
    public EmpresaResponse update(Long id, EmpresaRequest request) {
        Empresa empresa = findOrThrow(id);

        String novoCnpj = normalizeCnpj(request.getCnpj());
        if (!empresa.getCnpj().equals(novoCnpj)) {
            empresaRepository.findByCnpj(novoCnpj).ifPresent(e -> {
                throw new BadRequestException("Já existe uma empresa com este CNPJ");
            });
        }

        empresa.setNome(request.getNome().trim());
        empresa.setCnpj(novoCnpj);
        empresa.setContatoEmail(request.getContatoEmail().trim().toLowerCase());
        empresa.setUpdatedAt(Instant.now());

        return mapToResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public EmpresaResponse toggleAtivo(Long id) {
        Empresa empresa = findOrThrow(id);
        empresa.setAtivo(!empresa.isAtivo());
        empresa.setUpdatedAt(Instant.now());
        return mapToResponse(empresaRepository.save(empresa));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Empresa findOrThrow(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
    }

    /** Remove formatação do CNPJ, mantém apenas dígitos. */
    private String normalizeCnpj(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }

    private EmpresaResponse mapToResponse(Empresa e) {
        return new EmpresaResponse(
                e.getId(),
                e.getNome(),
                e.getCnpj(),
                e.getContatoEmail(),
                e.isAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }
}
