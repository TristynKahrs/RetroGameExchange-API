package com.tkahrs.retroexchange.Controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.tkahrs.retroexchange.Models.AppUser;
import com.tkahrs.retroexchange.Models.VideoGame;
import com.tkahrs.retroexchange.Repositories.AppUserJpaRepository;
import com.tkahrs.retroexchange.Repositories.VideoGameJpaRepository;
import com.tkahrs.retroexchange.Views.JSONViews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/games")
public class GameRestController {
    @Autowired
    private AppUserJpaRepository userJpa;

    @Autowired
    private VideoGameJpaRepository gameJpa;

    @Autowired
    private BLL bll;
//    {
//        "gameName" : "God of War",
//            "publisher" : "Santa Monica",
//            "publishYear" : 2018,
//            "gamingSystem" : "PS4",
//            "gameCondition" : "GOOD"
//    }

    private ArrayList<Link> generateGameLinks(int id){
        ArrayList<Link> links = new ArrayList<>();
        Link linkSelf = Link.of(String.format("http://localhost:8080/games/%s", id), "self"); //adds the self link
        Link owner = Link.of(String.format("http://localhost:8080/games/user/%s", gameJpa.getById(id).getOwner().getId()), "owner"); //adds the gameList link
        links.add(linkSelf);
        links.add(owner);
        return links;
    }

    @PostMapping(path = "/addGame")
    @ResponseStatus(code= HttpStatus.CREATED)
    public VideoGame addGame(@RequestHeader(value="Authorization") String authorizationHeader, @RequestBody VideoGame game) {
        AppUser curUser = userJpa.getByEmail(BLL.decodeAuth(authorizationHeader)[0]);
        curUser.addGame(game);
        game.setOwner(curUser);
//        for(Link link : generateGameLinks(game.getId())){
//            game.add(link);
//        }
        userJpa.save(curUser);
        gameJpa.save(game);
        return game;
    }

    @GetMapping(path = {"/user", "/user/{id}"}) // gets all your games
    @JsonView(JSONViews.OfferView.class)
    public List<VideoGame> getGames(@RequestHeader(value="Authorization") String authorizationHeader, @PathVariable(required = false) String id) {
        String email = BLL.decodeAuth(authorizationHeader)[0];
        AppUser curUser = userJpa.getByEmail(email);
        if(id != null) {
            try {
                curUser = userJpa.getById(Integer.parseInt(id));
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException("Needs a number");
            }
        }
        return curUser.getGames();
    }

    @GetMapping("/{search}") // gets all by name
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @JsonView(JSONViews.OfferView.class)
    public List<VideoGame> searchGames(@PathVariable String search){
        List<VideoGame> returnValue = gameJpa.findAllByGameName(search);
        for(VideoGame game : returnValue) {
            for(Link link : generateGameLinks(game.getId())){
                game.add(link);
            }
        }
        return returnValue;
    }

    @PutMapping("/{name}")
    @ResponseStatus(code = HttpStatus.ACCEPTED) // make sure they have that actual game
    public void updateGame(@RequestHeader(value="Authorization") String authorizationHeader, @PathVariable String name, @RequestBody VideoGame updateGame){
        String email = BLL.decodeAuth(authorizationHeader)[0];
        AppUser curUser = userJpa.getByEmail(email);
        VideoGame game = gameJpa.findByGameName(name);
        if(curUser.getGames().contains(game)) {
            game.setGamingSystem(updateGame.getGamingSystem());
            game.setGameName(updateGame.getGameName());
            game.setPublisher(updateGame.getPublisher());
            game.setPublishYear(updateGame.getPublishYear());
            this.gameJpa.save(game);
        }
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(code = HttpStatus.ACCEPTED) // TODO make sure they actually have the game you fucking idiot
    public void deleteGame(@RequestHeader(value="Authorization") String authorizationHeader, @PathVariable String name){
        String email = BLL.decodeAuth(authorizationHeader)[0];
        AppUser curUser = userJpa.getByEmail(email);
        VideoGame game = gameJpa.findByGameName(name);
        if(curUser.getGames().contains(game)) {
            this.gameJpa.delete(game);
        }
    }
}
