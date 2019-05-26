package ru.kubsu.fs.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "view")
public class View {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_view")
    private Long view_id;

    private Integer time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_model", nullable = false)
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
