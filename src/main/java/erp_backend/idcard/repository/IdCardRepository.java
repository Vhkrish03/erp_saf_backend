package erp_backend.idcard.repository;

import erp_backend.idcard.entity.IdCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IdCardRepository extends JpaRepository<IdCard, Long> {
    Optional<IdCard> findByPersonIdAndPersonType(String personId, String personType);
}
