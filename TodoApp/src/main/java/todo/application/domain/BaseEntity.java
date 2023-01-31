package todo.application.domain;

import lombok.Data;
import org.apache.tomcat.jni.Local;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class BaseEntity {

    @Column(updatable = false, insertable = true)
    private LocalDateTime createdTime;

    @Column(updatable = true, insertable = false)
    private LocalDateTime lastModifiedTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdTime = now;
        lastModifiedTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedTime = LocalDateTime.now();
    }

}
