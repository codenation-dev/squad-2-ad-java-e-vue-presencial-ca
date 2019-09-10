package br.com.codenation.logstackapi.audit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

    @NotNull
    @CreatedDate
    private LocalDateTime createdDate;

    @NotNull
    @CreatedBy
    @ManyToOne(optional = false)
    private U createdBy;

    @NotNull
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @NotNull
    @LastModifiedBy
    @ManyToOne(optional = false)
    private U lastModifiedBy;

}
