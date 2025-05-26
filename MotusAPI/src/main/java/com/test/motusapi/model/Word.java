package com.test.motusapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private Integer length;

    @Column(nullable = false)
    private Integer difficulty;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Score> scores = new ArrayList<>();
}
