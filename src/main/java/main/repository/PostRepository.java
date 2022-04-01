package main.repository;

import main.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
}
