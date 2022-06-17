package main.model.repository;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {
    Optional<PostVote> findByUserAndPost(User user, Post post);
}
