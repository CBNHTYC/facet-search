package ru.kubsu.fs.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image")
    private Long imageId;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_model", nullable = false)
    private Model model;
}
