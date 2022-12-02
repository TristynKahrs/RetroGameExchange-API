package com.tkahrs.retroexchange.Controllers;

import com.tkahrs.retroexchange.Models.AppUser;
import com.tkahrs.retroexchange.Models.VideoGame;
import com.tkahrs.retroexchange.Repositories.AppUserJpaRepository;
import com.tkahrs.retroexchange.Repositories.TradeJpaRepository;
import com.tkahrs.retroexchange.Repositories.VideoGameJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BLL {
    @Autowired
    private VideoGameJpaRepository gameJpa;

    static String[] decodeAuth(String encodedString) {
        // Tristyn's baby
        encodedString = encodedString.substring(encodedString.indexOf(" ") + 1);
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        return decodedString.split(":", 2);
    }

    public boolean userHasGames(AppUser user, List<VideoGame> games){
        List<VideoGame> userGames = user.getGames();
        for(VideoGame game : games){
            if(!userGames.contains(game)){
                return false;
            }
        }
        return true;
    }

    public List<VideoGame> convertToGameList(List<String> strOfferList) {
        List<VideoGame> offerList = new ArrayList<>();
//        System.err.println(strOfferList);
        for (String game : strOfferList) {
//            System.err.println(game + " " + strOfferList.indexOf(game));
            offerList.add(gameJpa.getByGameName(game));
        }
//        System.err.println(offerList);
        return offerList;
    }
}
