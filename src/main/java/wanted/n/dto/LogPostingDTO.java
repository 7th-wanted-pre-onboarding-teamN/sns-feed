package wanted.n.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 임시 posting 데이터
 */
@Getter
@Setter
@AllArgsConstructor
public class LogPostingDTO {
    private List<Long> tag;
}
