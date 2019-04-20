package ru.kubsu.fs.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "detail")
public class Detail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail")
    private Long detailId;

    private String name;
    @Column(name = "id_parent_detail")
    private Long parentDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "detail", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetailValue> detailValueList;
}
