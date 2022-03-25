package main.repository;

import main.model.User;
import org.springframework.data.repository.CrudRepository;

public interface CheckRepository extends CrudRepository<User, Integer> {
}
