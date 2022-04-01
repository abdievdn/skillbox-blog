package main.repository;

import main.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckRepository extends CrudRepository<User, Integer> {
}
