package com.tkahrs.retroexchange.Models;

import com.fasterxml.jackson.annotation.JsonView;
import com.tkahrs.retroexchange.Views.JSONViews;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//@Data
@Entity
public class Trade extends RepresentationModel<Trade> {

    public enum Status {PENDING, ACCEPTED, REJECTED};

    @Id
    @JsonView(JSONViews.OfferView.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    // User requesting the trade
    @ManyToOne()
    @JsonIgnore
    private AppUser offerer;

    @JsonIgnore
    @ManyToMany()
    @JoinTable(name = "offered_games", joinColumns = @JoinColumn(name = "offer_id"), inverseJoinColumns = @JoinColumn(name="game_id"))
    private List<VideoGame> offeredGames = new ArrayList<>();

    // User being asked to trade
    @ManyToOne
    @JsonIgnore
    private AppUser receiver;

    @JsonIgnore
    @ManyToMany()
    @JoinTable(name = "requested_games", joinColumns = @JoinColumn(name = "offer_id"), inverseJoinColumns = @JoinColumn(name="game_id"))
    private List<VideoGame> requestedGames = new ArrayList<>();

    @JsonView(JSONViews.OfferView.class)
    @Column(nullable = false)
    private Status status;

    @JsonView(JSONViews.OfferView.class)
    @Column
    private String offerTime;

    public Trade() {}

    public Trade(AppUser offerer, List<VideoGame> offeredGames, AppUser receiver, List<VideoGame> requestedGames) {
        setOfferer(offerer);
        setOfferedGames(offeredGames);
        setReceiver(receiver);
        setRequestedGames(requestedGames);
        setStatus(Status.PENDING);
        this.offerTime = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss").format(LocalDateTime.now());;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppUser getOfferer() {
        return offerer;
    }

    public void setOfferer(AppUser offerer) {
        this.offerer = offerer;
    }

    public List<VideoGame> getOfferedGames() {
        return offeredGames;
    }

    public void setOfferedGames(List<VideoGame> offeredGames) {
        this.offeredGames = offeredGames;
    }

    public AppUser getReceiver() {
        return receiver;
    }

    public void setReceiver(AppUser receiver) {
        this.receiver = receiver;
    }

    public List<VideoGame> getRequestedGames() {
        return requestedGames;
    }

    public void setRequestedGames(List<VideoGame> requestedGames) {
        this.requestedGames = requestedGames;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOfferTime() {
        return offerTime;
    }

    public void setOfferTime(String offerTime) {
        this.offerTime = offerTime;
    }
}
