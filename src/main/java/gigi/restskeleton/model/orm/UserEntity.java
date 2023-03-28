package gigi.restskeleton.model.orm;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(
    name = "`user`",
    indexes = {@Index(name = "user_nickname_idx", columnList = "nickname", unique = true)})
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
  @Id private UUID id;
  private String nickname;
}
