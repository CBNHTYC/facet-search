package fs.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "phones")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id")
    private Long phoneId;

    private Integer price;
    private String model;
    private String producer;
    private Integer diagonal;
    private Integer ram;
    private Integer accumulator;
    private Integer sim;
    private Integer views;

    @OneToMany(mappedBy = "phone", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Image> phoneImages;

    @OneToMany(mappedBy = "phone", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> phoneComments;
}
