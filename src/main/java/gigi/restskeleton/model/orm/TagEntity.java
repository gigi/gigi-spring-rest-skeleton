package gigi.restskeleton.model.orm;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@Table(
    name = "tag",
    indexes = {@Index(name = "tag_name_idx", columnList = "name")})
@AllArgsConstructor
@NoArgsConstructor
public class TagEntity {
  @Id private UUID id;

  private String name;
}
