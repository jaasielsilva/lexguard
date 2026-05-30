package com.jaasielsilva.lexguard.repository;

import com.jaasielsilva.lexguard.model.DataAccessLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataAccessLogRepository extends JpaRepository<DataAccessLog, Long> {

    List<DataAccessLog> findByEmpresaId(Long empresaId);
}
