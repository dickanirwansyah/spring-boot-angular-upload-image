package id.backend.springbootbackend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageModelRepository extends JpaRepository<ImageModel, Long> {

    Optional<ImageModel> findByName(String name);
}
