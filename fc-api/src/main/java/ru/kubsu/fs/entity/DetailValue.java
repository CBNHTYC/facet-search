package ru.kubsu.fs.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "detail_value")
public class DetailValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail_value")
    private Long detailValueId;

    private String value;
    private String unit;

    @ManyToOne
    @JoinColumn(name = "id_model", nullable = false)
    Model model;

    @ManyToOne
    @JoinColumn(name = "id_detail", nullable = false)
    Detail detail;

}
