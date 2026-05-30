package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.compliance.ComplianceData;
import com.jaasielsilva.lexguard.model.DataClassification;
import com.jaasielsilva.lexguard.repository.AuditLogRepository;
import com.jaasielsilva.lexguard.repository.ConsentimentoRepository;
import com.jaasielsilva.lexguard.repository.DadoPessoalRepository;
import com.jaasielsilva.lexguard.repository.TitularRepository;
import com.jaasielsilva.lexguard.repository.UsuarioRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ComplianceDataService {

    private final DadoPessoalRepository dadoPessoalRepository;
    private final ConsentimentoRepository consentimentoRepository;
    private final TitularRepository titularRepository;
    private final AuditLogRepository auditLogRepository;
    private final UsuarioRepository usuarioRepository;

    public ComplianceDataService(
            DadoPessoalRepository dadoPessoalRepository,
            ConsentimentoRepository consentimentoRepository,
            TitularRepository titularRepository,
            AuditLogRepository auditLogRepository,
            UsuarioRepository usuarioRepository) {
        this.dadoPessoalRepository = dadoPessoalRepository;
        this.consentimentoRepository = consentimentoRepository;
        this.titularRepository = titularRepository;
        this.auditLogRepository = auditLogRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ComplianceData collect() {
        Long empresaId = TenantContext.getEmpresaId();

        // Dados sensíveis ativos da empresa
        long totalDadosSensiveis = dadoPessoalRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream()
                .filter(d -> d.getClassificacao() == DataClassification.SENSIVEL)
                .count();

        // Dados sensíveis sem local de armazenamento definido = sem proteção declarada
        long dadosSensiveisSemProtecao = dadoPessoalRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream()
                .filter(d -> d.getClassificacao() == DataClassification.SENSIVEL
                        && (d.getLocalArmazenamento() == null || d.getLocalArmazenamento().isBlank()))
                .count();

        // Usuários com senha que não começa com $2 (BCrypt) = sem hash adequado
        long usuariosSemHashSenha = usuarioRepository.findByEmpresaId(empresaId)
                .stream()
                .filter(u -> u.getPassword() == null || !u.getPassword().startsWith("$2"))
                .count();

        // Consentimentos revogados mas ainda marcados como ativos (inconsistência)
        long consentimentosExpirados = consentimentoRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream()
                .filter(c -> c.getRevogadoEm() != null && c.getRevogadoEm().isBefore(Instant.now()))
                .count();

        // Titulares sem nenhum consentimento ativo
        long totalTitulares = titularRepository.findByEmpresaIdAndAtivoTrueAndSoftDeletedFalseOrderByNomeAsc(empresaId)
                .size();
        long titularesComConsentimento = consentimentoRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream()
                .map(c -> c.getTitular().getId())
                .distinct()
                .count();
        long titularesSemConsentimento = Math.max(0, totalTitulares - titularesComConsentimento);

        // Logs de auditoria nos últimos 10 dias
        Instant dezDiasAtras = Instant.now().minus(10, ChronoUnit.DAYS);
        long logsUltimos10Dias = auditLogRepository
                .findByEmpresaIdOrderByTimestampDesc(empresaId, PageRequest.of(0, 500))
                .stream()
                .filter(log -> log.getTimestamp().isAfter(dezDiasAtras))
                .count();

        // Política de privacidade: ausente se não há nenhum consentimento com versão de
        // termo definida
        boolean politicaPrivacidadeAusente = consentimentoRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream()
                .noneMatch(c -> c.getVersaoTermo() != null && !c.getVersaoTermo().isBlank());

        return ComplianceData.builder()
                .totalDadosSensiveis(totalDadosSensiveis)
                .dadosSensiveisSemProtecao(dadosSensiveisSemProtecao)
                .usuariosSemHashSenha(usuariosSemHashSenha)
                .consentimentosExpirados(consentimentosExpirados)
                .titularesSemConsentimento(titularesSemConsentimento)
                .logsUltimos10Dias(logsUltimos10Dias)
                .politicaPrivacidadeAusente(politicaPrivacidadeAusente)
                .build();
    }
}
