package me.wuwenbin.noteblogv4.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * created by Wuwenbin on 2018/12/18 at 23:09
 * @author wuwenbin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nb_project")
@Entity
@Builder
public class NBProject implements Serializable {

    /**
     * 主键id
     * 自增长生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, length = 11)
    private Long id;

    @Column(length = 11, nullable = false)
    @NotNull(message = "项目必须属于一个分类下")
    private Long cateId;

    @ManyToOne
    @JoinColumn(name = "cate_refer_id")
    private NBProjectCate projectCate;

    private String cover;

}
