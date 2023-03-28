package gigi.restskeleton.model.service;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DummyUserService implements UserService {
  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  public UUID getCurrentUserId() {
    return USER_ID;
  }
}
