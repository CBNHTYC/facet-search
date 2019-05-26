package ru.kubsu.fs.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long userId;

    private String email;
    private String password;
    @Column(name = "aw_price")
    private Integer awPrice;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<View> viewList;

    public List<View> getViewList() {
        if (viewList == null) {
            return new ArrayList<>();
        }
        return this.viewList;
    }
}
