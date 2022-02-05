package todo.application.service;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class MemberServiceThreadFree {
    private Map<String, Integer> joinIdMap = new HashMap<>();
    private Map<String, Integer> joinNicknameMap = new HashMap<>();

}
