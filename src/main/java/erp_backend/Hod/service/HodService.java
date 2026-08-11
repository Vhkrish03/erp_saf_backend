package erp_backend.Hod.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import erp_backend.Hod.entity.Hod;
import erp_backend.Hod.repository.HodRepository;

@Service
public class HodService {

    private final HodRepository repository;

    public HodService(HodRepository repository) {
        this.repository = repository;
    }

    public Hod getHodByEmployeeId(String employeeId) {
        Optional<Hod> hod = repository.findByEmployeeId(employeeId);
        return hod.orElse(null);
    }
}
