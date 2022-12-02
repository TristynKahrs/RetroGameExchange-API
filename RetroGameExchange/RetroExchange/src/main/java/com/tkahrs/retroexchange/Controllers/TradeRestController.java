package com.tkahrs.retroexchange.Controllers;

import com.tkahrs.retroexchange.Models.AppUser;
import com.tkahrs.retroexchange.Models.Trade;
import com.tkahrs.retroexchange.Models.VideoGame;
import com.tkahrs.retroexchange.Repositories.AppUserJpaRepository;
import com.tkahrs.retroexchange.Repositories.TradeJpaRepository;
import com.tkahrs.retroexchange.Repositories.VideoGameJpaRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/trade")
public class TradeRestController {
    @Autowired
    private TradeJpaRepository tradeJpa;

    @Autowired
    private AppUserJpaRepository userJpa;

    @Autowired
    private VideoGameJpaRepository gameJpa;

    @Autowired
    private BLL bll;

    @GetMapping(path = "/{id}")
    public Trade viewOffers(@PathVariable(required = false) Integer id) {
        Trade returnValue = tradeJpa.getById(id);
        for (Link link : generateTradeLinks(returnValue)) {
            returnValue.add(link);
        }
        return returnValue;
    }

    // get logged-in user, return trades by that users id
    @GetMapping(path = "/")
    public List<Trade> viewOffers(@RequestHeader(value="Authorization") String authorizationHeader) {
        AppUser curUser = userJpa.getByEmail(BLL.decodeAuth(authorizationHeader)[0]);
        List<Trade> allOffers = new ArrayList<>();
        allOffers.addAll(curUser.getOutboundOffers());
        allOffers.addAll(curUser.getInboundOffers());
        // add links
        for (Trade trade:allOffers) {
            for (Link link : generateTradeLinks(trade)) {
                trade.add(link);
            }
        }
        return allOffers;
    }

    // get logged-in user; trader and reply status from header, save as accepted or denied
    @PatchMapping(path = "/reply/{status}")
    public Trade replyTrade(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestParam String offer_id, @PathVariable String status) {
        AppUser curUser = userJpa.getByEmail(BLL.decodeAuth(authorizationHeader)[0]);
        Trade curTrade;
        try {
            curTrade = tradeJpa.getById(Integer.parseInt(offer_id));
        }catch (Exception e) {
            throw new IllegalArgumentException("Use a number");
        }
        if(curTrade.getReceiver().equals(curUser)) { //if the logged-in user is the receiver of the offer
            if(curTrade.getStatus() == Trade.Status.PENDING) { // if the trade hasn't been answered yet
                switch (status) {
                    case "accepted":
                        curTrade.setStatus(Trade.Status.ACCEPTED);
                        AppUser offerUser = curTrade.getOfferer();
                        AppUser receiveUser = curTrade.getReceiver();
                        List<VideoGame> offerGames = curTrade.getOfferedGames();
                        List<VideoGame> receiveGames = curTrade.getRequestedGames();

                        for(VideoGame offerGame : offerGames) {
                            offerGame.setOwner(receiveUser);
                            gameJpa.save(offerGame);
                        }

                        for(VideoGame receiveGame : receiveGames) {
                            receiveGame.setOwner(offerUser);
                            gameJpa.save(receiveGame);
                        }

                        userJpa.save(offerUser);
                        userJpa.save(receiveUser);
                        break;
                    case "rejected":
                        curTrade.setStatus(Trade.Status.REJECTED);
                        break;
                    default:
                        break;
                }
                tradeJpa.save(curTrade);
                for (Link link : generateTradeLinks(curTrade)) {
                    curTrade.add(link);
                }
            }
        }
        return curTrade;
    }

    @Transactional
    @PostMapping(path = "/createOffer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code= HttpStatus.CREATED)
    public Trade createOffer(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestBody JSONObject offer) {
        List<String> strOfferList = (List<String>) offer.get("offerList");
        List<String> strReceiveList = (List<String>) offer.get("receiveList");

        AppUser offerUser = userJpa.getByEmail(BLL.decodeAuth(authorizationHeader)[0]);
        AppUser receiveUser = userJpa.getByEmail(offer.getAsString("receiver"));

        List<VideoGame> offerList = bll.convertToGameList(strOfferList);
        List<VideoGame> receiveList = bll.convertToGameList(strReceiveList);
        if(!bll.userHasGames(offerUser, offerList) || !bll.userHasGames(receiveUser, receiveList)) {
            throw new IllegalArgumentException("The game lists don't match the users games");
        }
        Trade trade = new Trade(offerUser, offerList, receiveUser, receiveList);
        offerUser.addOfferOut(trade);
        receiveUser.addOfferIn(trade);
        tradeJpa.save(trade);
        userJpa.save(offerUser);
        userJpa.save(receiveUser);
        for (Link link : generateTradeLinks(trade)) {
            trade.add(link);
        }
        return trade;
    }

    private ArrayList<Link> generateTradeLinks(Trade trade) {
        ArrayList<Link> links = new ArrayList<>();
        links.add(Link.of((String.format("http://localhost:8080/trade/%s", trade.getId())), "self"));
        for (VideoGame offerGame : trade.getOfferedGames()) {
            links.add(Link.of((String.format("http://localhost:8080/games/%s", offerGame.getId())), "offeredVideoGames"));
        }
        for (VideoGame receiveGame : trade.getRequestedGames()) {
            links.add(Link.of((String.format("http://localhost:8080/games/%s", receiveGame.getId())), "receivedVideoGames"));
        }
        links.add(Link.of((String.format("http://localhost:8080/user/%s", trade.getOfferer().getId())), "offeringUser"));
        links.add(Link.of((String.format("http://localhost:8080/user/%s", trade.getReceiver().getId())), "receivingUser"));
        return links;
    }

    private ArrayList<Link> generateTradeLinks(int offerId, int offerUserId, int receiveUserId, List<VideoGame> offerList, List<VideoGame> receiveList) {
        ArrayList<Link> links = new ArrayList<>();
        links.add(Link.of((String.format("http://localhost:8080/trade/%s", offerId)), "self"));
        for (VideoGame offerGame : offerList) {
            links.add(Link.of((String.format("http://localhost:8080/games/%s", offerGame.getId())), "offeredVideoGames"));
        }
        for (VideoGame receiveGame : receiveList) {
            links.add(Link.of((String.format("http://localhost:8080/games/%s", receiveGame.getId())), "receivedVideoGames"));
        }
        links.add(Link.of((String.format("http://localhost:8080/user/%s", offerUserId)), "offeringUser"));
        links.add(Link.of((String.format("http://localhost:8080/user/%s", receiveUserId)), "receivingUser"));
        return links;
    }
}
