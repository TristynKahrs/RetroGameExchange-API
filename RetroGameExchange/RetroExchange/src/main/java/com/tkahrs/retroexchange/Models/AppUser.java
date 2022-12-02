package com.tkahrs.retroexchange.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.tkahrs.retroexchange.Views.JSONViews;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Data
@Entity
public class AppUser extends RepresentationModel<AppUser> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(JSONViews.OfferView.class)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @JsonView(JSONViews.OfferView.class)
    private String name;

    @Column(nullable = false)
    @JsonView(JSONViews.OfferView.class)
    private String email;

    @Column(nullable = false)
    @JsonView(JSONViews.OfferView.class)
    private String password;

    @JsonView(JSONViews.OfferView.class)
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<VideoGame> games = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "offerer")
    private List<Trade> outboundOffers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver")
    private List<Trade> inboundOffers = new ArrayList<>();

    public void addGame(VideoGame game) {
        games.add(game);
    }

    public void removeGame(VideoGame game){
        this.games.remove(game);
    }

    public void addOfferIn(Trade offer){
        this.inboundOffers.add(offer);
    }

    public void addOfferOut(Trade offer){
        this.inboundOffers.add(offer);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<VideoGame> getGames() {
        return games;
    }

    public void setGames(List<VideoGame> games) {
        this.games = games;
    }

    public List<Trade> getOutboundOffers() {
        return outboundOffers;
    }

    public void setOutboundOffers(List<Trade> outboundOffers) {
        this.outboundOffers = outboundOffers;
    }

    public List<Trade> getInboundOffers() {
        return inboundOffers;
    }

    public void setInboundOffers(List<Trade> inboundOffers) {
        this.inboundOffers = inboundOffers;
    }
}
