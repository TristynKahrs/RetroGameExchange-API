package com.tkahrs.retroexchange.Models;

import com.fasterxml.jackson.annotation.JsonView;
import com.tkahrs.retroexchange.Models.AppUser;
import com.tkahrs.retroexchange.Views.JSONViews;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

@Entity
public class VideoGame extends RepresentationModel<VideoGame> {

    private enum Condition {MINT, GOOD, FAIR, POOR};

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(JSONViews.OfferView.class)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @JsonView(JSONViews.OfferView.class)
    private String gameName;

    @JsonView(JSONViews.OfferView.class)
    private String publisher;

    @JsonView(JSONViews.OfferView.class)
    private int publishYear;

    @JsonView(JSONViews.OfferView.class)
    private String gamingSystem;

    @JsonView(JSONViews.OfferView.class)
    private Condition gameCondition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private AppUser owner;

    public VideoGame() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public String getGamingSystem() {
        return gamingSystem;
    }

    public void setGamingSystem(String gamingSystem) {
        this.gamingSystem = gamingSystem;
    }

    public Condition getGameCondition() {
        return gameCondition;
    }

    public void setGameCondition(Condition gameCondition) {
        this.gameCondition = gameCondition;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }
}
