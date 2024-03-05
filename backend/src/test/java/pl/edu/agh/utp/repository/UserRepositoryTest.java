package pl.edu.agh.utp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.utp.model.nodes.User;

@SpringBootTest
public class UserRepositoryTest extends DbTest {

  @Autowired private UserRepository userRepository;

  @Test
  @Transactional
  public void shouldFindUserByEmail() {
    // given
    User user = new User("John", "test@example.com", "password");
    userRepository.save(user);

    // when
    var foundUser = userRepository.findByEmail("test@example.com");

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get()).isEqualTo(user);
  }

  @Test
  @Transactional
  public void shouldFindUserByEmail1() {
    // then
    var user = new User("John", "test1@example.com", "password");
    userRepository.save(user);

    // when
    var foundUser = userRepository.findByEmail("test@example.com");

    // then
    assertThat(foundUser).isEmpty();
  }
}
